package com.sannorem.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sannorem.app.ui.components.SanNoremLogo
import com.sannorem.app.ui.theme.SanNoremTheme
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import java.util.Calendar
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ======================================================================================
// 1. DATA MODELS & ENUMS (100% Kotlin, Zero Java)
// ======================================================================================

enum class CoverageBrand(val label: String, val hexColor: Long, val badgeBg: Long, val badgeBorder: Long) {
    SEVEN_ELEVEN("7-Eleven", 0xFFEA580C, 0xFFFFF7ED, 0xFFFED7AA),
    ALFAMART("Alfamart", 0xFFDC2626, 0xFFFEF2F2, 0xFFFECACA),
    DALI("DALI", 0xFF2563EB, 0xFFEFF6FF, 0xFFBFDBFE)
}

enum class ServiceState(val label: String, val colorHex: Long) {
    WORKING("Working", 0xFF16A34A),
    OFFLINE("Offline", 0xFFDC2626),
    BUSY("Busy", 0xFFEA580C)
}

data class ServiceItemStatus(
    val state: ServiceState = ServiceState.WORKING,
    val reportCount: Int = 0,
    val offlineReports: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class UserServiceReport(
    val id: String = java.util.UUID.randomUUID().toString(),
    val branchId: String,
    val branchName: String,
    val serviceName: String,
    val reportedState: ServiceState,
    val userEmail: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class StoreRequestType(val label: String) {
    UNDERSERVED_AREA("Underserved Area - Need Store Here"),
    NEWLY_OPENED("Report Newly Opened Branch")
}

data class BranchSuggestionRequest(
    val id: String = java.util.UUID.randomUUID().toString(),
    val requestType: StoreRequestType,
    val brgyId: String,
    val brgyName: String,
    val preferredBrand: String,
    val branchName: String = "",
    val lat: Double,
    val lng: Double,
    val note: String,
    val userEmail: String,
    val timestamp: Long = System.currentTimeMillis(),
    var status: String = "PENDING" // "PENDING", "APPROVED", "REJECTED"
)

data class EmergencyBroadcast(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isActive: Boolean = false,
    val title: String = "",
    val message: String = "",
    val affectedBarangayIds: List<String> = emptyList(), // empty = All
    val affectedBrands: List<CoverageBrand> = emptyList(), // empty = All
    val isTemporaryClosedOverride: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class BarangayCoverageAnalytics(
    val brgyId: String,
    val brgyName: String,
    val group: String,
    val totalBranches: Int,
    val alfamartCount: Int,
    val daliCount: Int,
    val sevenElevenCount: Int,
    val status: String, // "Well-Served", "Moderate", "Underserved"
    val statusColorHex: Long
)

data class CoverageBarangay(
    val id: String,
    val name: String,
    val group: String,
    val lat: Double,
    val lng: Double
)

data class CoverageBranch(
    val id: String,
    val brand: CoverageBrand,
    val name: String,
    val brgyId: String,
    val lat: Double,
    val lng: Double,
    val radiusKm: Double,
    val hours: String,
    val isVerified: Boolean,
    val attrs: List<String>,
    val strategy: String,
    val servicesStatus: Map<String, ServiceItemStatus> = emptyMap()
)

data class CoveringBranchResult(
    val branch: CoverageBranch,
    val distanceKm: Double
)

data class UserAccount(
    val fullName: String,
    val email: String,
    val passwordHash: String
)

// ======================================================================================
// 2. AUTHENTICATION & SESSION MANAGER
// ======================================================================================

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sannorem_user_auth", Context.MODE_PRIVATE)

    sealed class AuthResult {
        data class Success(val user: UserAccount) : AuthResult()
        object AccountNotFound : AuthResult()
        object IncorrectPassword : AuthResult()
        object AdminRestricted : AuthResult()
    }

    init {
        // Pre-configure persistent Admin account credentials
        if (!accountExists("admin@sannorem.com")) {
            prefs.edit()
                .putString("user_admin@sannorem.com_name", "San Norem Administrator")
                .putString("user_admin@sannorem.com_pass", "admin123")
                .putLong("user_admin@sannorem.com_created_at", System.currentTimeMillis())
                .putBoolean("user_admin@sannorem.com_tutorial_seen", true)
                .commit()
        }
        // Pre-configure Aya Admin account credentials
        prefs.edit()
            .putString("user_aya@sannorem.com_name", "Aya (Administrator)")
            .putString("user_aya@sannorem.com_pass", "admin")
            .putLong("user_aya@sannorem.com_created_at", System.currentTimeMillis())
            .putBoolean("user_aya@sannorem.com_tutorial_seen", true)
            .commit()
    }

    fun accountExists(email: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        return prefs.contains("user_${cleanEmail}_pass")
    }

    fun registerUser(fullName: String, email: String, pass: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty() || pass.length < 6) return false
        // Strictly block public sign up for admin accounts
        if (cleanEmail == "admin@sannorem.com" || cleanEmail == "admin" ||
            cleanEmail == "aya@sannorem.com" || cleanEmail == "aya") return false
        val key = "user_$cleanEmail"
        if (accountExists(cleanEmail)) return false

        prefs.edit()
            .putString("${key}_name", fullName.trim())
            .putString("${key}_pass", pass)
            .putLong("${key}_created_at", System.currentTimeMillis())
            .putBoolean("${key}_tutorial_seen", false)
            .commit()
        return true
    }

    fun loginAdmin(identifier: String, pass: String): Boolean {
        val clean = identifier.trim().lowercase()
        if ((clean == "admin@sannorem.com" || clean == "admin") && pass == "admin123") {
            saveSession("San Norem Administrator", "admin@sannorem.com", role = "ADMIN")
            return true
        }
        if ((clean == "aya@sannorem.com" || clean == "aya") && pass == "admin") {
            saveSession("Aya (Administrator)", "aya@sannorem.com", role = "ADMIN")
            return true
        }
        return false
    }

    fun loginUser(email: String, pass: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        // Admin credentials must use the Admin Portal
        if (cleanEmail == "admin@sannorem.com" || cleanEmail == "admin" ||
            cleanEmail == "aya@sannorem.com" || cleanEmail == "aya") {
            return AuthResult.AdminRestricted
        }
        val key = "user_$cleanEmail"
        if (!accountExists(cleanEmail)) {
            return AuthResult.AccountNotFound
        }
        val storedPass = prefs.getString("${key}_pass", null)
        if (storedPass == pass) {
            val name = prefs.getString("${key}_name", "Verified User") ?: "Verified User"
            saveSession(name, cleanEmail, role = "USER")
            return AuthResult.Success(UserAccount(name, cleanEmail, storedPass))
        }
        return AuthResult.IncorrectPassword
    }

    fun login(email: String, pass: String): AuthResult = loginUser(email, pass)

    fun authenticate(email: String, pass: String): UserAccount? {
        val result = loginUser(email, pass)
        return if (result is AuthResult.Success) result.user else null
    }

    fun hasSeenTutorial(email: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val key = "user_$cleanEmail"
        return prefs.getBoolean("${key}_tutorial_seen", false)
    }

    fun markTutorialSeen(email: String) {
        val cleanEmail = email.trim().lowercase()
        val key = "user_$cleanEmail"
        prefs.edit().putBoolean("${key}_tutorial_seen", true).commit()
    }

    fun saveSession(fullName: String, email: String, role: String = "USER") {
        prefs.edit()
            .putString("session_name", fullName)
            .putString("session_email", email)
            .putString("session_role", role)
            .commit()
    }

    fun getSessionRole(): String? {
        return prefs.getString("session_role", null)
    }

    fun isUserLoggedIn(): Boolean {
        return getSessionRole() == "USER" && getCurrentSession() != null
    }

    fun isAdminLoggedIn(): Boolean {
        return getSessionRole() == "ADMIN" && getCurrentSession() != null
    }

    fun clearSession() {
        prefs.edit()
            .remove("session_name")
            .remove("session_email")
            .remove("session_role")
            .commit()
    }

    fun getCurrentSession(): UserAccount? {
        val email = prefs.getString("session_email", null) ?: return null
        val name = prefs.getString("session_name", "User") ?: "User"
        return UserAccount(name, email, "")
    }
}

// ======================================================================================
// 3. DATA ENGINE: EXACT 31 OFFICIAL STORE BRANCHES & NASUGBU BARANGAYS
// ======================================================================================

object CoverageDataStore {
    private const val EARTH_RADIUS_KM = 6371.0088

    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
    }

    val barangays: List<CoverageBarangay> by lazy {
        val list = mutableListOf<CoverageBarangay>()

        // 12 Poblacion Barangays
        for (i in 0 until 12) {
            val row = (i / 4) - 1
            val col = (i % 4) - 1.5
            list.add(
                CoverageBarangay(
                    id = "brgy-${i + 1}",
                    name = "Barangay ${i + 1} (Poblacion)",
                    group = "Poblacion",
                    lat = 14.0720 + row * 0.0032,
                    lng = 120.6320 + col * 0.0035
                )
            )
        }

        // 30 Rural & Coastal Barangays
        val ruralList = listOf(
            Triple("Aga", 14.07449, 120.73847),
            Triple("Balaytigui", 14.13752, 120.61198),
            Triple("Banilad", 14.0640, 120.6520),
            Triple("Bilaran", 14.0500, 120.6430),
            Triple("Bucana", 14.09312, 120.62887),
            Triple("Bulihan", 14.0830, 120.6560),
            Triple("Bunducan", 14.1090, 120.6050),
            Triple("Butucan", 14.0450, 120.6560),
            Triple("Calayo", 14.18631, 120.60942),
            Triple("Catandaan", 14.1170, 120.6680),
            Triple("Cogunan", 14.0350, 120.6380),
            Triple("Dayap", 14.08861, 120.74834),
            Triple("Kaylaway", 14.06731, 120.78519),
            Triple("Kayrilaw", 14.0900, 120.6520),
            Triple("Latag", 14.1150, 120.6350),
            Triple("Looc", 14.22178, 120.63276),
            Triple("Lumbangan", 14.0760, 120.6830),
            Triple("Malapad Na Bato", 14.1240, 120.6640),
            Triple("Mataas Na Pulo", 14.1300, 120.5850),
            Triple("Maugat", 14.0420, 120.6700),
            Triple("Munting Indan", 14.0700, 120.6150),
            Triple("Natipuan", 14.0980, 120.7480),
            Triple("Pantalan", 14.0780, 120.6180),
            Triple("Papaya", 14.0210, 120.6200),
            Triple("Putat", 14.0280, 120.6240),
            Triple("Reparo", 14.0710, 120.6950),
            Triple("Talangan", 14.0560, 120.6900),
            Triple("Tumalim", 14.0300, 120.6050),
            Triple("Utod", 14.0390, 120.6150),
            Triple("Wawa", 14.07592, 120.62248)
        )

        for ((name, lat, lng) in ruralList) {
            list.add(
                CoverageBarangay(
                    id = name.lowercase().replace(" ", "-"),
                    name = name,
                    group = "Rural & Coastal",
                    lat = lat,
                    lng = lng
                )
            )
        }

        list
    }


    // Exact branches provided by user with high precision coordinates
    val branches: List<CoverageBranch> by lazy {
        listOf(
            // === ALFAMART BRANCHES (16) ===
            CoverageBranch(
                id = "ALFA-001",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Brgy. Wawa-Nasugbu",
                brgyId = "wawa",
                lat = 14.079430116291613,
                lng = 120.62645494937897,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Fresh Produce & Meat", "Chilled Beverages", "Pantry Essentials"),
                strategy = "Serving coastal Wawa neighborhood with fresh food and daily groceries."
            ),
            CoverageBranch(
                id = "ALFA-002",
                brand = CoverageBrand.ALFAMART,
                name = "Alfa Mart Poblacion 2 Nasugbu",
                brgyId = "brgy-2",
                lat = 14.077562867377686,
                lng = 120.63103637469676,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Fresh Food Section", "Express Checkout", "Personal Care"),
                strategy = "Town proper residential shopping and convenience store."
            ),
            CoverageBranch(
                id = "ALFA-003",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart JP Laurel",
                brgyId = "brgy-1",
                lat = 14.066749400925646,
                lng = 120.63350915908813,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Daily Supermarket Essentials", "Cold Drinks", "Snacks"),
                strategy = "Prime commercial strip along J.P. Laurel Street."
            ),
            CoverageBranch(
                id = "ALFA-004",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart wawa 3 nasugbu Batangas",
                brgyId = "wawa",
                lat = 14.081798204519972,
                lng = 120.62485956623685,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Frozen Meat & Dairy", "Packaged Goods", "Beverages"),
                strategy = "Community service hub in upper Wawa."
            ),
            CoverageBranch(
                id = "ALFA-005",
                brand = CoverageBrand.ALFAMART,
                name = "ALFAMART POBLACION II 2 NASUGBU",
                brgyId = "brgy-2",
                lat = 14.075173451483145,
                lng = 120.63237108026938,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Supermarket Goods", "Personal Hygiene", "Quick Bites"),
                strategy = "Central Poblacion walk-in shopping store."
            ),
            CoverageBranch(
                id = "ALFA-006",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart poblacion 5 Nasugbu",
                brgyId = "brgy-5",
                lat = 14.073274567607854,
                lng = 120.63717842102051,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Fresh Produce", "Dry Goods", "Cold Cuts"),
                strategy = "Convenient access for Barangay 5 residential corridor."
            ),
            CoverageBranch(
                id = "ALFA-007",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Poblacion XI Nasugbu",
                brgyId = "brgy-11",
                lat = 14.062516268568622,
                lng = 120.63415825366974,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Pantry Essentials", "Frozen Food", "Hygiene Supplies"),
                strategy = "North Poblacion transit and neighborhood store."
            ),
            CoverageBranch(
                id = "ALFA-008",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart bucana",
                brgyId = "bucana",
                lat = 14.06774587981612,
                lng = 120.62840223312378,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Fresh Food", "Beverages & Ice", "Bucana Center"),
                strategy = "Bucana coastal and university community service point."
            ),
            CoverageBranch(
                id = "ALFA-009",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart F Alix Margarita Pob 7",
                brgyId = "brgy-7",
                lat = 14.06845,
                lng = 120.63440795030462,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Household Essentials", "Food & Drink", "Snack Hub"),
                strategy = "F. Alix & Margarita Street local residential hub."
            ),
            CoverageBranch(
                id = "ALFA-010",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Wawa2-Nasugbu",
                brgyId = "wawa",
                lat = 14.07489802511803,
                lng = 120.62558054924011,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Fresh Groceries", "Chilled Products", "Quick Service"),
                strategy = "Wawa South waterfront neighborhood branch."
            ),
            CoverageBranch(
                id = "ALFA-011",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Poblacion 7 Nasugbu",
                brgyId = "brgy-7",
                lat = 14.06736341976332,
                lng = 120.63551008701324,
                radiusKm = 0.5,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Full Supermarket Line", "Personal Care", "Snacks"),
                strategy = "Downtown shopping convenience for Barangay 7 residents."
            ),
            CoverageBranch(
                id = "ALFA-012",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Looc Nasugbu",
                brgyId = "looc",
                lat = 14.163442205968872,
                lng = 120.62861144542694,
                radiusKm = 0.8,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Highway Retail Hub", "Beach Area Supplies", "Groceries"),
                strategy = "Serving North Nasugbu, Looc residents and beach travelers."
            ),
            CoverageBranch(
                id = "ALFA-013",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Calayo Nasugbu",
                brgyId = "calayo",
                lat = 14.153219731300032,
                lng = 120.60905621085229,
                radiusKm = 0.8,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Resort & Resident Supplies", "Fresh Goods", "Ice & Water"),
                strategy = "Calayo Cove tourism and local community supermarket."
            ),
            CoverageBranch(
                id = "ALFA-014",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart Aga 2",
                brgyId = "aga",
                lat = 14.095996719918537,
                lng = 120.79551458358765,
                radiusKm = 0.8,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Highway Stop Store", "Produce & Meat", "Road Trip Essentials"),
                strategy = "Tagaytay-Nasugbu Highway Aga retail station."
            ),
            CoverageBranch(
                id = "ALFA-015",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart- Malarutan",
                brgyId = "malarutan",
                lat = 14.08124,
                lng = 120.64515,
                radiusKm = 0.8,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Neighborhood Grocery", "Daily Household Goods", "Drinks"),
                strategy = "Malarutan eastern border shopping convenience."
            ),
            CoverageBranch(
                id = "ALFA-016",
                brand = CoverageBrand.ALFAMART,
                name = "Alfamart kaylaway Nasugbu",
                brgyId = "kaylaway",
                lat = 14.057659028070338,
                lng = 120.84481428043486,
                radiusKm = 0.8,
                hours = "6:00 AM - 10:00 PM",
                isVerified = true,
                attrs = listOf("Fresh Food Section", "Express Groceries", "Snacks"),
                strategy = "Kaylaway mountain junction express grocery."
            ),

            // === DALI EVERYDAY GROCERY BRANCHES (11) ===
            CoverageBranch(
                id = "DALI-001",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Wawa",
                brgyId = "wawa",
                lat = 14.079841174075316,
                lng = 120.62605798244476,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Everyday Low Price", "Frozen Goods & Poultry", "Bulk Discounts"),
                strategy = "Discount supermarket serving Wawa port and community."
            ),
            CoverageBranch(
                id = "DALI-002",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Pob. 2",
                brgyId = "brgy-2",
                lat = 14.078095474211151,
                lng = 120.63041925430298,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Affordable Groceries", "Dairy & Frozen Food", "Household Cleaning"),
                strategy = "Poblacion 2 central community grocery store."
            ),
            CoverageBranch(
                id = "DALI-003",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu Sentro",
                brgyId = "brgy-4",
                lat = 14.072712598859823,
                lng = 120.63315510749817,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Discount Food Market", "Baking & Cooking Essentials", "Snacks"),
                strategy = "Nasugbu town proper sentro discount hub."
            ),
            CoverageBranch(
                id = "DALI-004",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Bucana",
                brgyId = "bucana",
                lat = 14.06755595075422,
                lng = 120.62853366136551,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Low-Cost Daily Food", "Frozen Meats", "Beverages"),
                strategy = "Bucana student and residential grocery mart."
            ),
            CoverageBranch(
                id = "DALI-005",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Pob. 1",
                brgyId = "brgy-1",
                lat = 14.073727263650012,
                lng = 120.63128963112831,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Everyday Low Prices", "Dairy Products", "Pantry Food"),
                strategy = "Poblacion 1 town center accessibility store."
            ),
            CoverageBranch(
                id = "DALI-006",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Pob. 11",
                brgyId = "brgy-11",
                lat = 14.063132889472277,
                lng = 120.6342452306014,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Affordable Cooking Ingredients", "Snacks", "Cold Drinks"),
                strategy = "Terminal and residential gateway for Poblacion 11."
            ),
            CoverageBranch(
                id = "DALI-007",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Balaytigui",
                brgyId = "balaytigui",
                lat = 14.12875559223731,
                lng = 120.60835544159588,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Value Grocery Hub", "Frozen Foods", "Household Supplies"),
                strategy = "Coastal Balaytigue peninsula grocery service."
            ),
            CoverageBranch(
                id = "DALI-008",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Looc",
                brgyId = "looc",
                lat = 14.164768546010613,
                lng = 120.62926054000854,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Affordable Daily Essentials", "Canned Goods", "Beverages"),
                strategy = "Northern Looc highway discount grocery store."
            ),
            CoverageBranch(
                id = "DALI-009",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Dayap",
                brgyId = "dayap",
                lat = 14.093811226533436,
                lng = 120.66421057429544,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Budget Grocery Market", "Frozen Meat", "Household Essentials"),
                strategy = "Dayap agricultural valley neighborhood store."
            ),
            CoverageBranch(
                id = "DALI-010",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Aga",
                brgyId = "aga",
                lat = 14.095746981051162,
                lng = 120.79403400421143,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Everyday Low Price Groceries", "Dairy Products", "Quick Essentials"),
                strategy = "Highway junction discount grocery hub in Aga."
            ),
            CoverageBranch(
                id = "DALI-011",
                brand = CoverageBrand.DALI,
                name = "DALI Everyday Grocery - Nasugbu - Kaylaway",
                brgyId = "kaylaway",
                lat = 14.075024550532625,
                lng = 120.84010677911007,
                radiusKm = 1.0,
                hours = "7:00 AM - 9:00 PM",
                isVerified = true,
                attrs = listOf("Discount Food Supplies", "Pantry Staples", "Personal Care"),
                strategy = "Kaylaway mountain corridor daily grocery market."
            ),

            // === 7-ELEVEN BRANCHES (4) ===
            CoverageBranch(
                id = "SEVN-001",
                brand = CoverageBrand.SEVEN_ELEVEN,
                name = "7-Eleven (3650) Roxaco Nasugbu",
                brgyId = "brgy-2",
                lat = 14.078199540150706,
                lng = 120.63068479299545,
                radiusKm = 0.6,
                hours = "24/7",
                isVerified = true,
                attrs = listOf("24/7 Operations", "CLiQQ Digital Kiosk & Bill Pay", "ATM Machine"),
                strategy = "Flagship 24/7 convenience store at Roxaco Landing corridor."
            ),
            CoverageBranch(
                id = "SEVN-002",
                brand = CoverageBrand.SEVEN_ELEVEN,
                name = "7-Eleven (JP Laurel St)",
                brgyId = "brgy-1",
                lat = 14.07209,
                lng = 120.63088,
                radiusKm = 0.6,
                hours = "24/7",
                isVerified = true,
                attrs = listOf("24/7 Quick Service", "Hot Food & Meals", "Chilled Beverages"),
                strategy = "Prime downtown walking convenience along J.P. Laurel."
            ),
            CoverageBranch(
                id = "SEVN-003",
                brand = CoverageBrand.SEVEN_ELEVEN,
                name = "7-Eleven Bucana Nasugbu",
                brgyId = "bucana",
                lat = 14.06759237551804,
                lng = 120.62841564416885,
                radiusKm = 0.6,
                hours = "24/7",
                isVerified = true,
                attrs = listOf("24/7 Store", "Student & Tourist Hub", "CLiQQ Services"),
                strategy = "Serving Bucana beach resorts and university students round-the-clock."
            ),
            CoverageBranch(
                id = "SEVN-004",
                brand = CoverageBrand.SEVEN_ELEVEN,
                name = "7-Eleven (3359) Nasugbu Arch",
                brgyId = "kaylaway",
                lat = 14.058257039122788,
                lng = 120.84506839513779,
                radiusKm = 0.6,
                hours = "24/7",
                isVerified = true,
                attrs = listOf("24/7 Highway Stopover", "Coffee & Snacks", "CLiQQ Kiosk"),
                strategy = "Welcome Arch highway stopover station welcoming visitors to Nasugbu."
            )
        )
    }

    fun getBarangayName(id: String): String {
        return barangays.firstOrNull { it.id == id }?.name ?: "Nasugbu Center"
    }

    fun findCovering(lat: Double, lng: Double, list: List<CoverageBranch>): List<CoveringBranchResult> {
        return list.map { branch ->
            val dist = haversineKm(lat, lng, branch.lat, branch.lng)
            CoveringBranchResult(branch, dist)
        }.filter { it.distanceKm <= it.branch.radiusKm }
         .sortedBy { it.distanceKm }
    }

    fun findNearbyAll(lat: Double, lng: Double, list: List<CoverageBranch>): List<CoveringBranchResult> {
        return list.map { branch ->
            val dist = haversineKm(lat, lng, branch.lat, branch.lng)
            CoveringBranchResult(branch, dist)
        }.sortedBy { it.distanceKm }
    }

    private var cachedPolygons: Map<String, List<GeoPoint>>? = null

    fun getBarangayPolygon(context: Context, brgyId: String): List<GeoPoint>? {
        if (cachedPolygons == null) {
            val map = mutableMapOf<String, List<GeoPoint>>()
            try {
                val jsonString = context.assets.open("nasugbu_barangays_geojson.json").bufferedReader().use { it.readText() }
                val root = JSONObject(jsonString)
                val features = root.optJSONArray("features")
                if (features != null) {
                    for (i in 0 until features.length()) {
                        val feature = features.getJSONObject(i)
                        val id = feature.optJSONObject("properties")?.optString("id") ?: continue
                        val geom = feature.getJSONObject("geometry")
                        val type = geom.optString("type")
                        val points = mutableListOf<GeoPoint>()
                        if (type.equals("Polygon", ignoreCase = true)) {
                            val coords = geom.getJSONArray("coordinates").getJSONArray(0)
                            for (j in 0 until coords.length()) {
                                val c = coords.getJSONArray(j)
                                points.add(GeoPoint(c.getDouble(1), c.getDouble(0)))
                            }
                        } else if (type.equals("MultiPolygon", ignoreCase = true)) {
                            val polyArray = geom.getJSONArray("coordinates")
                            var bestRing: org.json.JSONArray? = null
                            var maxLen = 0
                            for (p in 0 until polyArray.length()) {
                                val ring = polyArray.getJSONArray(p).getJSONArray(0)
                                if (ring.length() > maxLen) {
                                    maxLen = ring.length()
                                    bestRing = ring
                                }
                            }
                            if (bestRing != null) {
                                for (j in 0 until bestRing.length()) {
                                    val c = bestRing.getJSONArray(j)
                                    points.add(GeoPoint(c.getDouble(1), c.getDouble(0)))
                                }
                            }
                        }
                        if (points.isNotEmpty()) {
                            map[id] = points
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cachedPolygons = map
        }
        return cachedPolygons?.get(brgyId)
    }

    fun getBarangayBoundingBox(context: Context, brgyId: String): BoundingBox? {
        val points = getBarangayPolygon(context, brgyId) ?: return null
        if (points.isEmpty()) return null
        var maxLat = -90.0
        var minLat = 90.0
        var maxLng = -180.0
        var minLng = 180.0
        for (p in points) {
            if (p.latitude > maxLat) maxLat = p.latitude
            if (p.latitude < minLat) minLat = p.latitude
            if (p.longitude > maxLng) maxLng = p.longitude
            if (p.longitude < minLng) minLng = p.longitude
        }
        return BoundingBox(maxLat, maxLng, minLat, minLng)
    }

    fun isPointInPolygon(lat: Double, lng: Double, points: List<GeoPoint>): Boolean {
        if (points.size < 3) return false
        var inside = false
        val n = points.size
        var j = n - 1
        for (i in 0 until n) {
            val xi = points[i].longitude
            val yi = points[i].latitude
            val xj = points[j].longitude
            val yj = points[j].latitude
            val intersect = ((yi > lat) != (yj > lat)) &&
                    (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi)
            if (intersect) inside = !inside
            j = i
        }
        return inside
    }

    fun filterBranchesInBarangayPolygon(context: Context, brgyId: String, branches: List<CoverageBranch>): List<CoverageBranch> {
        val polygon = getBarangayPolygon(context, brgyId) ?: return branches.filter { it.brgyId == brgyId }
        val box = getBarangayBoundingBox(context, brgyId)
        return branches.filter { b ->
            if (b.brgyId == brgyId) {
                true
            } else if (box != null && (b.lat > box.latNorth || b.lat < box.latSouth || b.lng > box.lonEast || b.lng < box.lonWest)) {
                false
            } else {
                isPointInPolygon(b.lat, b.lng, polygon)
            }
        }
    }

    // Default services mapping per brand and custom attributes
    fun getDefaultServices(brand: CoverageBrand, customAttrs: List<String>): Map<String, ServiceItemStatus> {
        val map = mutableMapOf<String, ServiceItemStatus>()
        val base = when (brand) {
            CoverageBrand.SEVEN_ELEVEN -> listOf("Cliqq Kiosk", "ATM", "GCash Cash-In", "Bills Payment", "Parking")
            CoverageBrand.ALFAMART -> listOf("GCash Cash-In", "ATM", "Bills Payment", "Frozen Meat", "Parking")
            CoverageBrand.DALI -> listOf("GCash Cash-In", "Frozen Meat", "Daily Groceries", "Parking", "ATM")
        }
        for (s in base) {
            map[s] = ServiceItemStatus(state = ServiceState.WORKING, reportCount = 0, offlineReports = 0)
        }
        for (a in customAttrs) {
            if (!map.containsKey(a)) {
                map[a] = ServiceItemStatus(state = ServiceState.WORKING, reportCount = 0, offlineReports = 0)
            }
        }
        return map
    }

    // ==================================================================================
    // REAL-TIME OBSERVABLE STATE (Shared between User View & Admin Control Center)
    // ==================================================================================
    val activeBranches = mutableStateListOf<CoverageBranch>()
    val serviceReports = mutableStateListOf<UserServiceReport>()
    val demandRequests = mutableStateListOf<BranchSuggestionRequest>()
    val customServiceTags = mutableStateListOf(
        "Open 24/7", "GCash Cash-In", "ATM", "Cliqq Kiosk",
        "Bills Payment", "Parking", "Frozen Meat", "Fresh Produce & Meat"
    )
    val emergencyBroadcast = mutableStateOf(EmergencyBroadcast())
    val bookmarkedBranchIds = mutableStateListOf<String>()

    private var isStoreInitialized = false

    fun ensureInitialized(context: Context? = null) {
        if (isStoreInitialized && activeBranches.isNotEmpty()) return
        isStoreInitialized = true

        if (activeBranches.isEmpty()) {
            val enriched = branches.map { b ->
                if (b.servicesStatus.isEmpty()) {
                    b.copy(servicesStatus = getDefaultServices(b.brand, b.attrs))
                } else b
            }
            activeBranches.addAll(enriched)
        }

        if (serviceReports.isEmpty()) {
            serviceReports.add(
                UserServiceReport(
                    branchId = "SEV-001",
                    branchName = "7-Eleven Nasugbu Arch",
                    serviceName = "Cliqq Kiosk",
                    reportedState = ServiceState.OFFLINE,
                    userEmail = "resident@nasugbu.gov",
                    timestamp = System.currentTimeMillis() - 15 * 60 * 1000
                )
            )
            serviceReports.add(
                UserServiceReport(
                    branchId = "ALFA-001",
                    branchName = "Alfamart Brgy. Wawa-Nasugbu",
                    serviceName = "ATM",
                    reportedState = ServiceState.BUSY,
                    userEmail = "juan.delacruz@gmail.com",
                    timestamp = System.currentTimeMillis() - 45 * 60 * 1000
                )
            )
        }

        if (demandRequests.isEmpty()) {
            demandRequests.add(
                BranchSuggestionRequest(
                    requestType = StoreRequestType.UNDERSERVED_AREA,
                    brgyId = "calayo",
                    brgyName = "Calayo",
                    preferredBrand = "Alfamart",
                    lat = 14.1863,
                    lng = 120.6094,
                    note = "Far from highway, lots of tourists and beach resort staff need grocery access.",
                    userEmail = "calayo.resident@gmail.com",
                    timestamp = System.currentTimeMillis() - 2 * 3600 * 1000
                )
            )
            demandRequests.add(
                BranchSuggestionRequest(
                    requestType = StoreRequestType.UNDERSERVED_AREA,
                    brgyId = "papaya",
                    brgyName = "Papaya",
                    preferredBrand = "DALI",
                    lat = 14.0210,
                    lng = 120.6200,
                    note = "Coastal fishing community with zero modern convenience stores.",
                    userEmail = "maria.santos@gmail.com",
                    timestamp = System.currentTimeMillis() - 5 * 3600 * 1000
                )
            )
            demandRequests.add(
                BranchSuggestionRequest(
                    requestType = StoreRequestType.NEWLY_OPENED,
                    brgyId = "brgy-4",
                    brgyName = "Barangay 4 (Poblacion)",
                    preferredBrand = "Alfamart",
                    branchName = "Alfamart Poblacion 4 Corner Store",
                    lat = 14.0745,
                    lng = 120.6330,
                    note = "Newly opened branch along market road this week! Fully operational.",
                    userEmail = "poblacion.shopper@yahoo.com",
                    timestamp = System.currentTimeMillis() - 1 * 3600 * 1000
                )
            )
        }

        context?.let { ctx ->
            val prefs = ctx.getSharedPreferences("sannorem_bookmarks", Context.MODE_PRIVATE)
            val saved = prefs.getStringSet("bookmarked_ids", emptySet()) ?: emptySet()
            if (bookmarkedBranchIds.isEmpty()) {
                bookmarkedBranchIds.addAll(saved)
            }
        }
    }

    // Emergency and Operating Hours Evaluation
    fun isBranchEffectivelyOpen(branch: CoverageBranch): Boolean {
        val broadcast = emergencyBroadcast.value
        if (broadcast.isActive && broadcast.isTemporaryClosedOverride) {
            val matchesBrgy = broadcast.affectedBarangayIds.isEmpty() || broadcast.affectedBarangayIds.contains(branch.brgyId)
            val matchesBrand = broadcast.affectedBrands.isEmpty() || broadcast.affectedBrands.contains(branch.brand)
            if (matchesBrgy && matchesBrand) {
                return false
            }
        }
        return OperatingHoursHelper.isStoreOpen(branch.hours)
    }

    fun getEffectiveStatusDetails(branch: CoverageBranch): BranchStatusDetails {
        val broadcast = emergencyBroadcast.value
        if (broadcast.isActive && broadcast.isTemporaryClosedOverride) {
            val matchesBrgy = broadcast.affectedBarangayIds.isEmpty() || broadcast.affectedBarangayIds.contains(branch.brgyId)
            val matchesBrand = broadcast.affectedBrands.isEmpty() || broadcast.affectedBrands.contains(branch.brand)
            if (matchesBrgy && matchesBrand) {
                return BranchStatusDetails(
                    isOpen = false,
                    label = "Temporarily Closed (Alert)",
                    badgeColorHex = 0xFFDC2626
                )
            }
        }
        return OperatingHoursHelper.getStatusDetails(branch.hours)
    }

    // Module 1: Crowdsourced Service Reporting & Auto-Rule (3 offline reports -> OFFLINE)
    fun reportServiceStatus(
        branchId: String,
        serviceName: String,
        reportedState: ServiceState,
        userEmail: String
    ): String {
        val idx = activeBranches.indexOfFirst { it.id == branchId }
        if (idx == -1) return "Branch not found"
        val branch = activeBranches[idx]

        val report = UserServiceReport(
            branchId = branchId,
            branchName = branch.name,
            serviceName = serviceName,
            reportedState = reportedState,
            userEmail = userEmail
        )
        serviceReports.add(0, report)

        val currentMap = branch.servicesStatus.toMutableMap()
        val curr = currentMap[serviceName] ?: ServiceItemStatus()
        val newReportCount = curr.reportCount + 1
        val newOffline = if (reportedState == ServiceState.OFFLINE) curr.offlineReports + 1 else curr.offlineReports

        val newState = if (newOffline >= 3) {
            ServiceState.OFFLINE
        } else if (reportedState == ServiceState.WORKING && curr.state == ServiceState.OFFLINE) {
            ServiceState.WORKING
        } else {
            reportedState
        }

        currentMap[serviceName] = curr.copy(
            state = newState,
            reportCount = newReportCount,
            offlineReports = newOffline,
            lastUpdated = System.currentTimeMillis()
        )
        activeBranches[idx] = branch.copy(servicesStatus = currentMap)

        return if (newOffline >= 3) {
            "Naitala ang ulat! Awtomatikong naging OFFLINE ang $serviceName dahil sa 3 sunod-sunod na offline reports."
        } else {
            "Salamat! Matagumpay na naitala ang iyong ulat ukol sa $serviceName."
        }
    }

    // Module 1 Admin: Manual Real-Time Service Override
    fun adminOverrideServiceStatus(
        branchId: String,
        serviceName: String,
        newState: ServiceState
    ) {
        val idx = activeBranches.indexOfFirst { it.id == branchId }
        if (idx != -1) {
            val branch = activeBranches[idx]
            val currentMap = branch.servicesStatus.toMutableMap()
            val curr = currentMap[serviceName] ?: ServiceItemStatus()
            currentMap[serviceName] = curr.copy(
                state = newState,
                offlineReports = if (newState == ServiceState.WORKING) 0 else curr.offlineReports,
                lastUpdated = System.currentTimeMillis()
            )
            activeBranches[idx] = branch.copy(servicesStatus = currentMap)
        }
    }

    // Module 2: Suggest Missing Branch / Demand Mapping
    fun submitBranchSuggestion(
        requestType: StoreRequestType,
        brgyId: String,
        preferredBrand: String,
        branchName: String,
        lat: Double,
        lng: Double,
        note: String,
        userEmail: String
    ) {
        val brgyName = getBarangayName(brgyId)
        demandRequests.add(
            0,
            BranchSuggestionRequest(
                requestType = requestType,
                brgyId = brgyId,
                brgyName = brgyName,
                preferredBrand = preferredBrand,
                branchName = branchName,
                lat = lat,
                lng = lng,
                note = note,
                userEmail = userEmail
            )
        )
    }

    // Module 2 Admin: Approve / Reject Branch Submissions
    fun approveBranchSubmission(request: BranchSuggestionRequest): CoverageBranch {
        request.status = "APPROVED"
        val brand = when (request.preferredBrand.uppercase()) {
            "7-ELEVEN", "SEVEN_ELEVEN" -> CoverageBrand.SEVEN_ELEVEN
            "DALI" -> CoverageBrand.DALI
            else -> CoverageBrand.ALFAMART
        }
        val newBranch = CoverageBranch(
            id = "COMMUNITY-${System.currentTimeMillis() % 100000}",
            brand = brand,
            name = if (request.branchName.isNotBlank()) request.branchName else "${brand.label} ${request.brgyName}",
            brgyId = request.brgyId,
            lat = request.lat,
            lng = request.lng,
            radiusKm = 0.5,
            hours = "6:00 AM - 10:00 PM",
            isVerified = true,
            attrs = listOf("Community Approved", "Daily Groceries", "Chilled Beverages"),
            strategy = "Community approved branch via San Norem Demand Mapping.",
            servicesStatus = getDefaultServices(brand, listOf("Community Approved", "Daily Groceries"))
        )
        activeBranches.add(newBranch)
        return newBranch
    }

    fun rejectBranchSubmission(request: BranchSuggestionRequest) {
        request.status = "REJECTED"
    }

    // Module 3 Admin: Custom Service Tag Manager & Bulk Assignment
    fun createCustomServiceTag(tag: String): Boolean {
        val clean = tag.trim()
        if (clean.isBlank() || customServiceTags.contains(clean)) return false
        customServiceTags.add(clean)
        return true
    }

    fun bulkAssignServiceTag(tagName: String, branchIds: Set<String>) {
        for (i in activeBranches.indices) {
            val branch = activeBranches[i]
            val shouldHave = branchIds.contains(branch.id)
            val currentAttrs = branch.attrs.toMutableList()
            val currentServices = branch.servicesStatus.toMutableMap()

            if (shouldHave) {
                if (!currentAttrs.contains(tagName)) currentAttrs.add(tagName)
                if (!currentServices.containsKey(tagName)) {
                    currentServices[tagName] = ServiceItemStatus(state = ServiceState.WORKING)
                }
            } else {
                currentAttrs.remove(tagName)
                currentServices.remove(tagName)
            }
            activeBranches[i] = branch.copy(attrs = currentAttrs, servicesStatus = currentServices)
        }
    }

    // Module 3 User: Bookmark / Go-To Store Toggle
    fun toggleBookmark(branchId: String, context: Context) {
        if (bookmarkedBranchIds.contains(branchId)) {
            bookmarkedBranchIds.remove(branchId)
        } else {
            bookmarkedBranchIds.add(branchId)
        }
        val prefs = context.getSharedPreferences("sannorem_bookmarks", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("bookmarked_ids", bookmarkedBranchIds.toSet()).apply()
    }

    // Module 4: Barangay Coverage Analytics Engine
    fun getBarangayAnalytics(context: Context, brgyId: String): BarangayCoverageAnalytics {
        val brgyName = getBarangayName(brgyId)
        val brgy = barangays.find { it.id == brgyId }
        val group = brgy?.group ?: if (brgyId.startsWith("brgy-")) "Poblacion" else "Rural & Coastal"
        val branchesInBrgy = filterBranchesInBarangayPolygon(context, brgyId, activeBranches)
        val total = branchesInBrgy.size
        val alfa = branchesInBrgy.count { it.brand == CoverageBrand.ALFAMART }
        val dali = branchesInBrgy.count { it.brand == CoverageBrand.DALI }
        val sev = branchesInBrgy.count { it.brand == CoverageBrand.SEVEN_ELEVEN }

        val (status, color) = when {
            total >= 3 -> "Well-Served" to 0xFF16A34A
            total in 1..2 -> "Moderate" to 0xFF0084D6
            else -> "Underserved" to 0xFFEA580C
        }

        return BarangayCoverageAnalytics(
            brgyId = brgyId,
            brgyName = brgyName,
            group = group,
            totalBranches = total,
            alfamartCount = alfa,
            daliCount = dali,
            sevenElevenCount = sev,
            status = status,
            statusColorHex = color
        )
    }
}

// ======================================================================================
// 3.5. OPERATING HOURS & REAL-TIME STATUS ENGINE
// ======================================================================================

data class BranchStatusDetails(
    val isOpen: Boolean,
    val label: String,
    val badgeColorHex: Long
)

object OperatingHoursHelper {
    fun parseTimeToMinutes(timeStr: String, isClosing: Boolean = false): Int? {
        val clean = timeStr.trim().uppercase()
        if (clean.isEmpty()) return null
        val isPM = clean.contains("PM")
        val isAM = clean.contains("AM")
        val numPart = clean.replace("AM", "").replace("PM", "").trim()
        val parts = numPart.split(":")
        var hour = parts[0].trim().toIntOrNull() ?: return null
        val minute = if (parts.size > 1) parts[1].trim().toIntOrNull() ?: 0 else 0

        if (isPM && hour < 12) hour += 12
        if (isAM && hour == 12) {
            hour = if (isClosing) 24 else 0
        }

        return hour * 60 + minute
    }

    fun isStoreOpen(hoursStr: String, currentTime: Calendar = Calendar.getInstance()): Boolean {
        val h = hoursStr.trim().uppercase()
        if (h.contains("24/7") || h.contains("24 HOURS") || h.contains("24 HOUR") || h.contains("ALWAYS OPEN")) {
            return true
        }

        val parts = h.split("-")
        if (parts.size != 2) return true

        val openMinutes = parseTimeToMinutes(parts[0], isClosing = false) ?: return true
        val closeMinutes = parseTimeToMinutes(parts[1], isClosing = true) ?: return true

        val currentMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE)

        return if (closeMinutes > openMinutes) {
            currentMinutes in openMinutes until closeMinutes
        } else {
            currentMinutes >= openMinutes || currentMinutes < closeMinutes
        }
    }

    fun getStatusDetails(hoursStr: String, currentTime: Calendar = Calendar.getInstance()): BranchStatusDetails {
        val isOpen = isStoreOpen(hoursStr, currentTime)
        val h = hoursStr.trim()
        return if (isOpen) {
            if (h.contains("24/7", ignoreCase = true)) {
                BranchStatusDetails(isOpen = true, label = "Open 24/7", badgeColorHex = 0xFF16A34A)
            } else {
                val closeTime = h.split("-").getOrNull(1)?.trim() ?: ""
                BranchStatusDetails(
                    isOpen = true,
                    label = if (closeTime.isNotEmpty()) "Open Now · Closes $closeTime" else "Open Now",
                    badgeColorHex = 0xFF16A34A
                )
            }
        } else {
            val openTime = h.split("-").getOrNull(0)?.trim() ?: ""
            BranchStatusDetails(
                isOpen = false,
                label = if (openTime.isNotEmpty()) "Closed · Opens $openTime" else "Closed",
                badgeColorHex = 0xFF64748B
            )
        }
    }
}

// ======================================================================================
// 4. MAIN ACTIVITY & ENTRY POINT
// ======================================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authManager = AuthManager(applicationContext)

        // Automatic session routing to dedicated activities
        if (authManager.isAdminLoggedIn()) {
            val intent = Intent(this, AdminDashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
            return
        } else if (authManager.isUserLoggedIn()) {
            val intent = Intent(this, UserMainActivity::class.java).apply {
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
                    SanNoremMainController(
                        authManager = authManager,
                        onUserLoginSuccess = {
                            val intent = Intent(this, UserMainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        },
                        onOpenAdminPortal = {
                            val intent = Intent(this, AdminLoginActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

// ======================================================================================
// 5. MASTER CONTROLLER (Splash Screen, Auth, and Dashboard)
// ======================================================================================

@Composable
fun SanNoremMainController(
    authManager: AuthManager,
    onUserLoginSuccess: (UserAccount) -> Unit = {},
    onOpenAdminPortal: () -> Unit = {}
) {
    var isSplashDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(900)
        isSplashDone = true
    }

    if (!isSplashDone) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0084D6)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                    SanNoremLogo(modifier = Modifier.fillMaxSize())
                }
            }
        }
    } else {
        AuthScreen(
            authManager = authManager,
            onAuthSuccess = onUserLoginSuccess,
            onOpenAdminPortal = onOpenAdminPortal
        )
    }
}

// ======================================================================================
// 6. AUTHENTICATION SCREEN (Sign-Up & Login, Blue Theme)
// ======================================================================================

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onAuthSuccess: (UserAccount) -> Unit,
    onOpenAdminPortal: () -> Unit = {}
) {
    var isSignUpMode by rememberSaveable { mutableStateOf(false) }

    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var generalMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isGeneralError by rememberSaveable { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    fun handleAuthSubmit() {
        keyboardController?.hide()
        nameError = null
        emailError = null
        passwordError = null
        confirmPasswordError = null
        generalMessage = null

        var valid = true

        if (isSignUpMode) {
            if (fullName.trim().isEmpty()) {
                nameError = "Full Name is required"; valid = false
            }
        }

        if (email.trim().isEmpty()) {
            emailError = "Email address is required"; valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Please enter a valid email address"; valid = false
        }

        if (password.isEmpty()) {
            passwordError = "Password is required"; valid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"; valid = false
        }

        if (isSignUpMode) {
            if (confirmPassword != password) {
                confirmPasswordError = "Passwords do not match"; valid = false
            }
        }

        if (!valid) return

        isSubmitting = true
        if (isSignUpMode) {
            val registered = authManager.registerUser(fullName, email, password)
            if (registered) {
                val user = authManager.authenticate(email, password)
                if (user != null) {
                    onAuthSuccess(user)
                } else {
                    generalMessage = "Registration successful! Please log in."
                    isGeneralError = false
                    isSignUpMode = false
                }
            } else {
                generalMessage = "An account with this email already exists. Please log in."
                isGeneralError = true
            }
            isSubmitting = false
        } else {
            when (val res = authManager.login(email, password)) {
                is AuthManager.AuthResult.Success -> {
                    onAuthSuccess(res.user)
                }
                is AuthManager.AuthResult.AccountNotFound -> {
                    generalMessage = "No account found with this email. Please register first via Sign Up."
                    isGeneralError = true
                }
                is AuthManager.AuthResult.IncorrectPassword -> {
                    generalMessage = "Incorrect password. Please verify your credentials and try again."
                    isGeneralError = true
                }
                is AuthManager.AuthResult.AdminRestricted -> {
                    generalMessage = "Admin accounts must log in via the Admin Portal below."
                    isGeneralError = true
                }
            }
            isSubmitting = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0084D6))
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Original Application Logo
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.size(88.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                    SanNoremLogo(modifier = Modifier.fillMaxSize())
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SAN NOREM",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Nasugbu Store Network & Locator",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Welcome Back",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = if (isSignUpMode) "Register your account to access Nasugbu store branches"
                               else "Sign in with your registered account credentials",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    generalMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isGeneralError) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, if (isGeneralError) Color(0xFFFECACA) else Color(0xFFA7F3D0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isGeneralError) Color(0xFFB91C1C) else Color(0xFF047857),
                                    fontWeight = FontWeight.Medium
                                )
                                if (isGeneralError && msg.contains("register first", ignoreCase = true)) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            isSignUpMode = true
                                            generalMessage = null
                                        },
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                                    ) {
                                        Text(
                                            text = "Switch to Sign Up",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("Enter your name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B))
                            },
                            isError = nameError != null,
                            supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B))
                        },
                        isError = emailError != null,
                        supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("At least 6 characters") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { if (!isSignUpMode) handleAuthSubmit() }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSignUpMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Re-enter password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B))
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = confirmPasswordError != null,
                            supportingText = { confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { handleAuthSubmit() }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { handleAuthSubmit() },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text(
                                text = if (isSignUpMode) "Create Account" else "Log In",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            isSignUpMode = !isSignUpMode
                            nameError = null
                            emailError = null
                            passwordError = null
                            confirmPasswordError = null
                            generalMessage = null
                        }
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account? Log In"
                                   else "Don't have an account? Sign Up",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0084D6)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onOpenAdminPortal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF0F172A)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF0F172A)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Portal Login →",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            }
        }
    }
}

