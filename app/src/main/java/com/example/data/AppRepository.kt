package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val appDao: AppDao) {

    val allShares: Flow<List<ShareEntity>> = appDao.getAllSharesFlow()
    val allVotes: Flow<List<VoteEntity>> = appDao.getAllVotesFlow()

    fun getUserFlow(phoneNumber: String): Flow<UserEntity?> = appDao.getUserFlow(phoneNumber)
    fun getUserSharesFlow(phoneNumber: String): Flow<List<UserShareEntity>> = appDao.getUserSharesFlow(phoneNumber)
    fun getUserVotesFlow(phoneNumber: String): Flow<List<UserVoteEntity>> = appDao.getUserVotesFlow(phoneNumber)

    suspend fun getUser(phoneNumber: String): UserEntity? = appDao.getUser(phoneNumber)

    suspend fun insertUser(user: UserEntity) = appDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = appDao.updateUser(user)

    suspend fun seedInitialDataIfNeeded() {
        // Seed Shares if empty
        val currentShares = allShares.first()
        if (currentShares.isEmpty()) {
            val initialShares = listOf(
                ShareEntity("AAPL", "Apple Inc.", 175.50, 1.2),
                ShareEntity("TSLA", "Tesla Inc.", 210.00, -2.4),
                ShareEntity("NVDA", "NVIDIA Corp.", 450.25, 3.8),
                ShareEntity("AMZN", "Amazon.com Inc.", 145.10, 0.5),
                ShareEntity("MSFT", "Microsoft Corp.", 370.80, -0.2)
            )
            appDao.insertShares(initialShares)
        }

        // Seed Votes if empty
        val currentVotes = allVotes.first()
        if (currentVotes.isEmpty()) {
            val initialVotes = listOf(
                VoteEntity(1, "Will Apple (AAPL) stock close higher than $180 today?", 142, 89),
                VoteEntity(2, "Is Tesla (TSLA) currently undervalued at $210 per share?", 94, 112),
                VoteEntity(3, "Will NVIDIA (NVDA) beat its revenue standard targets in Q2?", 208, 47),
                VoteEntity(4, "Will interest rates drop in the next upcoming regulatory session?", 105, 120),
                VoteEntity(5, "Is Amazon (AMZN) a safer buy today than Microsoft (MSFT)?", 76, 95)
            )
            appDao.insertVotes(initialVotes)
        }
    }

    suspend fun castVote(phoneNumber: String, voteId: Int, choice: String): Boolean {
        // Check if user already voted
        val existingVote = appDao.getUserVote(phoneNumber, voteId)
        if (existingVote != null) {
            return false // Already voted
        }

        // Get the vote topic
        val voteTopic = appDao.getVote(voteId) ?: return false

        // Update counts
        val updatedTopic = if (choice == "YES") {
            voteTopic.copy(yesVotes = voteTopic.yesVotes + 1)
        } else {
            voteTopic.copy(noVotes = voteTopic.noVotes + 1)
        }

        appDao.updateVote(updatedTopic)
        appDao.insertUserVote(UserVoteEntity(phoneNumber, voteId, choice))
        return true
    }

    suspend fun buyShare(phoneNumber: String, symbol: String, quantity: Int, pricePerShare: Double): String {
        val user = appDao.getUser(phoneNumber) ?: return "User not found."
        
        // Check if unverified
        if (!user.isVerified) {
            return "Cannot buy shares. Your account is not verified yet. Please complete regional mobile verification."
        }

        val totalCost = pricePerShare * quantity
        if (user.balance < totalCost) {
            return "Insufficient balance. Cost: $${String.format("%.2f", totalCost)}, Balance: $${String.format("%.2f", user.balance)}"
        }

        // Deduct balance
        val updatedUser = user.copy(balance = user.balance - totalCost)
        appDao.updateUser(updatedUser)

        // Add user holding
        val existingHolding = appDao.getUserShare(phoneNumber, symbol)
        if (existingHolding != null) {
            appDao.insertUserShare(existingHolding.copy(quantity = existingHolding.quantity + quantity))
        } else {
            appDao.insertUserShare(UserShareEntity(phoneNumber, symbol, quantity))
        }

        return "Successfully bought $quantity share(s) of $symbol!"
    }

    suspend fun sellShare(phoneNumber: String, symbol: String, quantity: Int, pricePerShare: Double): String {
        val user = appDao.getUser(phoneNumber) ?: return "User not found."

        // Check if unverified
        if (!user.isVerified) {
            return "Cannot sell shares. Your account is not verified yet. Please complete mobile verification."
        }

        val existingHolding = appDao.getUserShare(phoneNumber, symbol)
        if (existingHolding == null || existingHolding.quantity < quantity) {
            return "Insufficient shares owned. You own ${existingHolding?.quantity ?: 0} share(s) of $symbol."
        }

        val totalEarnings = pricePerShare * quantity

        // Add to balance
        val updatedUser = user.copy(balance = user.balance + totalEarnings)
        appDao.updateUser(updatedUser)

        // Decrease or delete holding
        if (existingHolding.quantity == quantity) {
            appDao.deleteUserShare(existingHolding)
        } else {
            appDao.insertUserShare(existingHolding.copy(quantity = existingHolding.quantity - quantity))
        }

        return "Successfully sold $quantity share(s) of $symbol!"
    }
}
