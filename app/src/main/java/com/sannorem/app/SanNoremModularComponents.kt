package com.sannorem.app

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.osmdroid.util.GeoPoint

// ======================================================================================
// 6.5. MODULAR DIALOGS & COMPONENTS (Modules 1 - 5)
// ======================================================================================

// MODULE 5: EMERGENCY / HOLIDAY HOURS BROADCAST BANNER (USER SIDE)
@Composable
fun EmergencyAlertBanner(
    broadcast: EmergencyBroadcast,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    if (!broadcast.isActive) return

    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Alert",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (broadcast.title.isNotBlank()) broadcast.title else "EMERGENCY ADVISORY",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )
                        if (broadcast.isTemporaryClosedOverride) {
                            Text(
                                text = "Temporary Closed Override Active (Pins turned B&W)",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = Color(0xFF991B1B)
                        )
                    }
                    if (onDismiss != null) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF991B1B))
                        }
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFFECACA))
                Spacer(modifier = Modifier.height(8.dp))

                if (broadcast.message.isNotBlank()) {
                    Text(
                        text = broadcast.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7F1D1D),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                val affectedScope = buildString {
                    append("Scope: ")
                    if (broadcast.affectedBarangayIds.isEmpty()) {
                        append("All 42 Barangays")
                    } else {
                        append(broadcast.affectedBarangayIds.joinToString(", ") { CoverageDataStore.getBarangayName(it) })
                    }
                    append(" | Chains: ")
                    if (broadcast.affectedBrands.isEmpty()) {
                        append("All Brands")
                    } else {
                        append(broadcast.affectedBrands.joinToString(", ") { it.label })
                    }
                }
                Text(
                    text = affectedScope,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = Color(0xFFB91C1C),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// MODULE 4: BARANGAY COVERAGE ANALYTICS CARD (USER SIDE)
@Composable
fun BarangayCoverageCard(
    analytics: BarangayCoverageAnalytics,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.98f),
        border = BorderStroke(1.5.dp, Color(analytics.statusColorHex)),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(analytics.statusColorHex))
                    )
                    Column {
                        Text(
                            text = "${analytics.brgyName} GIS Coverage",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${analytics.group} · Nasugbu Boundary",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(analytics.statusColorHex).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(analytics.statusColorHex))
                    ) {
                        Text(
                            text = analytics.status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(analytics.statusColorHex)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text(
                            "${analytics.totalBranches}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0084D6)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Alfamart", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text(
                            "${analytics.alfamartCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("DALI", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text(
                            "${analytics.daliCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF7ED),
                    border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("7-Eleven", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text(
                            "${analytics.sevenElevenCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA580C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (analytics.status) {
                    "Well-Served" -> "High retail density in this barangay. Stores are easily accessible from the community."
                    "Moderate" -> "1-2 active branches. Sufficient for essential groceries, but open to additional neighborhood stores."
                    else -> "Underserved: No active branch within this boundary polygon. Primary target for store requests."
                },
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

// MODULE 3: MULTI-SERVICE TRIP PLANNER & DYNAMIC SERVICE TAGS DIALOG (USER SIDE)
@Composable
fun MultiServiceFilterDialog(
    availableTags: List<String>,
    selectedCriteria: Set<String>,
    bookmarkedOnly: Boolean,
    onToggleBookmarkOnly: () -> Unit,
    onToggleCriterion: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Multi-Service Trip Planner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Filter branches matching ALL selected criteria",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(14.dp))

                // Bookmarked Only quick toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (bookmarkedOnly) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (bookmarkedOnly) Color(0xFFEF4444) else Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleBookmarkOnly() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (bookmarkedOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (bookmarkedOnly) Color(0xFFDC2626) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "My Bookmarked / Go-To Stores Only",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (bookmarkedOnly) Color(0xFFDC2626) else Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Select Amenities & Services:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Chips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = selectedCriteria.contains(tag)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF0084D6) else Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleCriterion(tag) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF0084D6) else Color(0xFF1E293B)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF0084D6),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset", color = Color(0xFF64748B))
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                    ) {
                        val totalSelected = selectedCriteria.size + if (bookmarkedOnly) 1 else 0
                        Text(
                            text = if (totalSelected > 0) "Apply ($totalSelected)" else "Done",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// MODULE 2: "SUGGEST A MISSING BRANCH / BARANGAY REQUEST" DIALOG (USER SIDE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestStoreDialog(
    initialLocation: GeoPoint?,
    initialBarangayId: String,
    currentUserEmail: String,
    onDismiss: () -> Unit,
    onSubmit: (StoreRequestType, String, String, String, Double, Double, String) -> Unit
) {
    var requestType by remember { mutableStateOf(StoreRequestType.UNDERSERVED_AREA) }
    var selectedBrgyId by remember {
        mutableStateOf(if (initialBarangayId != "ALL") initialBarangayId else "wawa")
    }
    var selectedBrand by remember { mutableStateOf("Any") }
    var branchName by remember { mutableStateOf("") }
    var latText by remember { mutableStateOf(String.format(java.util.Locale.US, "%.5f", initialLocation?.latitude ?: 14.0734)) }
    var lngText by remember { mutableStateOf(String.format(java.util.Locale.US, "%.5f", initialLocation?.longitude ?: 120.6315)) }
    var note by remember { mutableStateOf("") }
    var brgyDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Suggest Store / Demand Map",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Help retailers pinpoint high-demand areas in Nasugbu",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Request Type Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Request Type:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { requestType = StoreRequestType.UNDERSERVED_AREA },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = requestType == StoreRequestType.UNDERSERVED_AREA,
                            onClick = { requestType = StoreRequestType.UNDERSERVED_AREA },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0084D6))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Underserved Area - Need Store Here", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { requestType = StoreRequestType.NEWLY_OPENED },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = requestType == StoreRequestType.NEWLY_OPENED,
                            onClick = { requestType = StoreRequestType.NEWLY_OPENED },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0084D6))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Report Newly Opened Branch", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Barangay Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Select Barangay:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    ExposedDropdownMenuBox(
                        expanded = brgyDropdownExpanded,
                        onExpandedChange = { brgyDropdownExpanded = !brgyDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = CoverageDataStore.getBarangayName(selectedBrgyId),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = brgyDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = brgyDropdownExpanded,
                            onDismissRequest = { brgyDropdownExpanded = false }
                        ) {
                            CoverageDataStore.barangays.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b.name) },
                                    onClick = {
                                        selectedBrgyId = b.id
                                        brgyDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Preferred Brand
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Preferred Retail Brand:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Alfamart", "DALI", "7-Eleven", "Any").forEach { brand ->
                            val isSel = selectedBrand == brand
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF0084D6) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF0084D6) else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedBrand = brand }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = brand,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }

                if (requestType == StoreRequestType.NEWLY_OPENED) {
                    OutlinedTextField(
                        value = branchName,
                        onValueChange = { branchName = it },
                        label = { Text("Branch Name / Proposed Title") },
                        placeholder = { Text("e.g. Alfamart Market Road") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Coordinates
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latText,
                        onValueChange = { latText = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = lngText,
                        onValueChange = { lngText = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Notes
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note / Why is a store needed here?") },
                    placeholder = { Text("e.g., Near school and church, no groceries nearby.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2
                )

                // Submit button
                Button(
                    onClick = {
                        val lat = latText.toDoubleOrNull() ?: 14.0734
                        val lng = lngText.toDoubleOrNull() ?: 120.6315
                        onSubmit(requestType, selectedBrgyId, selectedBrand, branchName, lat, lng, note)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                ) {
                    Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Store Request", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// MODULE 1 & 3: BRANCH DETAILS DIALOG (ADMIN-MANAGED SERVICES & RICH STORE PROFILE)
@Composable
fun BranchDetailsModal(
    branch: CoverageBranch,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onReportService: (String, ServiceState) -> Unit = { _, _ -> },
    onViewOnMap: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val status = CoverageDataStore.getEffectiveStatusDetails(branch)
    val brandColor = Color(branch.brand.hexColor)
    var reportingService by remember { mutableStateOf<String?>(null) }
    var reportFeedbackMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // RICH BRAND HERO BANNER (Fills and maximizes the top area cleanly)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(brandColor, brandColor.copy(alpha = 0.85f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = brandColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = branch.brand.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Nasugbu Neighborhood Network",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        // Bookmark and Close Buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                IconButton(onClick = onToggleBookmark) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isBookmarked) Color(0xFFFF4D4F) else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                IconButton(onClick = onDismiss) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // MAIN CONTENT SECTION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Branch Name & Live Status Pills
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = branch.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )

                        // Badges Row
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (status.isOpen) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                                border = BorderStroke(1.dp, if (status.isOpen) Color(0xFFA7F3D0) else Color(0xFFFECACA))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (status.isOpen) Color(0xFF16A34A) else Color(0xFFDC2626))
                                    )
                                    Text(
                                        text = status.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (status.isOpen) Color(0xFF15803D) else Color(0xFFDC2626)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(
                                    text = "Reach: ${branch.radiusKm} km",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0084D6)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = "Brgy. ${CoverageDataStore.getBarangayName(branch.brgyId)}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }

                    // Location & Operating Schedule Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF0084D6), modifier = Modifier.size(15.dp))
                                Text(
                                    text = "Brgy. ${CoverageDataStore.getBarangayName(branch.brgyId)}, Nasugbu, Batangas",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF0084D6), modifier = Modifier.size(15.dp))
                                Text(
                                    text = "Store Hours: ${branch.hours}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                            if (branch.strategy.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF0084D6), modifier = Modifier.size(15.dp))
                                    Text(
                                        text = branch.strategy,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    // STORE AMENITIES & FEATURES (if any)
                    if (branch.attrs.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Store Amenities 🏪",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                branch.attrs.forEach { attr ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(11.dp))
                                            Text(
                                                text = attr,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF334155),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // ADMIN-CONTROLLED DYNAMIC SERVICES & PERKS SECTION
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available Services & Perks 🌟",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Live Verified",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0084D6)
                            )
                        }
                        Text(
                            text = "Verified live status managed directly by store operations",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Strictly show ONLY services explicitly enabled/toggled by admin for this store ID
                    val services = branch.servicesStatus

                    if (services.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Standard Neighborhood Shopping Ready! 🛒",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Store is open and ready to serve your daily grocery essentials.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            services.forEach { (serviceName, itemStatus) ->
                                val (bg, border, textCol) = when (itemStatus.state) {
                                    ServiceState.WORKING -> Triple(Color(0xFFDCFCE7), Color(0xFF86EFAC), Color(0xFF15803D)) // Green
                                    ServiceState.BUSY -> Triple(Color(0xFFFFEDD5), Color(0xFFFDBA74), Color(0xFFC2410C))    // Orange
                                    ServiceState.OFFLINE -> Triple(Color(0xFFFEE2E2), Color(0xFFFCA5A5), Color(0xFFB91C1C)) // Red
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = serviceName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1E293B)
                                                )
                                                Text(
                                                    text = "Community & Admin Monitored",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = bg,
                                                border = BorderStroke(1.dp, border)
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
                                                            .background(textCol)
                                                    )
                                                    Text(
                                                        text = itemStatus.state.label,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = textCol
                                                    )
                                                }
                                            }
                                        }

                                        // Interactive "Report Status" Button & Picker Action
                                        if (reportingService == serviceName) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFEFF6FF),
                                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = "Select current condition for $serviceName:",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E3A8A)
                                                    )
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        // Working
                                                        Button(
                                                            onClick = {
                                                                onReportService(serviceName, ServiceState.WORKING)
                                                                reportingService = null
                                                                reportFeedbackMessage = "Reported: $serviceName is Working"
                                                            },
                                                            modifier = Modifier.weight(1f).height(32.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = PaddingValues(0.dp)
                                                        ) {
                                                            Text("Working", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                        }
                                                        // Busy
                                                        Button(
                                                            onClick = {
                                                                onReportService(serviceName, ServiceState.BUSY)
                                                                reportingService = null
                                                                reportFeedbackMessage = "Reported: $serviceName has long queue / busy"
                                                            },
                                                            modifier = Modifier.weight(1f).height(32.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = PaddingValues(0.dp)
                                                        ) {
                                                            Text("Busy", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                        }
                                                        // Offline
                                                        Button(
                                                            onClick = {
                                                                onReportService(serviceName, ServiceState.OFFLINE)
                                                                reportingService = null
                                                                reportFeedbackMessage = "Reported: $serviceName is Offline"
                                                            },
                                                            modifier = Modifier.weight(1f).height(32.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                            shape = RoundedCornerShape(6.dp),
                                                            contentPadding = PaddingValues(0.dp)
                                                        ) {
                                                            Text("Offline", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = { reportingService = serviceName },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(28.dp),
                                                    border = BorderStroke(1.dp, Color(0xFF0084D6))
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Campaign,
                                                        contentDescription = null,
                                                        tint = Color(0xFF0084D6),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "Report Status",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 10.sp,
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

                    // Temporary user feedback message after reporting
                    reportFeedbackMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                    Text(text = msg, style = MaterialTheme.typography.labelSmall, color = Color(0xFF15803D), fontWeight = FontWeight.Medium)
                                }
                                IconButton(
                                    onClick = { reportFeedbackMessage = null },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF15803D), modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    if (onViewOnMap != null) {
                        Button(
                            onClick = onViewOnMap,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pin on Interactive Map 🧭", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
