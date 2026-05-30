package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
    LOGIN, REGISTER, DASHBOARD
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())
        
        // Seed initial mock data
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
        
        // Start simulated stock ticker updates
        startStockTicker()
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Logged-in User Info
    private val _loggedInUserPhone = MutableStateFlow<String?>(null)
    val loggedInUserPhone: StateFlow<String?> = _loggedInUserPhone.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _loggedInUserPhone
        .flatMapLatest { phone ->
            if (phone != null) repository.getUserFlow(phone) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Global Shares & Votes
    val shares: StateFlow<List<ShareEntity>> = repository.allShares
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val votes: StateFlow<List<VoteEntity>> = repository.allVotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userShares: StateFlow<List<UserShareEntity>> = _loggedInUserPhone
        .flatMapLatest { phone ->
            if (phone != null) repository.getUserSharesFlow(phone) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userVotes: StateFlow<List<UserVoteEntity>> = _loggedInUserPhone
        .flatMapLatest { phone ->
            if (phone != null) repository.getUserVotesFlow(phone) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback messages
    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _verificationMessage = MutableStateFlow<String?>(null)
    val verificationMessage: StateFlow<String?> = _verificationMessage.asStateFlow()

    // Stock price update job
    private var tickerJob: Job? = null

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
        _authError.value = null
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun clearVerificationMessage() {
        _verificationMessage.value = null
    }

    // Authenticated API actions
    fun register(phone: String, name: String, passwordHash: String, depositAmount: Double, onFinished: () -> Unit) {
        if (phone.isBlank() || name.isBlank() || passwordHash.isBlank()) {
            _authError.value = "All fields are required."
            return
        }
        if (depositAmount <= 0) {
            _authError.value = "An initial deposit amount is required."
            return
        }

        viewModelScope.launch {
            val existing = repository.getUser(phone)
            if (existing != null) {
                _authError.value = "An account with this number already exists."
                return@launch
            }

            // Generate random 5 digit code
            val code = (10000..99999).random().toString()
            val newUser = UserEntity(
                phoneNumber = phone,
                name = name,
                passwordHash = passwordHash,
                hasDeposited = true,
                depositAmount = depositAmount,
                isVerified = false,
                balance = 0.0, // starts at 0 until verified
                verificationCode = code
            )
            repository.insertUser(newUser)
            _loggedInUserPhone.value = phone
            _currentScreen.value = AppScreen.DASHBOARD
            _actionMessage.value = "Account created. Initiate mobile verification to activate your $depositAmount balance!"
            onFinished()
        }
    }

    fun login(phone: String, passwordHash: String, onFinished: () -> Unit) {
        if (phone.isBlank() || passwordHash.isBlank()) {
            _authError.value = "Credentials cannot be empty."
            return
        }
        viewModelScope.launch {
            val user = repository.getUser(phone)
            if (user == null) {
                _authError.value = "Account not found. Please register first."
                return@launch
            }
            if (user.passwordHash != passwordHash) {
                _authError.value = "Incorrect password."
                return@launch
            }

            _loggedInUserPhone.value = phone
            _currentScreen.value = AppScreen.DASHBOARD
            _actionMessage.value = "Successfully logged in."
            onFinished()
        }
    }

    fun verifyCode(enteredCode: String) {
        val phone = _loggedInUserPhone.value ?: return
        viewModelScope.launch {
            val user = repository.getUser(phone) ?: return@launch
            if (user.isVerified) {
                _verificationMessage.value = "Already verified."
                return@launch
            }

            if (user.verificationCode == enteredCode.trim()) {
                val updated = user.copy(
                    isVerified = true,
                    balance = user.depositAmount // Transfer fixed deposit to trading balance
                )
                repository.updateUser(updated)
                _actionMessage.value = "Verification Successful! $${user.depositAmount} deposited. You can now buy and sell shares."
                _verificationMessage.value = "SUCCESS"
            } else {
                _verificationMessage.value = "Invalid Verification Code. Please try again."
            }
        }
    }

    fun castVote(voteId: Int, choice: String) {
        val phone = _loggedInUserPhone.value ?: return
        viewModelScope.launch {
            val success = repository.castVote(phone, voteId, choice)
            if (success) {
                _actionMessage.value = "Your vote on proposal #$voteId has been tracked securely!"
            } else {
                _actionMessage.value = "You have already voted on this proposal."
            }
        }
    }

    fun buyShare(symbol: String, quantity: Int, pricePerShare: Double) {
        val phone = _loggedInUserPhone.value ?: return
        if (quantity <= 0) {
            _actionMessage.value = "Quantity must be at least 1."
            return
        }
        viewModelScope.launch {
            val result = repository.buyShare(phone, symbol, quantity, pricePerShare)
            _actionMessage.value = result
        }
    }

    fun sellShare(symbol: String, quantity: Int, pricePerShare: Double) {
        val phone = _loggedInUserPhone.value ?: return
        if (quantity <= 0) {
            _actionMessage.value = "Quantity must be at least 1."
            return
        }
        viewModelScope.launch {
            val result = repository.sellShare(phone, symbol, quantity, pricePerShare)
            _actionMessage.value = result
        }
    }

    fun logout() {
        _loggedInUserPhone.value = null
        _currentScreen.value = AppScreen.LOGIN
        _actionMessage.value = "Logged out successfully."
    }

    private fun startStockTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(12000) // update stock prices every 12 seconds
                val currentShares = repository.allShares.first()
                if (currentShares.isNotEmpty()) {
                    val db = AppDatabase.getDatabase(getApplication())
                    val updated = currentShares.map { share ->
                        val changePercent = Random.nextDouble(-1.5, 1.8)
                        val factor = 1.0 + (changePercent / 100.0)
                        val newPrice = Math.round(share.price * factor * 100.0) / 100.0
                        share.copy(price = newPrice, priceChangePercent = changePercent)
                    }
                    db.appDao().insertShares(updated)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}