// ======================================================================================
// 6.5. FIRST-TIME ONBOARDING TUTORIAL DIALOG
// ======================================================================================

@Composable
fun OnboardingTutorialDialog(
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 24.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            text = "Step ${step + 1} of 3",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0084D6)
                        )
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Skip", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slide Graphic / Icon
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when (step) {
                            0 -> Icon(
                                Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color(0xFF0084D6),
                                modifier = Modifier.size(40.dp)
                            )
                            1 -> Icon(
                                Icons.Default.Map,
                                contentDescription = null,
                                tint = Color(0xFF0084D6),
                                modifier = Modifier.size(40.dp)
                            )
                            else -> Icon(
                                Icons.Default.NearMe,
                                contentDescription = null,
                                tint = Color(0xFF0084D6),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slide Content
                when (step) {
                    0 -> {
                        Text(
                            text = "Maligayang Pagdating sa San Norem!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Explore the complete retail convenience network of Nasugbu, Batangas. We track 31 verified official branches across 16 Alfamart, 11 DALI Everyday Grocery, and 4 7-Eleven locations.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    1 -> {
                        Text(
                            text = "Interactive Map & Live Hours",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap 'View on Map' to see any store's highlighted coverage radius. All stores monitor real-time operating hours—branches closed for the day turn black & white or can be hidden with one tap.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    else -> {
                        Text(
                            text = "'May malapit kaya?' Detector",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the 'May malapit kaya?' button to detect your live location via GPS, or tap anywhere on the OpenStreetMap to instantly rank nearest stores by real-time walking distance.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { i ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (step == i) 20.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (step == i) Color(0xFF0084D6) else Color(0xFFCBD5E1))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { step-- },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("Back", color = Color(0xFF475569))
                        }
                    }
                    Button(
                        onClick = {
                            if (step < 2) {
                                step++
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(if (step > 0) 1f else 2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                    ) {
                        Text(
                            text = if (step < 2) "Next" else "Get Started!",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ======================================================================================
// 7. POST-LOGIN DASHBOARD (Unified Blue Button Styling, Original Logo, No Title Text)
// ======================================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanNoremDashboardScreen(
    currentUser: UserAccount,
    authManager: AuthManager,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        CoverageDataStore.ensureInitialized(context)
    }

    var selectedBarangayId by remember { mutableStateOf("ALL") }
    var selectedBrand by remember { mutableStateOf<CoverageBrand?>(null) }
    var showOpenOnly by remember { mutableStateOf(false) }
    var barangayDropdownExpanded by remember { mutableStateOf(false) }

    // Multi-Service Filter & Bookmark states
    var showMultiServiceFilter by remember { mutableStateOf(false) }
    val selectedServiceFilters = remember { mutableStateListOf<String>() }
    var showBookmarkedOnly by remember { mutableStateOf(false) }

    // Suggest missing branch / request store dialog state
    var showRequestStoreDialog by remember { mutableStateOf(false) }

    // Crowdsourced Live Status Modal state
    var selectedBranchForDetails by remember { mutableStateOf<CoverageBranch?>(null) }

    // First-Time Tutorial State
    var showTutorial by remember {
        mutableStateOf(!authManager.hasSeenTutorial(currentUser.email))
    }

    if (showTutorial) {
        OnboardingTutorialDialog(
            onDismiss = {
                authManager.markTutorialSeen(currentUser.email)
                showTutorial = false
            }
        )
    }

    // Map Modal States
    var isMapModalOpen by remember { mutableStateOf(false) }
    var focusedBranchOnMap by remember { mutableStateOf<CoverageBranch?>(null) }
    var liveLocationTriggered by remember { mutableStateOf(false) }
    var userLiveGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }

    val allBranches = CoverageDataStore.activeBranches
    val allBarangays = remember { CoverageDataStore.barangays }

    // Location Permission Launcher for 'May malapit kaya?'
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            userLiveGeoPoint = GeoPoint(loc.latitude, loc.longitude)
                        } else {
                            userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
                        }
                        focusedBranchOnMap = null
                        liveLocationTriggered = true
                        isMapModalOpen = true
                    }
                    .addOnFailureListener {
                        userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
                        focusedBranchOnMap = null
                        liveLocationTriggered = true
                        isMapModalOpen = true
                    }
            } catch (_: SecurityException) {
                userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
                focusedBranchOnMap = null
                liveLocationTriggered = true
                isMapModalOpen = true
            }
        } else {
            userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
            focusedBranchOnMap = null
            liveLocationTriggered = true
            isMapModalOpen = true
        }
    }

    fun onMayMalapitKayaClicked() {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineCheck || coarseCheck) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            userLiveGeoPoint = GeoPoint(loc.latitude, loc.longitude)
                        } else {
                            userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
                        }
                        focusedBranchOnMap = null
                        liveLocationTriggered = true
                        isMapModalOpen = true
                    }
                    .addOnFailureListener {
                        userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
                        focusedBranchOnMap = null
                        liveLocationTriggered = true
                        isMapModalOpen = true
                    }
            } catch (_: SecurityException) {
                userLiveGeoPoint = GeoPoint(14.0734, 120.6315)
                focusedBranchOnMap = null
                liveLocationTriggered = true
                isMapModalOpen = true
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Filter branches based on selections, operating hours, bookmarks, and multi-service tags
    val visibleBranches = remember(
        allBranches.toList(),
        selectedBarangayId,
        selectedBrand,
        showOpenOnly,
        showBookmarkedOnly,
        selectedServiceFilters.toList(),
        CoverageDataStore.bookmarkedBranchIds.toSet(),
        CoverageDataStore.emergencyBroadcast.value
    ) {
        allBranches.filter { b ->
            val matchesBrand = selectedBrand == null || b.brand == selectedBrand
            val matchesBrgy = selectedBarangayId == "ALL" || b.brgyId == selectedBarangayId
            val effectivelyOpen = CoverageDataStore.isBranchEffectivelyOpen(b)
            val matchesOpen = !showOpenOnly || effectivelyOpen
            val matchesBookmark = !showBookmarkedOnly || CoverageDataStore.bookmarkedBranchIds.contains(b.id)
            val matchesServices = selectedServiceFilters.isEmpty() || selectedServiceFilters.all { tag ->
                b.attrs.any { it.equals(tag, ignoreCase = true) } ||
                (b.servicesStatus[tag]?.state == ServiceState.WORKING)
            }
            matchesBrand && matchesBrgy && matchesOpen && matchesBookmark && matchesServices
        }
    }

    Scaffold(
        topBar = {
            // Header Bar: Spacious top padding avoiding status bar overlap, original logo retained
            Surface(
                color = Color(0xFF0084D6), // Consistent San Norem Blue theme
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                                SanNoremLogo(modifier = Modifier.fillMaxSize())
                            }
                        }
                        Column {
                            Text(
                                text = "SAN NOREM",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Nasugbu Store Network & Locator",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // User session pill, Tutorial & Logout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = currentUser.fullName.split(" ").firstOrNull() ?: "Active",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { showTutorial = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Tutorial & Guide",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Log Out",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // MODULE 5: EMERGENCY / HOLIDAY HOURS BANNER (USER SIDE)
            item {
                EmergencyAlertBanner(
                    broadcast = CoverageDataStore.emergencyBroadcast.value,
                    onDismiss = null
                )
            }

            // MODULE 4: COMPACT BARANGAY COVERAGE CARD ON BARANGAY SELECT
            if (selectedBarangayId != "ALL") {
                item {
                    val analytics = remember(selectedBarangayId, allBranches.toList()) {
                        CoverageDataStore.getBarangayAnalytics(context, selectedBarangayId)
                    }
                    BarangayCoverageCard(
                        analytics = analytics,
                        onDismiss = { selectedBarangayId = "ALL" }
                    )
                }
            }

            // TOOLBAR / CONTROLS CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        Column {
                            Text(
                                text = "Store Branches & Service Areas",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Browse convenience stores across Nasugbu and check nearby store locations.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Prominent "May malapit kaya?" Button Feature (Consistent Blue Styling)
                        Button(
                            onClick = { onMayMalapitKayaClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "May malapit kaya?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        // Barangay Selector Dropdown
                        ExposedDropdownMenuBox(
                            expanded = barangayDropdownExpanded,
                            onExpandedChange = { barangayDropdownExpanded = !barangayDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = if (selectedBarangayId == "ALL") "All 42 barangays (${allBranches.size})"
                                        else "${CoverageDataStore.getBarangayName(selectedBarangayId)} (${allBranches.count { it.brgyId == selectedBarangayId }})",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = barangayDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF0084D6),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = barangayDropdownExpanded,
                                onDismissRequest = { barangayDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All 42 barangays (${allBranches.size})") },
                                    onClick = {
                                        selectedBarangayId = "ALL"
                                        barangayDropdownExpanded = false
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                // Poblacion Group
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "POBLACION",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0084D6)
                                        )
                                    },
                                    onClick = {},
                                    enabled = false,
                                    colors = MenuDefaults.itemColors(disabledTextColor = Color(0xFF0084D6))
                                )
                                allBarangays.filter { it.group == "Poblacion" }.forEach { brgy ->
                                    val count = allBranches.count { it.brgyId == brgy.id }
                                    DropdownMenuItem(
                                        text = { Text("${brgy.name} ($count)") },
                                        onClick = {
                                            selectedBarangayId = brgy.id
                                            barangayDropdownExpanded = false
                                        }
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                // Rural & Coastal Group
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "RURAL & COASTAL",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0084D6)
                                        )
                                    },
                                    onClick = {},
                                    enabled = false,
                                    colors = MenuDefaults.itemColors(disabledTextColor = Color(0xFF0084D6))
                                )
                                allBarangays.filter { it.group != "Poblacion" }.forEach { brgy ->
                                    val count = allBranches.count { it.brgyId == brgy.id }
                                    DropdownMenuItem(
                                        text = { Text("${brgy.name} ($count)") },
                                        onClick = {
                                            selectedBarangayId = brgy.id
                                            barangayDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Brand Tabs Pill Bar (Unified Blue selection)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BlueBrandPill(
                                    label = "All Brands",
                                    isSelected = selectedBrand == null,
                                    onClick = { selectedBrand = null }
                                )
                                BlueBrandPill(
                                    label = "7-Eleven",
                                    isSelected = selectedBrand == CoverageBrand.SEVEN_ELEVEN,
                                    onClick = { selectedBrand = CoverageBrand.SEVEN_ELEVEN }
                                )
                                BlueBrandPill(
                                    label = "Alfamart",
                                    isSelected = selectedBrand == CoverageBrand.ALFAMART,
                                    onClick = { selectedBrand = CoverageBrand.ALFAMART }
                                )
                                BlueBrandPill(
                                    label = "DALI",
                                    isSelected = selectedBrand == CoverageBrand.DALI,
                                    onClick = { selectedBrand = CoverageBrand.DALI }
                                )

                                // Real-Time Operating Hours Filter Chip
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (showOpenOnly) Color(0xFF16A34A) else Color.Transparent,
                                    border = BorderStroke(1.dp, if (showOpenOnly) Color(0xFF16A34A) else Color(0xFFCBD5E1)),
                                    modifier = Modifier.clickable { showOpenOnly = !showOpenOnly }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = if (showOpenOnly) Color.White else Color(0xFF475569),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (showOpenOnly) "Open Now (Active)" else "Open Now",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (showOpenOnly) Color.White else Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                        }

                        // MODULE 3 & MODULE 2 ACTION BAR (Multi-Service Filter, Bookmarks, and Request Missing Branch)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Multi-Service Filter Chip Button
                            val activeFiltersCount = selectedServiceFilters.size + if (showBookmarkedOnly) 1 else 0
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (activeFiltersCount > 0) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.2.dp, if (activeFiltersCount > 0) Color(0xFF0084D6) else Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { showMultiServiceFilter = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "Multi-Service Filter",
                                        tint = if (activeFiltersCount > 0) Color(0xFF0084D6) else Color(0xFF475569),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (activeFiltersCount > 0) "Services ($activeFiltersCount Active)" else "Filter Services",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeFiltersCount > 0) Color(0xFF0084D6) else Color(0xFF334155)
                                    )
                                }
                            }

                            // Bookmarked Filter Chip Toggle
                            val bookmarkedCount = CoverageDataStore.bookmarkedBranchIds.size
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (showBookmarkedOnly) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.2.dp, if (showBookmarkedOnly) Color(0xFFEF4444) else Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { showBookmarkedOnly = !showBookmarkedOnly }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showBookmarkedOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (showBookmarkedOnly) Color(0xFFEF4444) else Color(0xFF475569),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Saved ($bookmarkedCount)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (showBookmarkedOnly) Color(0xFFEF4444) else Color(0xFF334155)
                                    )
                                }
                            }

                            // Suggest Missing Branch Button
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.2.dp, Color(0xFF22C55E)),
                                modifier = Modifier.clickable { showRequestStoreDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddLocationAlt,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "+ Suggest / Request Store",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }

                        // Secondary Action Button: Open Map Overview (Blue outline)
                        OutlinedButton(
                            onClick = {
                                focusedBranchOnMap = null
                                liveLocationTriggered = false
                                userLiveGeoPoint = null
                                isMapModalOpen = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Color(0xFF0084D6))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF0084D6)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View All Stores on Map",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0084D6)
                            )
                        }
                    }
                }
            }

            // Results Counter & Description
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Showing ${visibleBranches.size} of ${allBranches.size} store branches",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Complete convenience store network across Nasugbu, Batangas. Tap 'Services' for live GCash/ATM status, or 'View on map' for GIS boundaries.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Store Branch Cards
            if (visibleBranches.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No store branches match your filter criteria.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                items(visibleBranches, key = { it.id }) { branch ->
                    UnifiedBlueBranchCard(
                        branch = branch,
                        isBookmarked = CoverageDataStore.bookmarkedBranchIds.contains(branch.id),
                        onToggleBookmark = { CoverageDataStore.toggleBookmark(branch.id, context) },
                        onViewDetails = { selectedBranchForDetails = branch },
                        onViewOnMap = {
                            focusedBranchOnMap = it
                            liveLocationTriggered = false
                            userLiveGeoPoint = null
                            isMapModalOpen = true
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // MODULE 1: Crowdsourced Service Details & User Reporting Modal
    if (selectedBranchForDetails != null) {
        val branch = selectedBranchForDetails!!
        BranchDetailsModal(
            branch = branch,
            isBookmarked = CoverageDataStore.bookmarkedBranchIds.contains(branch.id),
            onToggleBookmark = { CoverageDataStore.toggleBookmark(branch.id, context) },
            onReportService = { serviceName, state ->
                CoverageDataStore.reportServiceStatus(branch.id, serviceName, state, currentUser.email)
            },
            onViewOnMap = {
                val b = selectedBranchForDetails
                selectedBranchForDetails = null
                focusedBranchOnMap = b
                liveLocationTriggered = false
                userLiveGeoPoint = null
                isMapModalOpen = true
            },
            onDismiss = { selectedBranchForDetails = null }
        )
    }

    // MODULE 3: Multi-Service Filter Dialog
    if (showMultiServiceFilter) {
        val allTags = remember {
            (CoverageDataStore.customServiceTags + listOf(
                "GCash Cash-In", "ATM", "Cliqq Kiosk", "Bills Payment",
                "Parking", "Frozen Meat", "Pharmacy Counter", "Open 24/7"
            )).distinct()
        }
        MultiServiceFilterDialog(
            availableTags = allTags,
            selectedCriteria = selectedServiceFilters.toSet(),
            bookmarkedOnly = showBookmarkedOnly,
            onToggleBookmarkOnly = { showBookmarkedOnly = !showBookmarkedOnly },
            onToggleCriterion = { tag ->
                if (selectedServiceFilters.contains(tag)) {
                    selectedServiceFilters.remove(tag)
                } else {
                    selectedServiceFilters.add(tag)
                }
            },
            onReset = {
                selectedServiceFilters.clear()
                showBookmarkedOnly = false
            },
            onDismiss = { showMultiServiceFilter = false }
        )
    }

    // MODULE 2: Request Store / Suggest Missing Branch Dialog
    if (showRequestStoreDialog) {
        RequestStoreDialog(
            initialLocation = null,
            initialBarangayId = selectedBarangayId,
            currentUserEmail = currentUser.email,
            onDismiss = { showRequestStoreDialog = false },
            onSubmit = { type, brgyId, brand, name, lat, lng, note ->
                CoverageDataStore.submitBranchSuggestion(
                    requestType = type,
                    brgyId = brgyId,
                    preferredBrand = brand,
                    branchName = name,
                    lat = lat,
                    lng = lng,
                    note = note,
                    userEmail = currentUser.email
                )
                showRequestStoreDialog = false
            }
        )
    }

    // Map Modal Dialog
    if (isMapModalOpen) {
        Dialog(
            onDismissRequest = { isMapModalOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                BlueThemedCoverageMapModal(
                    allStoreBranches = allBranches,
                    initialFocusBranch = focusedBranchOnMap,
                    initialUserLocation = userLiveGeoPoint,
                    isMayMalapitKaya = liveLocationTriggered,
                    selectedBarangayId = selectedBarangayId,
                    onClose = { isMapModalOpen = false }
                )
            }
        }
    }
}

// ======================================================================================
// 8. UNIFIED BLUE STORE BRANCH CARD
// ======================================================================================

@Composable
fun UnifiedBlueBranchCard(
    branch: CoverageBranch,
    isBookmarked: Boolean = false,
    onToggleBookmark: () -> Unit = {},
    onViewDetails: () -> Unit = {},
    onViewOnMap: (CoverageBranch) -> Unit
) {
    val status = CoverageDataStore.getEffectiveStatusDetails(branch)
    val brandBadgeBg = Color(branch.brand.badgeBg)
    val brandBadgeBorder = Color(branch.brand.badgeBorder)
    val brandColor = Color(branch.brand.hexColor)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Brand badge & bookmark heart & status dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = brandBadgeBg,
                            border = BorderStroke(1.dp, brandBadgeBorder)
                        ) {
                            Text(
                                text = branch.brand.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = brandColor
                            )
                        }

                        // Real-Time Operating Hours Badge (Respects Emergency / Holiday Closures)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (status.isOpen) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (status.isOpen) Color(0xFFA7F3D0) else Color(0xFFCBD5E1))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (status.isOpen) Color(0xFF16A34A) else Color(0xFF64748B))
                                )
                                Text(
                                    text = status.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (status.isOpen) Color(0xFF15803D) else Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = branch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (status.isOpen) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (status.isOpen) Color(0xFFBFDBFE) else Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = if (status.isOpen) "Verified Store · Active Branch" else "Verified Store · Currently Closed",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (status.isOpen) Color(0xFF0084D6) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Bookmark Heart Icon & Brand Dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Bookmark Store",
                            tint = if (isBookmarked) Color(0xFFEF4444) else Color(0xFF94A3B8)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (status.isOpen) brandColor else Color(0xFF64748B))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Details rows
            CleanRow(label = "Operating Hours:", value = branch.hours)
            CleanRow(label = "Barangay:", value = CoverageDataStore.getBarangayName(branch.brgyId))
            CleanRow(label = "Service Coverage:", value = "${branch.radiusKm} km radius")

            // MODULE 1: Live Services Status Row
            if (branch.servicesStatus.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "LIVE SERVICES AVAILABILITY",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    branch.servicesStatus.forEach { (srvName, srvItem) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(srvItem.state.colorHex).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color(srvItem.state.colorHex).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(srvItem.state.colorHex))
                                )
                                Text(
                                    text = "$srvName: ${srvItem.state.label}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(srvItem.state.colorHex)
                                )
                            }
                        }
                    }
                }
            }

            if (branch.attrs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "STORE AMENITIES & HIGHLIGHTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                branch.attrs.forEach { attr ->
                    Text(
                        text = attr.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0084D6)
                    )
                }
            }

            if (branch.strategy.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "COMMUNITY PROFILE:",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = branch.strategy,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: "Live Services" button + "View on map →" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.2.dp, Color(0xFF0084D6)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color(0xFF0084D6)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Services",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0084D6)
                    )
                }

                Button(
                    onClick = { onViewOnMap(branch) },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status.isOpen) Color(0xFF0084D6) else Color(0xFF475569)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (status.isOpen) "View on map →" else "Map (Closed) →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(0.58f)
        )
    }
}

