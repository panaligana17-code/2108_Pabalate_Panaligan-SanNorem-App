package com.sannorem.app.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sannorem.app.ui.components.LogoThemeBlue
import com.sannorem.app.ui.components.SanNoremLogo
import kotlinx.coroutines.delay

/**
 * Login Screen implementing:
 *
 * Feature 3 – Splash Screen State:
 *   App opens with ONLY the logo perfectly centered on a solid blue (#0084D6) screen.
 *
 * Feature 4 – Transition Animation (after 900 ms delay):
 *   The centered logo smoothly slides upward to the upper-third portion of the screen.
 *
 * Feature 5 – Reveal State (Login Entry):
 *   As the logo moves up, the login form fades in and slides up from below:
 *   - Email + Password (with show/hide visibility toggle)
 *   - Forgot Password link
 *   - Prominent "Log In" primary button
 *   - Continue as Guest option
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    initialSplashDone: Boolean = false,
    onLoginSuccess: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {}
) {
    // Splash→Reveal transition flag
    var isSplashDone by remember { mutableStateOf(initialSplashDone) }
    if (!initialSplashDone) {
        LaunchedEffect(Unit) {
            delay(900)
            isSplashDone = true
        }
    }

    // Form state
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    fun validateAndLogin() {
        keyboardController?.hide()
        var valid = true
        if (email.isBlank()) {
            emailError = "Email is required"; valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Please enter a valid email address"; valid = false
        } else emailError = null
        if (password.isBlank()) {
            passwordError = "Password is required"; valid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"; valid = false
        } else passwordError = null
        if (valid) { isLoading = true; onLoginSuccess() }
    }

    // --- Animation values ---
    // Feature 4: logo offset – starts centered (splashCenterY depends on screen height),
    // then slides to topThirdOffset when isSplashDone = true.
    // We express relative offsets in dp using BoxWithConstraints.
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            // Feature 3: solid blue background
            .background(LogoThemeBlue)
            .imePadding()
    ) {
        val screenHeight = maxHeight

        // Target logo top position:
        //  - Splash (centered):  screenHeight/2 - 46dp (half logo height)
        //  - Revealed (upper third): 48dp from top
        val logoCenterY = screenHeight / 2 - 46.dp
        val logoUpperY = 48.dp

        val animatedLogoTopOffset by animateDpAsState(
            targetValue = if (isSplashDone) logoUpperY else logoCenterY,
            animationSpec = tween(durationMillis = 820, easing = FastOutSlowInEasing),
            label = "logoSlide"
        )

        // Form reveal alpha: 0→1 starting 350 ms into transition
        val formAlpha by animateFloatAsState(
            targetValue = if (isSplashDone) 1f else 0f,
            animationSpec = tween(durationMillis = 700, delayMillis = 350, easing = FastOutSlowInEasing),
            label = "formAlpha"
        )

        // Form slide-up: starts 80 dp below final position
        val formOffsetY by animateDpAsState(
            targetValue = if (isSplashDone) 0.dp else 80.dp,
            animationSpec = tween(durationMillis = 720, delayMillis = 300, easing = FastOutSlowInEasing),
            label = "formSlide"
        )

        // --- Layout ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer that pushes the logo to the animated position
            Spacer(modifier = Modifier.height(animatedLogoTopOffset))

            // ───── App Logo (Feature 3: only this visible during splash) ─────
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.size(92.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                    SanNoremLogo(modifier = Modifier.fillMaxSize())
                }
            }

            // App name + tagline – appear with form reveal
            AnimatedVisibility(
                visible = isSplashDone,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 350))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = "SanNorem",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Nasugbu Store Coverage Explorer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ─── Feature 5: Login Form – fades in & slides up ───
            if (isSplashDone || formAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = formOffsetY)
                        .alpha(formAlpha)
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sign In",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Email field
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it; if (emailError != null) emailError = null },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Email Address") },
                                placeholder = { Text("name@example.com") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, "Email", tint = LogoThemeBlue)
                                },
                                trailingIcon = {
                                    if (email.isNotEmpty()) {
                                        IconButton(onClick = { email = "" }) {
                                            Icon(Icons.Default.Clear, "Clear email")
                                        }
                                    }
                                },
                                isError = emailError != null,
                                supportingText = emailError?.let { msg -> { Text(msg) } },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LogoThemeBlue,
                                    focusedLabelColor = LogoThemeBlue,
                                    cursorColor = LogoThemeBlue
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Password field with modern visibility toggle
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; if (passwordError != null) passwordError = null },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Password") },
                                placeholder = { Text("Enter your password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, "Password", tint = LogoThemeBlue)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = if (passwordVisible) LogoThemeBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                isError = passwordError != null,
                                supportingText = passwordError?.let { msg -> { Text(msg) } },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { validateAndLogin() }),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LogoThemeBlue,
                                    focusedLabelColor = LogoThemeBlue,
                                    cursorColor = LogoThemeBlue
                                )
                            )

                            // Forgot Password link
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                TextButton(onClick = { /* TODO: password reset flow */ }) {
                                    Text(
                                        "Forgot Password?",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = LogoThemeBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Prominent Log In button
                            Button(
                                onClick = { validateAndLogin() },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LogoThemeBlue,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Text(
                                        "Log In",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Continue as Guest
                            OutlinedButton(
                                onClick = onContinueAsGuest,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LogoThemeBlue)
                            ) {
                                Text("Continue as Guest", fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Sign-up footer
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Don't have an account?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                TextButton(onClick = { /* TODO: sign-up flow */ }) {
                                    Text(
                                        "Sign Up",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = LogoThemeBlue
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

@Preview(name = "Splash State", showBackground = true, widthDp = 390, heightDp = 820)
@Composable
fun LoginScreenSplashPreview() {
    LoginScreen(initialSplashDone = false)
}

@Preview(name = "Revealed Login Form", showBackground = true, widthDp = 390, heightDp = 820)
@Composable
fun LoginScreenRevealedPreview() {
    LoginScreen(initialSplashDone = true)
}
