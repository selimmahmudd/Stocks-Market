package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ShareEntity
import com.example.data.VoteEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()

    // Handle feedback messages as toasts
    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearActionMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate 900
                        Color(0xFF020617)  // Slate 950
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (currentScreen) {
            AppScreen.LOGIN -> LoginScreen(viewModel)
            AppScreen.REGISTER -> RegisterScreen(viewModel)
            AppScreen.DASHBOARD -> DashboardScreen(viewModel)
        }
    }
}

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo / Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF10B981)) // Emerald 500
                .testTag("login_logo_box"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Share Market Board Logo",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Share Market Board",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.SansSerif
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Secure Daily Voting & Share Trade Station",
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF94A3B8) // Slate 400
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message banner
        AnimatedVisibility(
            visible = authError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            authError?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFF87171)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFF87171)),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Number", tint = Color(0xFF10B981)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_phone_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFF10B981)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(phone, password) {
                    phone = ""
                    password = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Sign In Securely",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.setScreen(AppScreen.REGISTER) },
            modifier = Modifier.testTag("go_to_register_button")
        ) {
            Text(
                "Don't have an account? Sign Up Now",
                color = Color(0xFF10B981),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun RegisterScreen(viewModel: MainViewModel) {
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var depositText by remember { mutableStateOf("500.00") } // Fixed deposit
    val authError by viewModel.authError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Secure trading access requires a one-time fixed registration deposit. Complete instant phone verification afterwards.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF94A3B8)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        // Error message banner
        AnimatedVisibility(
            visible = authError != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            authError?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFF87171)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFF87171)),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Number", tint = Color(0xFF10B981)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_phone_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = Color(0xFF10B981)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFF10B981)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_password_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = depositText,
            onValueChange = { depositText = it },
            label = { Text("Fixed Registration Deposit ($)", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Fixed Deposit", tint = Color(0xFFFFB300)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_deposit_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color(0xFF334155)
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val amt = depositText.toDoubleOrNull() ?: 500.0
                viewModel.register(phone, name, password, amt) {
                    phone = ""
                    name = ""
                    password = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("register_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Register & Setup Deposit",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.setScreen(AppScreen.LOGIN) },
            modifier = Modifier.testTag("go_to_login_button")
        ) {
            Text(
                "Already have an account? Sign In",
                color = Color(0xFF10B981),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val shares by viewModel.shares.collectAsState()
    val votes by viewModel.votes.collectAsState()
    val userShares by viewModel.userShares.collectAsState()
    val userVotes by viewModel.userVotes.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Daily Board Votes, 1: Live Shares & Portfolio, 2: Security Lock
    var verificationCodeInput by remember { mutableStateOf("") }
    val verificationMsg by viewModel.verificationMessage.collectAsState()

    LaunchedEffect(verificationMsg) {
        if (verificationMsg == "SUCCESS") {
            verificationCodeInput = ""
            viewModel.clearVerificationMessage()
        }
    }

    currentUser?.let { user ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate 800
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome, ${user.name}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "🔒 Phone ID: ${user.phoneNumber}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }

                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = Color(0xFF334155))

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE PORTFOLIO POOL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (user.isVerified) "$${String.format("%.2f", user.balance)}" else "PENDING VERIFICATION",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (user.isVerified) Color(0xFF10B981) else Color(0xFFFBBF24)
                                )
                            )
                        }

                        // Verification Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (user.isVerified) Color(0xFF10B981).copy(alpha = 0.2f)
                                    else Color(0xFFEF4444).copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (user.isVerified) "Verified Safe" else "Action Needed",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.isVerified) Color(0xFF10B981) else Color(0xFFF87171)
                                )
                            )
                        }
                    }
                }
            }

            // Tabs Bar
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF10B981)
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    modifier = Modifier.testTag("tab_daily_votes")
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Daily Polls",
                            tint = if (activeTab == 0) Color(0xFF10B981) else Color(0xFF94A3B8)
                        )
                        Text(
                            "Daily Votes",
                            color = if (activeTab == 0) Color.White else Color(0xFF94A3B8),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    modifier = Modifier.testTag("tab_live_shares")
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Shares Board",
                            tint = if (activeTab == 1) Color(0xFF10B981) else Color(0xFF94A3B8)
                        )
                        Text(
                            "Shares Trade",
                            color = if (activeTab == 1) Color.White else Color(0xFF94A3B8),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    modifier = Modifier.testTag("tab_security")
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security Status",
                            tint = if (activeTab == 2) Color(0xFF10B981) else Color(0xFF94A3B8)
                        )
                        Text(
                            "Security",
                            color = if (activeTab == 2) Color.White else Color(0xFF94A3B8),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                when (activeTab) {
                    0 -> {
                        // Daily Board Votes (Accessible in both states!)
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Today's Board Votes",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "Live Feed",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (votes.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No daily voting board proposal is live.", color = Color(0xFF94A3B8))
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(votes) { voteTopic ->
                                        val votedObj = userVotes.any { it.voteId == voteTopic.id }
                                        val userChoice = userVotes.firstOrNull { it.voteId == voteTopic.id }?.choice
                                        VoteCard(
                                            vote = voteTopic,
                                            alreadyVoted = votedObj,
                                            userChoice = userChoice,
                                            onVoteCast = { choice ->
                                                viewModel.castVote(voteTopic.id, choice)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Live Shares & Portfolio
                        if (!user.isVerified) {
                            // If unverified, they are LOCKED. Show clean verification wall!
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked Feature",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(56.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    "Feature Locked",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Text(
                                    "Trade and portfolio access is locked until you complete verification. Your registration deposit of $${String.format("%.2f", user.depositAmount)} is secure.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Instant Verification Check",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            "Enter 5-digit security code received at registration for: +${user.phoneNumber.filter { it.isDigit() }}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Simulated hint box so user can copy correct code easily!
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .background(Color(0xFF10B981).copy(alpha = 0.08f))
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Info, contentDescription = "Verification helper", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Demo Verification Security Key: ${user.verificationCode}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = Color(0xFF10B981),
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                            value = verificationCodeInput,
                                            onValueChange = { verificationCodeInput = it },
                                            placeholder = { Text("Enter 5-digit verification key", color = Color(0xFF64748B)) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("otp_code_input"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = Color(0xFF10B981),
                                                unfocusedBorderColor = Color(0xFF334155)
                                            ),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        if (verificationMsg != null && verificationMsg != "SUCCESS") {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = verificationMsg ?: "",
                                                color = Color(0xFFEF4444),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                viewModel.verifyCode(verificationCodeInput)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .testTag("otp_verify_submit"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Complete Free Mobile Verification")
                                        }
                                    }
                                }
                            }
                        } else {
                            // IF VERIFIED: Show Shares and Portfolio!
                            var showPortfolioOnly by remember { mutableStateOf(false) }

                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (showPortfolioOnly) "My Securities Portfolio" else "Market Share Board",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "Portfolio",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Switch(
                                            checked = showPortfolioOnly,
                                            onCheckedChange = { showPortfolioOnly = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFF131C2E),
                                                checkedTrackColor = Color(0xFF10B981),
                                                uncheckedThumbColor = Color(0xFF64748B),
                                                uncheckedTrackColor = Color(0xFF334155)
                                            ),
                                            modifier = Modifier.testTag("portfolio_toggle_switch")
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (showPortfolioOnly) {
                                    if (userShares.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("You do not own any market shares yet.", color = Color(0xFF94A3B8))
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(userShares) { holding ->
                                                val shareDetail = shares.firstOrNull { it.symbol == holding.symbol }
                                                PortfolioCard(
                                                    symbol = holding.symbol,
                                                    quantity = holding.quantity,
                                                    currentPrice = shareDetail?.price ?: 0.0,
                                                    onSellClicked = { qty ->
                                                        shareDetail?.let {
                                                            viewModel.sellShare(it.symbol, qty, it.price)
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(shares) { share ->
                                            val holding = userShares.firstOrNull { it.symbol == share.symbol }?.quantity ?: 0
                                            ShareCard(
                                                share = share,
                                                ownedShares = holding,
                                                onBuyClicked = { qty ->
                                                    viewModel.buyShare(share.symbol, qty, share.price)
                                                },
                                                onSellClicked = { qty ->
                                                    viewModel.sellShare(share.symbol, qty, share.price)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Detailed Security tab demonstrating requirements
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Shield Guard",
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Transaction & Account Guard",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "1. Mobile Identity Locking",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = Color(0xFF10B981),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Your registered phone identification (+${user.phoneNumber.filter { it.isDigit() }}) is coupled directly with your secure trading locker. Trading features are locked unless OTP key is verified.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)),
                                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                        )

                                        HorizontalDivider(color = Color(0xFF334155))

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "2. Anti-Attack Protection (Limit Active)",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = Color(0xFFEF4444),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Repeated deposits and withdrawals are strictly locked for maximum compliance and to prevent malicious botting behavior. Only the initial secure registration deposit of $${String.format("%.2f", user.depositAmount)} has been cleared and authorized.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            "Cleared Registration Funds Log",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Success",
                                                    tint = Color(0xFF10B981)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        "Registration Deposit",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                    Text(
                                                        "One-Time Secure Setup",
                                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                                                    )
                                                }
                                            }

                                            Text(
                                                "+$${String.format("%.2f", user.depositAmount)}",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = Color(0xFF10B981),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        HorizontalDivider(color = Color(0xFF1E293B))

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "No features",
                                                    tint = Color(0xFFEF4444)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        "Recurring Deposit Limit",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color.White,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    )
                                                    Text(
                                                        "Locked - Multi-deposit bypass block",
                                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFEF4444))
                                                    )
                                                }
                                            }

                                            Text(
                                                "DISABLED",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFFEF4444),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoteCard(
    vote: VoteEntity,
    alreadyVoted: Boolean,
    userChoice: String?,
    onVoteCast: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DAILY MARKET PROPOSAL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = vote.question,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            val totalVotes = vote.yesVotes + vote.noVotes
            val yesPercent = if (totalVotes > 0) (vote.yesVotes.toFloat() / totalVotes.toFloat() * 100).toInt() else 0
            val noPercent = if (totalVotes > 0) (vote.noVotes.toFloat() / totalVotes.toFloat() * 100).toInt() else 0

            if (alreadyVoted) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Your vote: $userChoice",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    )

                    // Progress bar for YES
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Yes", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
                            Text("$yesPercent% (${vote.yesVotes})", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
                        }
                        LinearProgressIndicator(
                            progress = { if (totalVotes > 0) vote.yesVotes.toFloat() / totalVotes.toFloat() else 0.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF334155)
                        )
                    }

                    // Progress bar for NO
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("No", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
                            Text("$noPercent% (${vote.noVotes})", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
                        }
                        LinearProgressIndicator(
                            progress = { if (totalVotes > 0) vote.noVotes.toFloat() / totalVotes.toFloat() else 0.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFEF4444),
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onVoteCast("YES") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vote_yes_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981).copy(alpha = 0.15f)),
                        border = borderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Text("VOTE YES", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981)))
                    }

                    Button(
                        onClick = { onVoteCast("NO") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("vote_no_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
                        border = borderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Text("VOTE NO", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)))
                    }
                }
            }
        }
    }
}

