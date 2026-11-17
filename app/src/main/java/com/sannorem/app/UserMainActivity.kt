package com.sannorem.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sannorem.app.ui.theme.SanNoremTheme

/**
 * Dedicated User Portal Activity for standard authenticated users.
 * Strictly isolates user functionality with zero access to admin controls.
 */
class UserMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authManager = AuthManager(applicationContext)
        val currentUser = authManager.getCurrentSession()

        // Strict Session Validation: Ensure active standard user session exists
        if (currentUser == null || !authManager.isUserLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
            return
        }

        setContent {
            SanNoremTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0084D6)
                ) {
                    SanNoremDashboardScreen(
                        currentUser = currentUser,
                        authManager = authManager,
                        onLogout = {
                            authManager.clearSession()
                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}