@Composable
private fun BlueBrandPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF0084D6) else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color(0xFF64748B)
        )
    }
}

// ======================================================================================
// 9. ENHANCED OPENSTREETMAP MODAL ('May malapit kaya?' Live Detection & Zooming)
// ======================================================================================

enum class StoreStatusFilter(val label: String) {
    ALL("All Stores"),
    OPEN_ONLY("Open Now"),
    CLOSED_ONLY("Closed Only")
}

@Composable
fun BrandFilterToggleChip(
    label: String,
    count: Int,
    isActive: Boolean,
    brandColor: Color,
    onToggle: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isActive) brandColor else Color.White,
        animationSpec = tween(durationMillis = 250),
        label = "brandBg"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isActive) Color.White else Color(0xFF334155),
        animationSpec = tween(durationMillis = 250),
        label = "brandText"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isActive) brandColor else Color(0xFFCBD5E1),
        animationSpec = tween(durationMillis = 250),
        label = "brandBorder"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = animatedBg,
        border = BorderStroke(1.2.dp, animatedBorderColor),
        shadowElevation = if (isActive) 3.dp else 0.dp,
        modifier = Modifier.clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color.White else brandColor)
            )
            Text(
                text = "$label ($count)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = animatedTextColor
            )
        }
    }
}

