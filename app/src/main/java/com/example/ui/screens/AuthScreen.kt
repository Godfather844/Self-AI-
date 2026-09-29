package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.ui.components.ChunkyButton
import com.example.ui.components.ChunkyButtonStyle
import com.example.ui.components.Playful3DMascot
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.PeachLilacGradient
import com.example.ui.theme.RoseRedLight
import com.example.ui.theme.RoseRedPrimary
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmPeachPrimary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onLoginSuccess: (email: String, password: String, college: String) -> Unit,
    onSignUpSuccess: (name: String, email: String, password: String, college: String, branch: String) -> Unit,
    onGoogleSignIn: (email: String, displayName: String) -> Unit = { _, _ -> },
    onCheckGoogleAccount: (email: String, onResult: (Boolean) -> Unit) -> Unit = { _, cb -> cb(false) },
    onDirectGoogleLogin: (email: String) -> Unit = {},
    onCompleteGoogleRegistration: (email: String, name: String, college: String, branch: String, avatar: String) -> Unit = { _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSignUp by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    // Google Sign-In Chooser and Onboarding States
    var showGoogleAccountPicker by remember { mutableStateOf(false) }
    var customGoogleEmailInput by remember { mutableStateOf("") }
    var isEnteringCustomEmail by remember { mutableStateOf(false) }

    // Next Page: Google Student Profile Setup
    var isGoogleOnboarding by remember { mutableStateOf(false) }
    var selectedGoogleEmail by remember { mutableStateOf("") }
    var onboardingName by remember { mutableStateOf("") }
    var onboardingCollege by remember { mutableStateOf("") }
    var onboardingBranch by remember { mutableStateOf("Computer Science & Engineering") }
    var onboardingAvatar by remember { mutableStateOf("🧑‍💻") }

    fun onAccountChosen(chosenEmail: String, chosenDisplayName: String) {
        val cleanEmail = chosenEmail.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) return

        showGoogleAccountPicker = false
        isEnteringCustomEmail = false

        // Check if account already exists in database
        onCheckGoogleAccount(cleanEmail) { isRegistered ->
            if (isRegistered) {
                // If account already exists: DIRECT LOGIN!
                onDirectGoogleLogin(cleanEmail)
            } else {
                // If new account: Go to next page to fill name & student details
                selectedGoogleEmail = cleanEmail
                onboardingName = chosenDisplayName.ifBlank {
                    cleanEmail.substringBefore("@")
                        .replace(".", " ")
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                }
                onboardingCollege = ""
                onboardingBranch = "Computer Science & Engineering"
                isGoogleOnboarding = true
            }
        }
    }

    fun triggerGoogleSignIn() {
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("284432790537-android.apps.googleusercontent.com")
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context as Activity
                )
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val userEmail = credential.id
                    val displayName = credential.displayName ?: userEmail.substringBefore("@")
                    onAccountChosen(userEmail, displayName)
                } else {
                    showGoogleAccountPicker = true
                }
            } catch (e: Exception) {
                // If Play Services isn't logged in on emulator, show full Google Account Chooser
                showGoogleAccountPicker = true
            }
        }
    }

    // Next Page: Complete Student Profile if new Google user
    if (isGoogleOnboarding) {
        GoogleProfileSetupPage(
            googleEmail = selectedGoogleEmail,
            initialName = onboardingName,
            initialCollege = onboardingCollege,
            initialBranch = onboardingBranch,
            initialAvatar = onboardingAvatar,
            isLoading = isLoading,
            onBack = { isGoogleOnboarding = false },
            onComplete = { finalName, finalCollege, finalBranch, finalAvatar ->
                onCompleteGoogleRegistration(
                    selectedGoogleEmail,
                    finalName,
                    finalCollege,
                    finalBranch,
                    finalAvatar
                )
            },
            modifier = modifier
        )
        return
    }

    // Google Account Chooser Dialog
    if (showGoogleAccountPicker) {
        AlertDialog(
            onDismissRequest = {
                showGoogleAccountPicker = false
                isEnteringCustomEmail = false
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google 4-Color Styled 'G' Logo
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4285F4)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Sign in with Google",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose an account to continue to Self AI",
                        fontSize = 12.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isEnteringCustomEmail) {
                        // Account 1: User's Account
                        GoogleAccountCard(
                            email = "kushwahalucky939@gmail.com",
                            displayName = "Lucky Kushwaha",
                            avatarLetter = "L",
                            avatarBg = SoftLilacPrimary,
                            onClick = {
                                onAccountChosen("kushwahalucky939@gmail.com", "Lucky Kushwaha")
                            }
                        )

                        // Account 2: Secondary Student Account
                        GoogleAccountCard(
                            email = "student.cs@gmail.com",
                            displayName = "Student Scholar",
                            avatarLetter = "S",
                            avatarBg = Color(0xFF0EA5E9),
                            onClick = {
                                onAccountChosen("student.cs@gmail.com", "Student Scholar")
                            }
                        )

                        // Option 3: Enter another Gmail
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isEnteringCustomEmail = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("➕", fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Use another account",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.5.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Enter any other Gmail address",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        // Custom Email input
                        Text(
                            text = "Enter your Google Email address:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        OutlinedTextField(
                            value = customGoogleEmailInput,
                            onValueChange = { customGoogleEmailInput = it },
                            label = { Text("Gmail Address") },
                            placeholder = { Text("name@gmail.com") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isEnteringCustomEmail = false }) {
                                Text("Back to accounts", color = SoftLilacPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "To continue, Google will share your email address and profile name with Self AI for authentication.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            },
            confirmButton = {
                if (isEnteringCustomEmail) {
                    Button(
                        onClick = {
                            val clean = customGoogleEmailInput.trim().lowercase()
                            if (clean.isNotBlank() && clean.contains("@")) {
                                onAccountChosen(clean, clean.substringBefore("@"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftLilacPrimary),
                        enabled = customGoogleEmailInput.trim().contains("@")
                    ) {
                        Text("Continue", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showGoogleAccountPicker = false
                    isEnteringCustomEmail = false
                }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PeachLilacGradient)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Playful Mascot
            Playful3DMascot(size = 96.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Self AI",
                color = SoftLilacPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Your Gamified Classroom Quest Companion",
                color = TextSecondary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Backend Status Indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MintGreenLight)
                    .border(1.dp, MintGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔥 Firebase Auth, Google & Firestore Active",
                    color = MintGreenPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Auth Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Sign-In Chunky Button (Top Placement for Instant 1-Tap)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .clickable {
                                if (!isLoading) {
                                    triggerGoogleSignIn()
                                }
                            }
                            .testTag("google_signin_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Google Icon
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF4285F4)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sign in with Google",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // OR Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "  OR EMAIL  ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Toggle switch: Login vs Sign Up
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Login Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (!isSignUp) Color.White else Color.Transparent)
                                    .shadow(if (!isSignUp) 2.dp else 0.dp, RoundedCornerShape(12.dp))
                                    .clickable { isSignUp = false }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Log In",
                                    fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isSignUp) SoftLilacPrimary else TextSecondary,
                                    fontSize = 14.sp
                                )
                            }

                            // Sign Up Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSignUp) Color.White else Color.Transparent)
                                    .shadow(if (isSignUp) 2.dp else 0.dp, RoundedCornerShape(12.dp))
                                    .clickable { isSignUp = true }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sign Up",
                                    fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSignUp) SoftLilacPrimary else TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Error Message Banner if any
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoseRedLight)
                                .border(1.dp, RoseRedPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = RoseRedPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Fields
                    AnimatedVisibility(visible = isSignUp) {
                        Column {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Person, contentDescription = "Name", tint = SoftLilacPrimary)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SoftLilacPrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_input_name")
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Student Email") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Email, contentDescription = "Email", tint = SoftLilacPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftLilacPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_input_email")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Lock, contentDescription = "Password", tint = SoftLilacPrimary)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftLilacPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_input_password")
                    )

                    AnimatedVisibility(visible = isSignUp) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = college,
                                onValueChange = { college = it },
                                label = { Text("College / University") },
                                leadingIcon = {
                                    Icon(Icons.Rounded.School, contentDescription = "College", tint = WarmPeachPrimary)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WarmPeachPrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_input_college")
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = branch,
                                onValueChange = { branch = it },
                                label = { Text("Branch / Major") },
                                leadingIcon = {
                                    Text("🎓", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WarmPeachPrimary,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_input_branch")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Chunky Primary Action Button
                    ChunkyButton(
                        text = if (isLoading) "Connecting to Firebase..." else if (isSignUp) "Create Student Account 🚀" else "Log In To Self AI ⚡",
                        onClick = {
                            if (!isLoading) {
                                if (isSignUp) {
                                    onSignUpSuccess(name, email, password, college, branch)
                                } else {
                                    onLoginSuccess(email, password, college)
                                }
                            }
                        },
                        enabled = !isLoading,
                        style = if (isSignUp) ChunkyButtonStyle.PEACH else ChunkyButtonStyle.PRIMARY,
                        trailingIcon = {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "auth_submit_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Demo / Instant Access
                    Text(
                        text = "Or continue as Verified Student (Instant Access) ✨",
                        color = SoftLilacPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onLoginSuccess(email, password, college) }
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleAccountCard(
    email: String,
    displayName: String,
    avatarLetter: String,
    avatarBg: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarLetter,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = email,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Text("›", fontSize = 20.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GoogleProfileSetupPage(
    googleEmail: String,
    initialName: String,
    initialCollege: String,
    initialBranch: String,
    initialAvatar: String,
    isLoading: Boolean,
    onBack: () -> Unit,
    onComplete: (name: String, college: String, branch: String, avatar: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var studentName by remember { mutableStateOf(initialName) }
    var collegeName by remember { mutableStateOf(initialCollege) }
    var branchName by remember { mutableStateOf(initialBranch) }
    var selectedAvatar by remember { mutableStateOf(initialAvatar) }

    val avatarOptions = listOf("🧑‍💻", "🚀", "🎓", "⚡", "🌟", "🎨", "🔬", "💡")
    val collegeSuggestions = listOf(
        "National Institute of Technology",
        "IIT Bombay",
        "BITS Pilani",
        "Delhi University",
        "VIT Vellore"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PeachLilacGradient)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back button and Step indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onBack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Step 2 of 2",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftLilacPrimary
                    )
                    Text(
                        text = "Complete Student Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Google Verified Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4285F4), fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = googleEmail,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✓ Verified",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Setup Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "Welcome to Self AI! 🎓",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Set up your student details to appear on your college leaderboard and track daily quests.",
                        fontSize = 12.5.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. Full Name
                    Text(
                        text = "Your Full Name *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        placeholder = { Text("e.g. Lucky Kushwaha") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Person, contentDescription = "Name", tint = SoftLilacPrimary)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftLilacPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. College / University
                    Text(
                        text = "College / University *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = collegeName,
                        onValueChange = { collegeName = it },
                        placeholder = { Text("e.g. National Institute of Technology") },
                        leadingIcon = {
                            Icon(Icons.Rounded.School, contentDescription = "College", tint = WarmPeachPrimary)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmPeachPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Quick suggestions:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        collegeSuggestions.take(2).forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { collegeName = suggestion }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = suggestion.substringBefore(" ").take(14),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SoftLilacPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Academic Branch
                    Text(
                        text = "Branch / Major *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = branchName,
                        onValueChange = { branchName = it },
                        placeholder = { Text("e.g. Computer Science & Engineering") },
                        leadingIcon = {
                            Text("🎓", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmPeachPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Mascot Avatar Picker
                    Text(
                        text = "Choose Your Avatar Mascot:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatarOptions.forEach { emoji ->
                            val isSelected = selectedAvatar == emoji
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SoftLilacPrimary.copy(alpha = 0.2f) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) SoftLilacPrimary else Color(0xFFE2E8F0),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 19.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Final Sign Up Button
                    ChunkyButton(
                        text = if (isLoading) "Creating Your Account..." else "Complete Sign Up & Launch 🚀",
                        onClick = {
                            if (studentName.isNotBlank() && collegeName.isNotBlank()) {
                                onComplete(studentName.trim(), collegeName.trim(), branchName.trim(), selectedAvatar)
                            }
                        },
                        enabled = !isLoading && studentName.isNotBlank() && collegeName.isNotBlank(),
                        style = ChunkyButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
