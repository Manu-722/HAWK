package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.StoreViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: StoreViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val authError by viewModel.authError.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val kbController = LocalSoftwareKeyboardController.current

    // Dialog for Simulated Google Sign In
    var showGoogleDialog by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("emmanuelmulongo46@gmail.com") }
    var googleName by remember { mutableStateOf("Emmanuel Mulongo") }
    var googlePhone by remember { mutableStateOf("") }
    var isCheckingGoogleSession by remember { mutableStateOf(false) }

    // Dialog for Forgot Password Flow
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var resetCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmNewPasswordInput by remember { mutableStateOf("") }
    var forgotStep by remember { mutableStateOf(1) } // 1: request code, 2: verify & reset
    var forgotMessage by remember { mutableStateOf<String?>(null) }
    var forgotError by remember { mutableStateOf<String?>(null) }

    if (currentUser != null) {
        LaunchedEffect(currentUser) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SleekSlate50)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(1.dp, SleekSlate100, RoundedCornerShape(24.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header - Hawk Logo & Title
            Image(
                painter = painterResource(id = R.drawable.hawk_logo),
                contentDescription = "Hawk Life Solutions Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, SleekSlate200, CircleShape)
                    .padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "HAWK INDUCTIONS",
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = SleekSlate950,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Premium Electromagnetic Heating",
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = SleekSlate500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
            )

            Text(
                text = if (isSignUp) "Create Account" else "Secure Sign In",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = SleekSlate950,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Input Fields - High Contrast & Text Color Fix
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email Address", fontSize = 14.sp, color = SleekSlate400) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = SleekSlate500) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = SleekSlate200,
                    focusedContainerColor = SleekSlate50,
                    unfocusedContainerColor = SleekSlate50
                )
            )

            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Full Name", fontSize = 14.sp, color = SleekSlate400) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = SleekSlate500) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = SleekSlate200,
                        focusedContainerColor = SleekSlate50,
                        unfocusedContainerColor = SleekSlate50
                    )
                )
            }

            // Password field with eye show/hide icon toggle
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    localError = null
                },
                placeholder = { Text("Password", fontSize = 14.sp, color = SleekSlate400) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = SleekSlate500) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility",
                            tint = SleekSlate600
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = SleekSlate200,
                    focusedContainerColor = SleekSlate50,
                    unfocusedContainerColor = SleekSlate50
                )
            )

            if (isSignUp) {
                // Confirm Password field with eye show/hide icon toggle
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        localError = null
                    },
                    placeholder = { Text("Confirm Password", fontSize = 14.sp, color = SleekSlate400) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password", tint = SleekSlate500) },
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Confirm Password Visibility",
                                tint = SleekSlate600
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = SleekSlate200,
                        focusedContainerColor = SleekSlate50,
                        unfocusedContainerColor = SleekSlate50
                    )
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Auth error display
            val errorToShow = localError ?: authError
            if (errorToShow != null) {
                Text(
                    text = errorToShow,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Submit Button - Sleek Style
            Button(
                onClick = {
                    kbController?.hide()
                    localError = null
                    if (isSignUp) {
                        if (email.isBlank() || name.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            localError = "Please fill in all fields."
                            return@Button
                        }
                        if (password != confirmPassword) {
                            localError = "Passwords do not match."
                            return@Button
                        }
                        // Validate password contains letters, numbers, and is at least 6 characters
                        val hasLetter = password.any { it.isLetter() }
                        val hasDigit = password.any { it.isDigit() }
                        if (!hasLetter || !hasDigit || password.length < 6) {
                            localError = "Password must be at least 6 characters and contain both letters and numbers."
                            return@Button
                        }
                        viewModel.signup(email, name, password) { success ->
                            if (success) onAuthSuccess()
                        }
                    } else {
                        if (email.isBlank() || password.isBlank()) {
                            localError = "Please fill in all fields."
                            return@Button
                        }
                        viewModel.login(email, password) { success ->
                            if (success) onAuthSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isSignUp) "Register Customer" else "Authorize Access",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider or "OR"
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SleekSlate100)
                Text(
                    text = "or",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color = SleekSlate400,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = SleekSlate100)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simulated Google Sign In Button - Sleek Style
            Button(
                onClick = { showGoogleDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, SleekSlate200, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G  ",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black
                    )
                    Text(
                        text = "Sign in with Google",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Switch Mode Link
            Text(
                text = if (isSignUp) "Already registered? Sign in" else "New to Hawk Inductions? Register",
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = SleekSlate700,
                modifier = Modifier
                    .clickable { 
                        isSignUp = !isSignUp 
                        localError = null
                    }
                    .padding(8.dp)
            )

            if (!isSignUp) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate500,
                    modifier = Modifier
                        .clickable {
                            forgotEmail = email
                            forgotError = null
                            forgotMessage = null
                            forgotStep = 1
                            showForgotDialog = true
                        }
                        .padding(4.dp)
                )
            }

            // Informative tag restricting admin registration
            if (isSignUp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(SleekSlate50, RoundedCornerShape(12.dp))
                        .border(1.dp, SleekSlate100, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = "Security",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin access restricted to key accounts.",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        color = SleekSlate700,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Google Sign-In Sheet Dialog Simulator - Sleek Style
    if (showGoogleDialog) {
        val detectedAccounts = listOf(
            "emmanuelmulongo46@gmail.com" to "Emmanuel Mulongo",
            "hawk.inductions.official@gmail.com" to "Hawk Operations",
            "admin1@induction.com" to "Hawk Administrator"
        )

        AlertDialog(
            onDismissRequest = { showGoogleDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (googleEmail.isNotBlank()) {
                            val finalName = googleName.ifBlank { googleEmail.substringBefore("@") }
                            email = googleEmail
                            name = finalName
                            viewModel.loginWithGoogleSimulated(googleEmail, finalName, googlePhone)
                            showGoogleDialog = false
                            onAuthSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text("Confirm Google Account & Sign In", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showGoogleDialog = false },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text("Cancel", color = SleekSlate500, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SleekSlate100),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = "Sign in with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate950
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose an account from Google to continue to Hawk Life Solutions:",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate600,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Account List Selector
                    detectedAccounts.forEach { (accEmail, accName) ->
                        val isSelected = (googleEmail == accEmail)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Black else SleekSlate200,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    googleEmail = accEmail
                                    googleName = accName
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SleekSlate100 else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.Black else SleekSlate300),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = accName.take(1).uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = accName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekSlate950
                                    )
                                    Text(
                                        text = accEmail,
                                        fontSize = 10.sp,
                                        color = SleekSlate500
                                    )
                                }

                                if (isSelected) {
                                    Text(
                                        text = "✓ Selected",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Phone Number Request to Complete Profile
                    Text(
                        text = "Complete Profile Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate950,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = googlePhone,
                        onValueChange = { googlePhone = it },
                        placeholder = { Text("Phone Number (+254 7XX XXX XXX)", fontSize = 12.sp, color = SleekSlate400) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = SleekSlate500) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            modifier = Modifier.border(1.dp, SleekSlate100, RoundedCornerShape(24.dp))
        )
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        forgotError = null
                        forgotMessage = null
                        if (forgotStep == 1) {
                            if (forgotEmail.isBlank()) {
                                forgotError = "Please enter your email address."
                                return@Button
                            }
                            viewModel.sendResetCode(forgotEmail) { success, msg ->
                                if (success) {
                                    forgotMessage = msg
                                    forgotStep = 2
                                } else {
                                    forgotError = msg
                                }
                            }
                        } else {
                            if (resetCodeInput.isBlank() || newPasswordInput.isBlank() || confirmNewPasswordInput.isBlank()) {
                                forgotError = "Please fill in all fields."
                                return@Button
                            }
                            if (newPasswordInput != confirmNewPasswordInput) {
                                forgotError = "Passwords do not match."
                                return@Button
                            }
                            // check complexity
                            val hasLetter = newPasswordInput.any { it.isLetter() }
                            val hasDigit = newPasswordInput.any { it.isDigit() }
                            if (!hasLetter || !hasDigit || newPasswordInput.length < 6) {
                                forgotError = "Password must be at least 6 characters and contain both letters and numbers."
                                return@Button
                            }
                            viewModel.verifyCodeAndResetPassword(resetCodeInput, newPasswordInput) { success, msg ->
                                if (success) {
                                    showForgotDialog = false
                                    // Successfully reset and auto-authenticated
                                    onAuthSuccess()
                                } else {
                                    forgotError = msg
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (forgotStep == 1) "Send Verification Code" else "Verify & Reset Password",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showForgotDialog = false },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text("Cancel", color = SleekSlate50, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Reset Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate950
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (forgotStep == 1) {
                            "Enter your registered email address below. We will send you a 6-digit temporary verification code to verify your identity. This code will expire after exactly 4 minutes."
                        } else {
                            "We've simulated sending a 6-digit verification code to $forgotEmail. Please check your simulated notifications, enter the code below and set your new password."
                        },
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate600,
                        modifier = Modifier.padding(bottom = 16.dp),
                        lineHeight = 16.sp
                    )

                    if (forgotStep == 1) {
                        OutlinedTextField(
                            value = forgotEmail,
                            onValueChange = { 
                                forgotEmail = it
                                forgotError = null
                            },
                            placeholder = { Text("Your Email Address", fontSize = 13.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = resetCodeInput,
                            onValueChange = { 
                                resetCodeInput = it
                                forgotError = null
                            },
                            placeholder = { Text("6-Digit Verification Code", fontSize = 13.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { 
                                newPasswordInput = it
                                forgotError = null
                            },
                            placeholder = { Text("New Secure Password", fontSize = 13.sp, color = SleekSlate400) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        OutlinedTextField(
                            value = confirmNewPasswordInput,
                            onValueChange = { 
                                confirmNewPasswordInput = it
                                forgotError = null
                            },
                            placeholder = { Text("Confirm New Password", fontSize = 13.sp, color = SleekSlate400) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )
                    }

                    if (forgotError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = forgotError ?: "",
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    if (forgotMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = forgotMessage ?: "",
                            color = Color(0xFF2E7D32), // Dark green
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            modifier = Modifier.border(1.dp, SleekSlate100, RoundedCornerShape(24.dp))
        )
    }
}
