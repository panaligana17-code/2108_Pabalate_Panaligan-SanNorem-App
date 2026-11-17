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
 * Dedicated Admin Portal Activity for verified administrators.
 * Hosts San Norem store operations, GIS demand requests, tag overrides, and live reports.
 */
class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authManager = AuthManager(applicationContext)

        // Strict Session Validation: Ensure active admin session exists
        if (!authManager.isAdminLoggedIn()) {
            val intent = Intent(this, AdminLoginActivity::class.java).apply {
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
                    color = Color(0xFF0F172A)
                ) {
                    SanNoremAdminDashboard(
                        onBackToUserApp = {
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