@Composable
fun ShareCard(
    share: ShareEntity,
    ownedShares: Int,
    onBuyClicked: (Int) -> Unit,
    onSellClicked: (Int) -> Unit
) {
    var isOperating by remember { mutableStateOf(false) }
    var actionQty by remember { mutableStateOf("1") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = share.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = share.symbol,
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF94A3B8))
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", share.price)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    val isPositive = share.priceChangePercent >= 0
                    Text(
                        text = (if (isPositive) "+" else "") + String.format("%.2f", share.priceChangePercent) + "%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Owned: $ownedShares | Value: $${String.format("%.2f", ownedShares * share.price)}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
                )

                Button(
                    onClick = { isOperating = !isOperating },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("trade_panel_btn_${share.symbol}")
                ) {
                    Text("Trade")
                }
            }

            AnimatedVisibility(visible = isOperating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(color = Color(0xFF334155))

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = actionQty,
                            onValueChange = { actionQty = it },
                            label = { Text("Quantity", color = Color(0xFF94A3B8)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("qty_input_${share.symbol}"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF10B981),
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                val qtyVal = actionQty.toIntOrNull() ?: 1
                                onBuyClicked(qtyVal)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("buy_btn_${share.symbol}")
                        ) {
                            Text("BUY")
                        }

                        Button(
                            onClick = {
                                val qtyVal = actionQty.toIntOrNull() ?: 1
                                onSellClicked(qtyVal)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sell_btn_${share.symbol}")
                        ) {
                            Text("SELL")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PortfolioCard(
    symbol: String,
    quantity: Int,
    currentPrice: Double,
    onSellClicked: (Int) -> Unit
) {
    var sellQty by remember { mutableStateOf("1") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Holding: $quantity units",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF10B981))
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Market Price",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                    Text(
                        text = "$${String.format("%.2f", currentPrice)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(color = Color(0xFF334155))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = sellQty,
                    onValueChange = { sellQty = it },
                    label = { Text("Sell Quantity", color = Color(0xFF94A3B8)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("portfolio_sell_qty_${symbol}"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                        val valQty = sellQty.toIntOrNull() ?: 1
                        onSellClicked(valQty)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("portfolio_sell_btn_${symbol}"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Instant Sell")
                }
            }
        }
    }
}

// Utility extension to make stroke styling completely standard & clean
fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