@Composable
fun HoursFilterToggleChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.White,
        animationSpec = tween(durationMillis = 250),
        label = "hoursBg"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF334155),
        animationSpec = tween(durationMillis = 250),
        label = "hoursText"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color(0xFFCBD5E1),
        animationSpec = tween(durationMillis = 250),
        label = "hoursBorder"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = animatedBg,
        border = BorderStroke(1.2.dp, animatedBorderColor),
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.Visibility else Icons.Default.AccessTime,
                contentDescription = null,
                tint = if (isSelected) Color.White else activeColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "$label ($count)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = animatedTextColor
            )
        }
    }
}

@Composable
fun BlueThemedCoverageMapModal(
    allStoreBranches: List<CoverageBranch>,
    initialFocusBranch: CoverageBranch?,
    initialUserLocation: GeoPoint?,
    isMayMalapitKaya: Boolean,
    selectedBarangayId: String = "ALL",
    onClose: () -> Unit
) {
    val context = LocalContext.current

    // Interactive Brand Visibility Toggles
    var showSevenEleven by remember { mutableStateOf(true) }
    var showAlfamart by remember { mutableStateOf(true) }
    var showDali by remember { mutableStateOf(true) }

    // Real-Time Store Status Filter (All, Open Only, Closed Only)
    var statusFilter by remember { mutableStateOf(StoreStatusFilter.ALL) }

    // Multi-Service Filter & Bookmarks in Map
    var showMultiServiceFilter by remember { mutableStateOf(false) }
    val selectedServiceFilters = remember { mutableStateListOf<String>() }
    var showBookmarkedOnly by remember { mutableStateOf(false) }

    // Suggest Store / Missing Branch dialog in Map
    var showRequestStoreDialog by remember { mutableStateOf(false) }
    var requestLocationForDialog by remember { mutableStateOf<GeoPoint?>(null) }

    // Crowdsourced Live Status Modal
    var selectedBranchForDetails by remember { mutableStateOf<CoverageBranch?>(null) }

    val baseBarangayBranches = remember(selectedBarangayId, allStoreBranches) {
        if (selectedBarangayId == "ALL") allStoreBranches
        else CoverageDataStore.filterBranchesInBarangayPolygon(context, selectedBarangayId, allStoreBranches)
    }

    val totalOpenCount = remember(baseBarangayBranches, CoverageDataStore.emergencyBroadcast.value) {
        baseBarangayBranches.count { CoverageDataStore.isBranchEffectivelyOpen(it) }
    }
    val totalClosedCount = remember(baseBarangayBranches, CoverageDataStore.emergencyBroadcast.value) {
        baseBarangayBranches.count { !CoverageDataStore.isBranchEffectivelyOpen(it) }
    }

    val branchesToDisplay = remember(
        baseBarangayBranches,
        showSevenEleven,
        showAlfamart,
        showDali,
        statusFilter,
        selectedServiceFilters.toList(),
        showBookmarkedOnly,
        CoverageDataStore.bookmarkedBranchIds.toSet(),
        CoverageDataStore.emergencyBroadcast.value
    ) {
        baseBarangayBranches.filter { b ->
            val brandMatch = when (b.brand) {
                CoverageBrand.SEVEN_ELEVEN -> showSevenEleven
                CoverageBrand.ALFAMART -> showAlfamart
                CoverageBrand.DALI -> showDali
            }
            val effectivelyOpen = CoverageDataStore.isBranchEffectivelyOpen(b)
            val statusMatch = when (statusFilter) {
                StoreStatusFilter.ALL -> true
                StoreStatusFilter.OPEN_ONLY -> effectivelyOpen
                StoreStatusFilter.CLOSED_ONLY -> !effectivelyOpen
            }
            val bookmarkMatch = !showBookmarkedOnly || CoverageDataStore.bookmarkedBranchIds.contains(b.id)
            val servicesMatch = selectedServiceFilters.isEmpty() || selectedServiceFilters.all { tag ->
                b.attrs.any { it.equals(tag, ignoreCase = true) } ||
                (b.servicesStatus[tag]?.state == ServiceState.WORKING)
            }
            brandMatch && statusMatch && bookmarkMatch && servicesMatch
        }
    }
    var activeRadiusBranch by remember { mutableStateOf<CoverageBranch?>(initialFocusBranch) }
    var customerLocation by remember { mutableStateOf<GeoPoint?>(initialUserLocation) }
    var coveringResults by remember {
        mutableStateOf<List<CoveringBranchResult>>(
            if (initialUserLocation != null) {
                CoverageDataStore.findNearbyAll(initialUserLocation.latitude, initialUserLocation.longitude, allStoreBranches)
            } else emptyList()
        )
    }

    val pinDrawables = remember(context) {
        mapOf(
            CoverageBrand.SEVEN_ELEVEN to createStorePinDrawable(context, 0xFFEA580C.toInt(), CoverageBrand.SEVEN_ELEVEN, isClosed = false),
            CoverageBrand.ALFAMART to createStorePinDrawable(context, 0xFFDC2626.toInt(), CoverageBrand.ALFAMART, isClosed = false),
            CoverageBrand.DALI to createStorePinDrawable(context, 0xFF2563EB.toInt(), CoverageBrand.DALI, isClosed = false)
        )
    }
    val closedPinDrawables = remember(context) {
        mapOf(
            CoverageBrand.SEVEN_ELEVEN to createStorePinDrawable(context, 0xFFEA580C.toInt(), CoverageBrand.SEVEN_ELEVEN, isClosed = true),
            CoverageBrand.ALFAMART to createStorePinDrawable(context, 0xFFDC2626.toInt(), CoverageBrand.ALFAMART, isClosed = true),
            CoverageBrand.DALI to createStorePinDrawable(context, 0xFF2563EB.toInt(), CoverageBrand.DALI, isClosed = true)
        )
    }
    val customerPinDrawable = remember(context) { createCustomerPinDrawable(context) }

    val barangayBox = remember(selectedBarangayId) {
        if (selectedBarangayId != "ALL") CoverageDataStore.getBarangayBoundingBox(context, selectedBarangayId) else null
    }

    val featureCenter = remember(initialFocusBranch, initialUserLocation, barangayBox, selectedBarangayId) {
        if (initialFocusBranch != null) {
            GeoPoint(initialFocusBranch.lat, initialFocusBranch.lng)
        } else if (barangayBox != null) {
            GeoPoint((barangayBox.latNorth + barangayBox.latSouth) / 2.0, (barangayBox.lonEast + barangayBox.lonWest) / 2.0)
        } else if (initialUserLocation != null) {
            initialUserLocation
        } else {
            GeoPoint(14.0734, 120.6315)
        }
    }

    val initialZoom = remember(initialFocusBranch, isMayMalapitKaya, barangayBox) {
        if (initialFocusBranch != null) 16.5
        else if (isMayMalapitKaya) 15.5
        else if (barangayBox != null) 15.2
        else 14.0
    }

    val mapView = remember {
        Configuration.getInstance().load(
            context, context.getSharedPreferences("osmdroid_blue", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true

            controller.setZoom(initialZoom)
            controller.setCenter(featureCenter)
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    // Function to hold and frame the feature with smooth camera animation
    fun frameFeature(animated: Boolean = true) {
        if (initialFocusBranch != null) {
            activeRadiusBranch = initialFocusBranch
            mapView.controller.animateTo(GeoPoint(initialFocusBranch.lat, initialFocusBranch.lng), 16.5, 750L)
        } else if (isMayMalapitKaya && initialUserLocation != null) {
            customerLocation = initialUserLocation
            coveringResults = CoverageDataStore.findNearbyAll(initialUserLocation.latitude, initialUserLocation.longitude, allStoreBranches)
            mapView.controller.animateTo(initialUserLocation, 15.5, 750L)
        } else if (barangayBox != null) {
            val center = GeoPoint((barangayBox.latNorth + barangayBox.latSouth) / 2.0, (barangayBox.lonEast + barangayBox.lonWest) / 2.0)
            if (mapView.isLaidOut && mapView.width > 0 && mapView.height > 0) {
                mapView.zoomToBoundingBox(barangayBox, animated, 120)
            } else {
                mapView.controller.setCenter(center)
                mapView.controller.setZoom(15.2)
                mapView.post {
                    if (mapView.isLaidOut) {
                        mapView.zoomToBoundingBox(barangayBox, animated, 120)
                    }
                }
            }
        } else {
            mapView.controller.animateTo(GeoPoint(14.0734, 120.6315), 14.0, 750L)
        }
    }

    // Direct Zoom & Service Area Highlight on focus
    LaunchedEffect(initialFocusBranch, selectedBarangayId, initialUserLocation) {
        frameFeature(animated = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Modal Header with high-visibility, high-contrast filter button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMayMalapitKaya) "Mga Malapit na Tindahan sa Iyo"
                           else if (selectedBarangayId != "ALL") "${CoverageDataStore.getBarangayName(selectedBarangayId)} GIS Boundary"
                           else "Nasugbu Store Network Map",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = if (selectedBarangayId != "ALL") {
                        val brgyName = CoverageDataStore.getBarangayName(selectedBarangayId)
                        "${branchesToDisplay.size} branch(es) · $totalOpenCount Open, $totalClosedCount Closed inside $brgyName"
                    } else if (isMayMalapitKaya) {
                        "Live location detected · Pinakamalapit na Alfamart, DALI, at 7-Eleven"
                    } else {
                        "${branchesToDisplay.size} branch(es) · $totalOpenCount Open, $totalClosedCount Closed across Nasugbu"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = Color(0xFF0084D6)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // High-visibility, prominent filter pill button (high contrast, blue border/fill, clear icon)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (statusFilter) {
                        StoreStatusFilter.ALL -> Color(0xFFEFF6FF)
                        StoreStatusFilter.OPEN_ONLY -> Color(0xFF0084D6)
                        StoreStatusFilter.CLOSED_ONLY -> Color(0xFF475569)
                    },
                    border = BorderStroke(1.5.dp, Color(0xFF0084D6)),
                    shadowElevation = if (statusFilter != StoreStatusFilter.ALL) 3.dp else 1.dp,
                    modifier = Modifier.clickable {
                        statusFilter = when (statusFilter) {
                            StoreStatusFilter.ALL -> StoreStatusFilter.OPEN_ONLY
                            StoreStatusFilter.OPEN_ONLY -> StoreStatusFilter.CLOSED_ONLY
                            StoreStatusFilter.CLOSED_ONLY -> StoreStatusFilter.ALL
                        }
                        frameFeature(animated = true)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = when (statusFilter) {
                                StoreStatusFilter.ALL -> Icons.Default.AccessTime
                                StoreStatusFilter.OPEN_ONLY -> Icons.Default.Visibility
                                StoreStatusFilter.CLOSED_ONLY -> Icons.Default.VisibilityOff
                            },
                            contentDescription = null,
                            tint = if (statusFilter == StoreStatusFilter.ALL) Color(0xFF0084D6) else Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = when (statusFilter) {
                                StoreStatusFilter.ALL -> "Filter: All"
                                StoreStatusFilter.OPEN_ONLY -> "Open Only ($totalOpenCount)"
                                StoreStatusFilter.CLOSED_ONLY -> "Closed Only ($totalClosedCount)"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (statusFilter == StoreStatusFilter.ALL) Color(0xFF0084D6) else Color.White
                        )
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF0F172A))
                }
            }
        }
        HorizontalDivider(color = Color(0xFFE2E8F0))

        // Body: Map & Overlays (Full height map with floating top filter bar)
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView },
                update = { map ->
                    map.overlays.clear()

                    val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            activeRadiusBranch = null
                            customerLocation = p
                            requestLocationForDialog = p
                            coveringResults = CoverageDataStore.findNearbyAll(p.latitude, p.longitude, allStoreBranches)
                            map.controller.animateTo(p, map.zoomLevelDouble, 500L)
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean {
                            requestLocationForDialog = p
                            showRequestStoreDialog = true
                            return true
                        }
                    })
                    map.overlays.add(eventsOverlay)

                    // Barangay Geographic Boundary Polygon (Sleek translucent fill & sharp GIS stroke)
                    if (selectedBarangayId != "ALL") {
                        val boundaryPoints = CoverageDataStore.getBarangayPolygon(context, selectedBarangayId)
                        if (boundaryPoints != null && boundaryPoints.isNotEmpty()) {
                            val boundaryPolygon = Polygon(map).apply {
                                points = boundaryPoints
                                val baseColor = android.graphics.Color.parseColor("#0084D6")
                                val fillColorInt = (0x24 shl 24) or (baseColor and 0x00FFFFFF)
                                fillPaint.color = fillColorInt
                                outlinePaint.color = baseColor
                                outlinePaint.strokeWidth = 5f
                            }
                            map.overlays.add(boundaryPolygon)
                        }
                    }

                    // Active Service Area Radius Circle
                    activeRadiusBranch?.let { branch ->
                        val isOpen = CoverageDataStore.isBranchEffectivelyOpen(branch)
                        val circle = Polygon(map).apply {
                            val center = GeoPoint(branch.lat, branch.lng)
                            points = Polygon.pointsAsCircle(center, branch.radiusKm * 1000.0)
                            val baseColor = if (isOpen) branch.brand.hexColor.toInt() else android.graphics.Color.rgb(100, 116, 139)
                            val fillColorInt = (0x28 shl 24) or (baseColor and 0x00FFFFFF)
                            fillPaint.color = fillColorInt
                            outlinePaint.color = baseColor
                            outlinePaint.strokeWidth = 4f
                            setOnClickListener { _, _, _ ->
                                activeRadiusBranch = null
                                true
                            }
                        }
                        map.overlays.add(circle)
                    }

                    // Customer Live / Selected Location Marker
                    customerLocation?.let { loc ->
                        val userMarker = Marker(map).apply {
                            position = loc
                            title = if (isMayMalapitKaya) "Ang Iyong Lokasyon" else "Selected Location"
                            snippet = "Scanning nearby branches..."
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            icon = customerPinDrawable
                        }
                        map.overlays.add(userMarker)
                    }

                    // Store Branch Markers (Filtered strictly to selected barangay polygon or all if "ALL")
                    branchesToDisplay.forEach { branch ->
                        val isOpen = CoverageDataStore.isBranchEffectivelyOpen(branch)
                        val status = CoverageDataStore.getEffectiveStatusDetails(branch)
                        val marker = Marker(map).apply {
                            position = GeoPoint(branch.lat, branch.lng)
                            title = branch.name
                            snippet = "${branch.brand.label} · ${status.label} · ${branch.radiusKm} km radius"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = if (isOpen) pinDrawables[branch.brand] else closedPinDrawables[branch.brand]
                            setOnMarkerClickListener { _, _ ->
                                activeRadiusBranch = branch
                                map.controller.animateTo(GeoPoint(branch.lat, branch.lng), 16.5, 600L)
                                true
                            }
                        }
                        map.overlays.add(marker)
                    }

                    map.postInvalidate()
                }
            )

            // Pinned Floating Filter Bar on the TOP OF THE MAP (Maximizes map view & holds feature)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.96f),
                shadowElevation = 5.dp,
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Brands:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )

                    // 7-Eleven Toggle
                    BrandFilterToggleChip(
                        label = "7-Eleven",
                        count = baseBarangayBranches.count { it.brand == CoverageBrand.SEVEN_ELEVEN },
                        isActive = showSevenEleven,
                        brandColor = Color(0xFFEA580C),
                        onToggle = {
                            showSevenEleven = !showSevenEleven
                            frameFeature(animated = true)
                        }
                    )

                    // Alfamart Toggle
                    BrandFilterToggleChip(
                        label = "Alfamart",
                        count = baseBarangayBranches.count { it.brand == CoverageBrand.ALFAMART },
                        isActive = showAlfamart,
                        brandColor = Color(0xFFDC2626),
                        onToggle = {
                            showAlfamart = !showAlfamart
                            frameFeature(animated = true)
                        }
                    )

                    // DALI Toggle
                    BrandFilterToggleChip(
                        label = "DALI",
                        count = baseBarangayBranches.count { it.brand == CoverageBrand.DALI },
                        isActive = showDali,
                        brandColor = Color(0xFF2563EB),
                        onToggle = {
                            showDali = !showDali
                            frameFeature(animated = true)
                        }
                    )

                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .width(1.dp)
                            .background(Color(0xFFCBD5E1))
                    )

                    Text(
                        text = "Hours:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )

                    // Status Filter Chips
                    HoursFilterToggleChip(
                        label = "All",
                        count = baseBarangayBranches.size,
                        isSelected = statusFilter == StoreStatusFilter.ALL,
                        activeColor = Color(0xFF0084D6),
                        onClick = {
                            statusFilter = StoreStatusFilter.ALL
                            frameFeature(animated = true)
                        }
                    )
                    HoursFilterToggleChip(
                        label = "Open Now",
                        count = totalOpenCount,
                        isSelected = statusFilter == StoreStatusFilter.OPEN_ONLY,
                        activeColor = Color(0xFF16A34A),
                        onClick = {
                            statusFilter = StoreStatusFilter.OPEN_ONLY
                            frameFeature(animated = true)
                        }
                    )
                    HoursFilterToggleChip(
                        label = "Closed",
                        count = totalClosedCount,
                        isSelected = statusFilter == StoreStatusFilter.CLOSED_ONLY,
                        activeColor = Color(0xFF475569),
                        onClick = {
                            statusFilter = StoreStatusFilter.CLOSED_ONLY
                            frameFeature(animated = true)
                        }
                    )

                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .width(1.dp)
                            .background(Color(0xFFCBD5E1))
                    )

                    // Multi-Service Filter Chip in Map
                    val mapServiceCount = selectedServiceFilters.size + if (showBookmarkedOnly) 1 else 0
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (mapServiceCount > 0) Color(0xFF0084D6) else Color.White,
                        border = BorderStroke(1.2.dp, Color(0xFF0084D6)),
                        shadowElevation = if (mapServiceCount > 0) 3.dp else 0.dp,
                        modifier = Modifier.clickable { showMultiServiceFilter = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = if (mapServiceCount > 0) Color.White else Color(0xFF0084D6),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (mapServiceCount > 0) "Services ($mapServiceCount)" else "Services",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (mapServiceCount > 0) Color.White else Color(0xFF0084D6)
                            )
                        }
                    }

                    // Request Store Chip in Map
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.2.dp, Color(0xFF22C55E)),
                        modifier = Modifier.clickable {
                            requestLocationForDialog = customerLocation ?: featureCenter
                            showRequestStoreDialog = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddLocationAlt,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "+ Suggest Store",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }

            // Map Zoom & Re-Center Controls (+ / - / Hold Feature)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { mapView.controller.zoomIn(500L) }) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color(0xFF0084D6))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { mapView.controller.zoomOut(500L) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color(0xFF0084D6))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = { frameFeature(animated = true) }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Hold & Re-center Feature", tint = Color(0xFF0084D6))
                    }
                }
            }

            // Active Highlight Banner with live status and smooth animation
            androidx.compose.animation.AnimatedVisibility(
                visible = activeRadiusBranch != null,
                enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp, start = 14.dp, end = 14.dp)
            ) {
                activeRadiusBranch?.let { activeBranch ->
                    val branchStatus = CoverageDataStore.getEffectiveStatusDetails(activeBranch)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.96f),
                        shadowElevation = 6.dp,
                        border = BorderStroke(1.2.dp, if (branchStatus.isOpen) Color(0xFF0084D6) else Color(0xFF64748B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeBranch.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${activeBranch.brand.label} · ${branchStatus.label} · Area: ${activeBranch.radiusKm} km",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = if (branchStatus.isOpen) Color(0xFF0084D6) else Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { selectedBranchForDetails = activeBranch },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Services", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { activeRadiusBranch = null },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear Highlight", tint = Color(0xFF64748B))
                                }
                            }
                        }
                    }
                }
            }

            // Friendly Empty State overlay if active filters match 0 stores
            if (branchesToDisplay.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.5.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = Color(0xFF0084D6),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = if (statusFilter == StoreStatusFilter.CLOSED_ONLY) "Walang saradong tindahan sa oras na ito!"
                                   else if (statusFilter == StoreStatusFilter.OPEN_ONLY) "Walang bukas na tindahan sa kasalukuyan."
                                   else "Walang tindahang tumutugma sa filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = if (statusFilter == StoreStatusFilter.CLOSED_ONLY) "Lahat ng $totalOpenCount sangay ay bukas ngayon."
                                   else "Subukang i-reset ang filter upang makita ang lahat ng sangay.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = {
                                showSevenEleven = true
                                showAlfamart = true
                                showDali = true
                                statusFilter = StoreStatusFilter.ALL
                                frameFeature(animated = true)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Ipakita Lahat (Reset)", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Results Bottom Panel
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (customerLocation == null) {
                    Text(
                        text = "Select any point on the map or tap 'May malapit kaya?' to detect nearby stores.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMayMalapitKaya) "Pinakamalapit na mga Tindahan sa Lokasyon Mo:"
                                   else "Nearby Store Branches:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Text(
                                text = "Nasugbu Area",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0084D6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val displayCoveringResults = remember(coveringResults, showSevenEleven, showAlfamart, showDali, statusFilter) {
                        coveringResults.filter { res ->
                            val brandMatch = when (res.branch.brand) {
                                CoverageBrand.SEVEN_ELEVEN -> showSevenEleven
                                CoverageBrand.ALFAMART -> showAlfamart
                                CoverageBrand.DALI -> showDali
                            }
                            val statusMatch = when (statusFilter) {
                                StoreStatusFilter.ALL -> true
                                StoreStatusFilter.OPEN_ONLY -> OperatingHoursHelper.isStoreOpen(res.branch.hours)
                                StoreStatusFilter.CLOSED_ONLY -> !OperatingHoursHelper.isStoreOpen(res.branch.hours)
                            }
                            brandMatch && statusMatch
                        }
                    }

                    // List of closest branches sorted by real distance
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        displayCoveringResults.take(4).forEach { res ->
                            val brandColor = Color(res.branch.brand.hexColor)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeRadiusBranch = res.branch
                                        mapView.controller.animateTo(GeoPoint(res.branch.lat, res.branch.lng), 16.5, 600L)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(brandColor)
                                        )
                                        Column {
                                            Text(
                                                text = res.branch.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "${res.branch.brand.label} · ${CoverageDataStore.getBarangayName(res.branch.brgyId)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (res.distanceKm < 1.0) "${Math.round(res.distanceKm * 1000)} m"
                                               else "${"%.1f".format(res.distanceKm)} km",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0084D6)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // MODULE 1: Live Store Status & Services Report Modal in Map
    if (selectedBranchForDetails != null) {
        val branch = selectedBranchForDetails!!
        BranchDetailsModal(
            branch = branch,
            isBookmarked = CoverageDataStore.bookmarkedBranchIds.contains(branch.id),
            onToggleBookmark = { CoverageDataStore.toggleBookmark(branch.id, context) },
            onReportService = { serviceName, state ->
                CoverageDataStore.reportServiceStatus(branch.id, serviceName, state, "map_user@sannorem.com")
            },
            onViewOnMap = {
                activeRadiusBranch = branch
                mapView.controller.animateTo(GeoPoint(branch.lat, branch.lng), 16.5, 600L)
                selectedBranchForDetails = null
            },
            onDismiss = { selectedBranchForDetails = null }
        )
    }

    // MODULE 3: Multi-Service Filter Modal in Map
    if (showMultiServiceFilter) {
        val allTags = remember {
            (CoverageDataStore.customServiceTags + listOf(
                "GCash Cash-In", "ATM", "Cliqq Kiosk", "Bills Payment",
                "Parking", "Frozen Meat", "Pharmacy Counter", "Open 24/7"
            )).distinct()
        }
        MultiServiceFilterDialog(
            availableTags = allTags,
            selectedCriteria = selectedServiceFilters.toSet(),
            bookmarkedOnly = showBookmarkedOnly,
            onToggleBookmarkOnly = { showBookmarkedOnly = !showBookmarkedOnly },
            onToggleCriterion = { tag ->
                if (selectedServiceFilters.contains(tag)) {
                    selectedServiceFilters.remove(tag)
                } else {
                    selectedServiceFilters.add(tag)
                }
            },
            onReset = {
                selectedServiceFilters.clear()
                showBookmarkedOnly = false
            },
            onDismiss = { showMultiServiceFilter = false }
        )
    }

    // MODULE 2: Suggest Missing Store / Request Pin Dialog in Map
    if (showRequestStoreDialog) {
        RequestStoreDialog(
            initialLocation = requestLocationForDialog,
            initialBarangayId = selectedBarangayId,
            currentUserEmail = "guest_locator@sannorem.com",
            onDismiss = { showRequestStoreDialog = false },
            onSubmit = { type, brgyId, brand, name, lat, lng, note ->
                CoverageDataStore.submitBranchSuggestion(
                    requestType = type,
                    brgyId = brgyId,
                    preferredBrand = brand,
                    branchName = name,
                    lat = lat,
                    lng = lng,
                    note = note,
                    userEmail = "guest_locator@sannorem.com"
                )
                showRequestStoreDialog = false
            }
        )
    }
}

// ======================================================================================
// 10. VECTOR CANVAS DRAWABLE HELPERS
// ======================================================================================

fun createStorePinDrawable(
    context: Context,
    colorInt: Int,
    brand: CoverageBrand? = null,
    isClosed: Boolean = false
): Drawable {
    val width = 72
    val height = 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val tipX = width / 2f
    val tipY = height - 10f
    val headRadius = 24f
    val headCenterY = 32f

    // 1. Drop shadow on the ground below the pin
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(85, 15, 23, 42)
        style = Paint.Style.FILL
    }
    canvas.drawOval(tipX - 14f, tipY + 1f, tipX + 14f, tipY + 8f, shadowPaint)

    // 2. Classic Location Pointer Pin Teardrop Path
    val pinPath = Path().apply {
        moveTo(tipX, tipY)
        // Left side curve upwards into circular head
        cubicTo(
            tipX - 6f, tipY - 18f,
            tipX - headRadius - 2f, headCenterY + headRadius * 0.7f,
            tipX - headRadius, headCenterY
        )
        // Circular head top arc
        arcTo(
            tipX - headRadius, headCenterY - headRadius,
            tipX + headRadius, headCenterY + headRadius,
            180f, 180f, false
        )
        // Right side curve downwards back to tip
        cubicTo(
            tipX + headRadius + 2f, headCenterY + headRadius * 0.7f,
            tipX + 6f, tipY - 18f,
            tipX, tipY
        )
        close()
    }

    // 3. Crisp White Outer Pin Border
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 5f
    paint.color = android.graphics.Color.WHITE
    paint.strokeJoin = Paint.Join.ROUND
    canvas.drawPath(pinPath, paint)

    // 4. Pin Body Solid Fill (Brand Color or Dark Slate if closed)
    paint.style = Paint.Style.FILL
    paint.color = if (isClosed) android.graphics.Color.rgb(51, 65, 85) else colorInt
    canvas.drawPath(pinPath, paint)

    // 5. Inner White Circular Badge in Head
    val badgeRadius = 15f
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(tipX, headCenterY, badgeRadius, paint)

    // 6. Brand Symbol / Letter / Icon inside Badge
    if (isClosed) {
        val symbolPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(100, 116, 139)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val textY = headCenterY - (symbolPaint.descent() + symbolPaint.ascent()) / 2f
        canvas.drawText("✕", tipX, textY, symbolPaint)
    } else {
        val label = when (brand) {
            CoverageBrand.SEVEN_ELEVEN -> "7"
            CoverageBrand.ALFAMART -> "A"
            CoverageBrand.DALI -> "D"
            null -> "•"
        }
        val symbolPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colorInt
            textSize = 19f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val textY = headCenterY - (symbolPaint.descent() + symbolPaint.ascent()) / 2f
        canvas.drawText(label, tipX, textY, symbolPaint)
    }

    return BitmapDrawable(context.resources, bitmap)
}

fun createCustomerPinDrawable(context: Context): Drawable {
    val width = 72
    val height = 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val tipX = width / 2f
    val tipY = height - 10f
    val headRadius = 24f
    val headCenterY = 32f

    // Shadow
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(85, 15, 23, 42)
        style = Paint.Style.FILL
    }
    canvas.drawOval(tipX - 14f, tipY + 1f, tipX + 14f, tipY + 8f, shadowPaint)

    // Pointer pin path
    val pinPath = Path().apply {
        moveTo(tipX, tipY)
        cubicTo(
            tipX - 6f, tipY - 18f,
            tipX - headRadius - 2f, headCenterY + headRadius * 0.7f,
            tipX - headRadius, headCenterY
        )
        arcTo(
            tipX - headRadius, headCenterY - headRadius,
            tipX + headRadius, headCenterY + headRadius,
            180f, 180f, false
        )
        cubicTo(
            tipX + headRadius + 2f, headCenterY + headRadius * 0.7f,
            tipX + 6f, tipY - 18f,
            tipX, tipY
        )
        close()
    }

    // Outer white border
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 5f
    paint.color = android.graphics.Color.WHITE
    paint.strokeJoin = Paint.Join.ROUND
    canvas.drawPath(pinPath, paint)

    // Vibrant San Norem Blue Pin Body
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.parseColor("#0084D6")
    canvas.drawPath(pinPath, paint)

    // White badge
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(tipX, headCenterY, 15f, paint)

    // Inner pulsating navigation target dot
    paint.color = android.graphics.Color.parseColor("#0084D6")
    canvas.drawCircle(tipX, headCenterY, 8f, paint)

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(tipX, headCenterY, 3.5f, paint)

    return BitmapDrawable(context.resources, bitmap)
}
