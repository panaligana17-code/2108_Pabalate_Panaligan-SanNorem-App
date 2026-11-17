package com.sannorem.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

// MODULE 4: COVERAGE SIMULATOR OPENSTREETMAP VIEW (ADMIN SIDE - STRICT PER-BRANCH VISUALIZATION)
@Composable
fun CoverageSimulatorMapView(
    branches: List<CoverageBranch>,
    selectedBranchId: String? = null,
    previewRadiusKm: Double? = null
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

            // Prevent parent scroll container from intercepting pan/zoom gestures
            setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN, android.view.MotionEvent.ACTION_MOVE -> {
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        v.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }

            controller.setZoom(13.6)
            controller.setCenter(GeoPoint(14.0734, 120.6315))
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    // Auto-focus on selected branch when chosen by admin
    LaunchedEffect(selectedBranchId) {
        val target = branches.find { it.id == selectedBranchId }
        if (target != null) {
            mapView.controller.animateTo(GeoPoint(target.lat, target.lng), 14.8, 500L)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { map ->
                map.overlays.clear()

                // Draw individual buffer circles around store pins (Strict Per-Branch constraint)
                branches.forEach { branch ->
                    val isSelected = branch.id == selectedBranchId
                    val effectiveRadiusM = if (isSelected && previewRadiusKm != null) previewRadiusKm * 1000.0 else branch.radiusKm * 1000.0
                    val circle = Polygon(map).apply {
                        val center = GeoPoint(branch.lat, branch.lng)
                        points = Polygon.pointsAsCircle(center, effectiveRadiusM)
                        val baseColor = branch.brand.hexColor.toInt()
                        val alpha = if (isSelected) 0x48 else 0x1E
                        val fillColorInt = (alpha shl 24) or (baseColor and 0x00FFFFFF)
                        fillPaint.color = fillColorInt
                        outlinePaint.color = baseColor
                        outlinePaint.strokeWidth = if (isSelected) 5f else 2.5f
                    }
                    map.overlays.add(circle)

                    val marker = Marker(map).apply {
                        position = GeoPoint(branch.lat, branch.lng)
                        title = branch.name
                        snippet = "${branch.brand.label} · Radius: ${effectiveRadiusM.toInt()}m"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    }
                    map.overlays.add(marker)
                }
                map.postInvalidate()
            }
        )

        // Overlay tactile zoom and center controls for smooth map navigation
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.size(34.dp)
            ) {
                IconButton(onClick = { mapView.controller.zoomIn(400L) }) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color(0xFF0084D6), modifier = Modifier.size(16.dp))
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.size(34.dp)
            ) {
                IconButton(onClick = { mapView.controller.zoomOut(400L) }) {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0084D6)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.size(34.dp)
            ) {
                IconButton(onClick = {
                    val target = branches.find { it.id == selectedBranchId }
                    if (target != null) {
                        mapView.controller.animateTo(GeoPoint(target.lat, target.lng), 14.8, 400L)
                    } else {
                        mapView.controller.animateTo(GeoPoint(14.0734, 120.6315), 13.6, 400L)
                    }
                }) {
                    Icon(Icons.Default.Storefront, contentDescription = "Focus Branch", tint = Color(0xFF0084D6), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ======================================================================================
// 6.6. SAN NOREM ADMIN DASHBOARD / CONTROL CENTER (Modules 1 - 5)
// ======================================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanNoremAdminDashboard(
    onBackToUserApp: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        "Live Reports",
        "Demand Requests",
        "Tag Manager",
        "Coverage Simulator",
        "Emergency Override"
    )

    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0084D6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "SAN NOREM ADMIN",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Store Operations & GIS Demand Center",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Button(
                            onClick = onBackToUserApp,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log Out", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab bar with horizontal scrolling to prevent awkward multi-line letter breaking
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF0084D6),
                        edgePadding = 12.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = Color(0xFF0084D6),
                                    height = 3.dp
                                )
                            }
                        }
                    ) {
                        tabTitles.forEachIndexed { idx, title ->
                            Tab(
                                selected = selectedTab == idx,
                                onClick = { selectedTab = idx },
                                modifier = Modifier.padding(horizontal = 6.dp),
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        softWrap = false,
                                        fontWeight = if (selectedTab == idx) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (selectedTab == idx) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AdminLiveReportsTab()
                1 -> AdminDemandRequestsTab()
                2 -> AdminServiceTagManagerTab()
                3 -> AdminCoverageSimulatorTab()
                4 -> AdminEmergencyOverrideTab()
            }
        }
    }
}

// TAB 1: LIVE STATUS & REPORTS (MODULE 1 ADMIN)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLiveReportsTab() {
    val context = LocalContext.current
    val reports = CoverageDataStore.serviceReports
    val activeBranches = CoverageDataStore.activeBranches

    var selectedBranchId by remember { mutableStateOf(activeBranches.firstOrNull()?.id ?: "") }
    var selectedServiceName by remember { mutableStateOf("GCash Cash-In") }
    var branchDropdownOpen by remember { mutableStateOf(false) }

    val currentSelectedBranch = activeBranches.find { it.id == selectedBranchId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Auto-Rule Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0084D6), modifier = Modifier.size(24.dp))
                    Column {
                        Text(
                            text = "Auto-Rule Active: 3 Offline Reports = OFFLINE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0084D6)
                        )
                        Text(
                            text = "When 3 users report a service as 'Offline', the system automatically updates the branch's service status badge to OFFLINE across all maps and user search results.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }
        }

        // Manual Real-Time Service Override Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Manual Real-Time Service Override",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Select any branch and manually override its service status in real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    // Branch selector
                    ExposedDropdownMenuBox(
                        expanded = branchDropdownOpen,
                        onExpandedChange = { branchDropdownOpen = !branchDropdownOpen }
                    ) {
                        OutlinedTextField(
                            value = currentSelectedBranch?.name ?: "Select Store",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchDropdownOpen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = branchDropdownOpen,
                            onDismissRequest = { branchDropdownOpen = false }
                        ) {
                            activeBranches.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text("${b.brand.label} - ${b.name}") },
                                    onClick = {
                                        selectedBranchId = b.id
                                        selectedServiceName = b.servicesStatus.keys.firstOrNull() ?: "GCash Cash-In"
                                        branchDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Service selector chips
                    if (currentSelectedBranch != null) {
                        Text("Select Service to Override:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentSelectedBranch.servicesStatus.keys.forEach { sName ->
                                val isSel = selectedServiceName == sName
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color(0xFF0084D6) else Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFF0084D6) else Color(0xFFCBD5E1)),
                                    modifier = Modifier.clickable { selectedServiceName = sName }
                                ) {
                                    Text(
                                        text = sName,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }

                        val currentStatus = currentSelectedBranch.servicesStatus[selectedServiceName]?.state ?: ServiceState.WORKING

                        Text(
                            text = "Current State: ${currentStatus.label} (Offline votes: ${currentSelectedBranch.servicesStatus[selectedServiceName]?.offlineReports ?: 0})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        )

                        // Override action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    CoverageDataStore.adminOverrideServiceStatus(selectedBranchId, selectedServiceName, ServiceState.WORKING)
                                    Toast.makeText(context, "$selectedServiceName set to WORKING", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set Working", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    CoverageDataStore.adminOverrideServiceStatus(selectedBranchId, selectedServiceName, ServiceState.BUSY)
                                    Toast.makeText(context, "$selectedServiceName set to BUSY", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set Busy", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    CoverageDataStore.adminOverrideServiceStatus(selectedBranchId, selectedServiceName, ServiceState.OFFLINE)
                                    Toast.makeText(context, "$selectedServiceName set to OFFLINE", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set Offline", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Live Incoming User Reports Stream
        item {
            Text(
                text = "Incoming User Reports Queue (${reports.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        if (reports.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No reports submitted yet.", color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(reports, key = { it.id }) { report ->
                val (bg, textCol) = when (report.reportedState) {
                    ServiceState.WORKING -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                    ServiceState.BUSY -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
                    ServiceState.OFFLINE -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = report.branchName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Service: ${report.serviceName} · Submitter: ${report.userEmail}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = bg
                        ) {
                            Text(
                                text = report.reportedState.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = textCol
                            )
                        }
                    }
                }
            }
        }
    }
}

// TAB 2: DEMAND MAPPING & PIN REQUESTS (MODULE 2 ADMIN)
@Composable
fun AdminDemandRequestsTab() {
    val context = LocalContext.current
    var subTab by remember { mutableStateOf(0) } // 0: Underserved Ranking, 1: Pending Submissions Queue
    val allRequests = CoverageDataStore.demandRequests

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Sub-Tab Switcher
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFE2E8F0),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (subTab == 0) Color(0xFF0084D6) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { subTab = 0 }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Underserved Ranking",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (subTab == 0) Color.White else Color(0xFF475569)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (subTab == 1) Color(0xFF0084D6) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { subTab = 1 }
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        val pendingCount = allRequests.count { it.requestType == StoreRequestType.NEWLY_OPENED && it.status == "PENDING" }
                        Text(
                            text = "Pending Submissions ($pendingCount)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (subTab == 1) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        if (subTab == 0) {
            // View A: Underserved Barangays Ranking
            val underservedRequests = remember(allRequests) {
                allRequests.filter { it.requestType == StoreRequestType.UNDERSERVED_AREA }
            }
            val rankedBarangays = remember(underservedRequests) {
                CoverageDataStore.barangays.map { b ->
                    val count = underservedRequests.count { it.brgyId == b.id }
                    val currentBranchCount = CoverageDataStore.activeBranches.count { it.brgyId == b.id }
                    Triple(b, count, currentBranchCount)
                }.sortedByDescending { it.second }
            }

            Text(
                text = "Underserved Barangays Demand Ranking",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rankedBarangays.take(15)) { (brgy, reqCount, branchCount) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = brgy.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Current branches: $branchCount · Category: ${brgy.group}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (branchCount == 0) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEF2F2),
                                        border = BorderStroke(1.dp, Color(0xFFFECACA))
                                    ) {
                                        Text(
                                            text = "Dead Zone",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                ) {
                                    Text(
                                        text = "$reqCount Request(s)",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0084D6)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // View B: Pending Branch Submissions Queue
            val pendingQueue = remember(allRequests) {
                allRequests.filter { it.requestType == StoreRequestType.NEWLY_OPENED && it.status == "PENDING" }
            }

            Text(
                text = "Pending Branch Verification Queue (${pendingQueue.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            if (pendingQueue.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No pending branch submissions to review.", color = Color(0xFF64748B))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pendingQueue, key = { it.id }) { req ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (req.branchName.isNotBlank()) req.branchName else "${req.preferredBrand} ${req.brgyName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEFF6FF),
                                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                    ) {
                                        Text(
                                            text = req.preferredBrand,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0084D6)
                                        )
                                    }
                                }

                                Text(
                                    text = "Barangay: ${req.brgyName} · Coordinates: (${"%.4f".format(req.lat)}, ${"%.4f".format(req.lng)})",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )

                                if (req.note.isNotBlank()) {
                                    Text(
                                        text = "Note: ${req.note}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            CoverageDataStore.rejectBranchSubmission(req)
                                            Toast.makeText(context, "Submission rejected", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Reject", color = Color(0xFFDC2626))
                                    }
                                    Button(
                                        onClick = {
                                            val newB = CoverageDataStore.approveBranchSubmission(req)
                                            Toast.makeText(context, "Branch Approved! Added to active map.", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Approve & Pin", fontWeight = FontWeight.Bold, color = Color.White)
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

// TAB 3: DYNAMIC PER-BRANCH SERVICE & AMENITIES MANAGER (MODULE 3 ADMIN - STRICT PER-BRANCH RULE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceTagManagerTab() {
    val context = LocalContext.current
    val activeBranches = CoverageDataStore.activeBranches
    var selectedBranchId by remember { mutableStateOf(activeBranches.firstOrNull()?.id ?: "") }
    var branchDropdownOpen by remember { mutableStateOf(false) }
    var newAmenityInput by remember { mutableStateOf("") }

    val currentBranch = activeBranches.find { it.id == selectedBranchId } ?: activeBranches.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branch Selector Card (Enforcing Strict Per-Branch Management)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Individual Store Management (Per-Branch Rule)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "All amenities and service indicators apply strictly and exclusively to the selected store branch ID.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    ExposedDropdownMenuBox(
                        expanded = branchDropdownOpen,
                        onExpandedChange = { branchDropdownOpen = !branchDropdownOpen },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = currentBranch?.let { "${it.name} (${it.brand.label} · ${CoverageDataStore.getBarangayName(it.brgyId)})" } ?: "Select Branch",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selected Branch ID") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchDropdownOpen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = branchDropdownOpen,
                            onDismissRequest = { branchDropdownOpen = false }
                        ) {
                            activeBranches.forEach { b ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(b.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                            Text("${b.brand.label} · ${CoverageDataStore.getBarangayName(b.brgyId)} · Radius: ${b.radiusKm}km", fontSize = 10.sp, color = Color(0xFF64748B))
                                        }
                                    },
                                    onClick = {
                                        selectedBranchId = b.id
                                        branchDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (currentBranch != null) {
            // Section 1: Branch Specific Amenities & Custom Tags
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Branch Amenities & Tags: ${currentBranch.name}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        // Current Tags on this Branch
                        if (currentBranch.attrs.isEmpty()) {
                            Text("No amenities assigned to this store.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                currentBranch.attrs.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFEFF6FF),
                                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(tag, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0084D6))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove tag",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        CoverageDataStore.removeBranchAmenity(currentBranch.id, tag)
                                                        Toast.makeText(context, "Removed '$tag' from ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Quick Add Popular Amenities to this Branch
                        Text("Quick Add Amenity to this Store:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        val popularTags = listOf("ATM", "GCash Cash-In", "Cliqq Kiosk", "Bills Payment", "Parking", "Pharmacy Counter", "Ice Cream", "E-Load", "Frozen Meat")
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            popularTags.forEach { pTag ->
                                val alreadyHas = currentBranch.attrs.contains(pTag)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (alreadyHas) Color(0xFFF1F5F9) else Color.White,
                                    border = BorderStroke(1.dp, if (alreadyHas) Color(0xFFCBD5E1) else Color(0xFF0084D6)),
                                    modifier = Modifier.clickable(enabled = !alreadyHas) {
                                        CoverageDataStore.addBranchAmenity(currentBranch.id, pTag)
                                        Toast.makeText(context, "Added '$pTag' to ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (alreadyHas) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                                        } else {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF0084D6), modifier = Modifier.size(12.dp))
                                        }
                                        Text(
                                            pTag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (alreadyHas) Color(0xFF64748B) else Color(0xFF0084D6),
                                            fontWeight = if (alreadyHas) FontWeight.Normal else FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Custom Tag Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newAmenityInput,
                                onValueChange = { newAmenityInput = it },
                                placeholder = { Text("Custom tag (e.g. WiFi, Drive-Thru)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Button(
                                onClick = {
                                    if (newAmenityInput.isNotBlank()) {
                                        CoverageDataStore.addBranchAmenity(currentBranch.id, newAmenityInput.trim())
                                        CoverageDataStore.createCustomServiceTag(newAmenityInput.trim())
                                        newAmenityInput = ""
                                        Toast.makeText(context, "Added to ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Add", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Section 2: Branch Specific Live Operational Service Statuses
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Live Service Statuses: ${currentBranch.name}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Only services explicitly activated here will appear on the customer app for this store ID.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )

                        // Quick Activate Services for this Branch
                        Text(
                            text = "Activate Services for this Branch:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        val standardServices = listOf("GCash Cash-In", "ATM", "Cliqq Kiosk", "Bills Payment", "Parking", "Frozen Meat", "Fresh Produce & Meat", "Ice Cream", "E-Load")
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            standardServices.forEach { sTag ->
                                val isAssigned = currentBranch.servicesStatus.containsKey(sTag)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isAssigned) Color(0xFFDCFCE7) else Color.White,
                                    border = BorderStroke(1.dp, if (isAssigned) Color(0xFF16A34A) else Color(0xFF0084D6)),
                                    modifier = Modifier.clickable {
                                        if (isAssigned) {
                                            CoverageDataStore.removeBranchService(currentBranch.id, sTag)
                                            Toast.makeText(context, "Deactivated '$sTag' on ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                        } else {
                                            CoverageDataStore.setBranchServiceStatus(currentBranch.id, sTag, ServiceState.WORKING)
                                            Toast.makeText(context, "Activated '$sTag' on ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (isAssigned) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                                        } else {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF0084D6), modifier = Modifier.size(12.dp))
                                        }
                                        Text(
                                            sTag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isAssigned) Color(0xFF15803D) else Color(0xFF0084D6),
                                            fontWeight = if (isAssigned) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        val services = currentBranch.servicesStatus

                        if (services.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No live services currently published for this store. Tap any service above to activate it for customer view.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            services.forEach { (sName, sStatus) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(sName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                                IconButton(
                                                    onClick = {
                                                        CoverageDataStore.removeBranchService(currentBranch.id, sName)
                                                        Toast.makeText(context, "Removed $sName from ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Deactivate Service", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                                }
                                            }
                                            val (badgeBg, badgeCol) = when (sStatus.state) {
                                                ServiceState.WORKING -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                                                ServiceState.BUSY -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
                                                ServiceState.OFFLINE -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
                                            }
                                            Surface(shape = RoundedCornerShape(6.dp), color = badgeBg) {
                                                Text(
                                                    sStatus.state.label,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeCol
                                                )
                                            }
                                        }

                                        // 3 Toggle buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    CoverageDataStore.setBranchServiceStatus(currentBranch.id, sName, ServiceState.WORKING)
                                                    Toast.makeText(context, "$sName set to Working on ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Working", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = {
                                                    CoverageDataStore.setBranchServiceStatus(currentBranch.id, sName, ServiceState.BUSY)
                                                    Toast.makeText(context, "$sName set to Busy on ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Busy", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = {
                                                    CoverageDataStore.setBranchServiceStatus(currentBranch.id, sName, ServiceState.OFFLINE)
                                                    Toast.makeText(context, "$sName set to Offline on ${currentBranch.name}", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Offline", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
}

// TAB 4: STRICT PER-BRANCH COVERAGE RADIUS SIMULATOR & EDITOR (MODULE 4 ADMIN)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCoverageSimulatorTab() {
    val context = LocalContext.current
    val activeBranches = CoverageDataStore.activeBranches
    var selectedBranchId by remember { mutableStateOf(activeBranches.firstOrNull()?.id ?: "") }
    var branchDropdownOpen by remember { mutableStateOf(false) }

    val selectedBranch = activeBranches.find { it.id == selectedBranchId } ?: activeBranches.firstOrNull()
    var previewRadiusKm by remember(selectedBranch?.id) {
        mutableDoubleStateOf(selectedBranch?.radiusKm ?: 1.0)
    }
    var showCsvExportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Controls Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Individual Branch Coverage Radius",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Strict Per-Branch GIS Radius Constraint",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0084D6)
                            )
                        }

                        Button(
                            onClick = { showCsvExportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export CSV", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Branch Picker Dropdown
                    ExposedDropdownMenuBox(
                        expanded = branchDropdownOpen,
                        onExpandedChange = { branchDropdownOpen = !branchDropdownOpen },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedBranch?.let { "${it.name} (${it.brand.label} · ${CoverageDataStore.getBarangayName(it.brgyId)})" } ?: "Select Branch",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Branch to Edit Radius") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchDropdownOpen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = branchDropdownOpen,
                            onDismissRequest = { branchDropdownOpen = false }
                        ) {
                            activeBranches.forEach { b ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(b.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                            Text("${b.brand.label} · ${CoverageDataStore.getBarangayName(b.brgyId)} · Current: ${b.radiusKm} km", fontSize = 10.sp, color = Color(0xFF64748B))
                                        }
                                    },
                                    onClick = {
                                        selectedBranchId = b.id
                                        previewRadiusKm = b.radiusKm
                                        branchDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Quick Radius Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            0.5 to "500m (Standard)",
                            1.0 to "1.0 km (Town)",
                            1.5 to "1.5 km (Highway)",
                            2.0 to "2.0 km (Regional)"
                        ).forEach { (r, label) ->
                            val isSel = Math.abs(previewRadiusKm - r) < 0.05
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF0084D6) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF0084D6) else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { previewRadiusKm = r }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    // Slider with real-time value display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Target Radius for Selected Branch:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        Text(
                            text = "${"%.1f".format(previewRadiusKm)} km (${(previewRadiusKm * 1000).toInt()} m)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0084D6)
                        )
                    }

                    Slider(
                        value = previewRadiusKm.toFloat(),
                        onValueChange = { previewRadiusKm = it.toDouble() },
                        valueRange = 0.3f..3.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0084D6),
                            activeTrackColor = Color(0xFF0084D6)
                        )
                    )

                    // Save strictly for selected branch
                    Button(
                        onClick = {
                            selectedBranch?.let { b ->
                                CoverageDataStore.updateBranchRadius(b.id, previewRadiusKm)
                                Toast.makeText(context, "Saved! ${b.name} radius set to ${"%.1f".format(previewRadiusKm)} km", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Coverage Radius for ${selectedBranch?.name ?: "Branch"}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        // Rectangular Map Card Container with fixed height (Neatly bounded)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) {
                CoverageSimulatorMapView(
                    branches = CoverageDataStore.activeBranches,
                    selectedBranchId = selectedBranch?.id,
                    previewRadiusKm = previewRadiusKm
                )
            }
        }
    }

    // CSV Export Summary Dialog
    if (showCsvExportDialog) {
        val csvContent = remember {
            buildString {
                appendLine("Barangay,Group,Total Branches,Alfamart,DALI,7-Eleven,Coverage Status")
                CoverageDataStore.barangays.forEach { b ->
                    val stats = CoverageDataStore.getBarangayAnalytics(context, b.id)
                    appendLine("\"${stats.brgyName}\",\"${stats.group}\",${stats.totalBranches},${stats.alfamartCount},${stats.daliCount},${stats.sevenElevenCount},\"${stats.status}\"")
                }
            }
        }

        Dialog(onDismissRequest = { showCsvExportDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Barangay Coverage Summary & CSV",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        IconButton(onClick = { showCsvExportDialog = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Text(
                        text = "Coverage statistics for all 42 barangays across Nasugbu, Batangas:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = csvContent,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCsvExportDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Close", color = Color(0xFF64748B))
                        }
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("San Norem Coverage CSV", csvContent)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084D6))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy CSV", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// TAB 5: BROADCAST & EMERGENCY OVERRIDE (MODULE 5 ADMIN)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmergencyOverrideTab() {
    val context = LocalContext.current
    val currentBroadcast = CoverageDataStore.emergencyBroadcast.value

    var titleInput by remember { mutableStateOf(currentBroadcast.title.ifBlank { "Power Interruption in Nasugbu" }) }
    var messageInput by remember { mutableStateOf(currentBroadcast.message.ifBlank { "Emergency power maintenance underway. Selected stores temporarily closed until 8:00 PM." }) }
    var affectedBrgyId by remember { mutableStateOf(currentBroadcast.affectedBarangayIds.firstOrNull() ?: "ALL") }
    var affectedBrand by remember { mutableStateOf<CoverageBrand?>(currentBroadcast.affectedBrands.firstOrNull()) }
    var isOverrideClosed by remember { mutableStateOf(currentBroadcast.isTemporaryClosedOverride) }

    var brgyDropdownOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (currentBroadcast.isActive) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                border = BorderStroke(1.5.dp, if (currentBroadcast.isActive) Color(0xFFEF4444) else Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (currentBroadcast.isActive) Icons.Default.Campaign else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (currentBroadcast.isActive) Color(0xFFDC2626) else Color(0xFF16A34A),
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = if (currentBroadcast.isActive) "EMERGENCY BROADCAST ACTIVE" else "No Emergency Broadcast Active",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (currentBroadcast.isActive) Color(0xFF991B1B) else Color(0xFF166534)
                        )
                        Text(
                            text = if (currentBroadcast.isActive && currentBroadcast.isTemporaryClosedOverride)
                                       "Affected stores are overridden as TEMPORARILY CLOSED with B&W map pins."
                                   else if (currentBroadcast.isActive)
                                       "Alert banner is broadcasting to all app users."
                                   else
                                       "All branches operating on standard business hours.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = if (currentBroadcast.isActive) Color(0xFFB91C1C) else Color(0xFF15803D)
                        )
                    }
                }
            }
        }

        // Form Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Broadcast Announcement Controls",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Broadcast Banner Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        label = { Text("Announcement Message") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    // Target Barangay
                    Text("Affected Barangay Scope:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    ExposedDropdownMenuBox(
                        expanded = brgyDropdownOpen,
                        onExpandedChange = { brgyDropdownOpen = !brgyDropdownOpen }
                    ) {
                        OutlinedTextField(
                            value = if (affectedBrgyId == "ALL") "All 42 Barangays (Municipality-Wide)" else CoverageDataStore.getBarangayName(affectedBrgyId),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brgyDropdownOpen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = brgyDropdownOpen,
                            onDismissRequest = { brgyDropdownOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All 42 Barangays (Municipality-Wide)") },
                                onClick = {
                                    affectedBrgyId = "ALL"
                                    brgyDropdownOpen = false
                                }
                            )
                            HorizontalDivider()
                            CoverageDataStore.barangays.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b.name) },
                                    onClick = {
                                        affectedBrgyId = b.id
                                        brgyDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Target Chain
                    Text("Affected Store Chain:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(null to "All Chains", CoverageBrand.ALFAMART to "Alfamart", CoverageBrand.DALI to "DALI", CoverageBrand.SEVEN_ELEVEN to "7-Eleven").forEach { (brand, label) ->
                            val isSel = affectedBrand == brand
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF0084D6) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSel) Color(0xFF0084D6) else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { affectedBrand = brand }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // ONE-CLICK TEMPORARY CLOSE SWITCH
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Temporary Close All in Selected Area",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverrideClosed) Color(0xFFDC2626) else Color(0xFF0F172A)
                            )
                            Text(
                                text = "Switches pins to black-and-white without overwriting standard operating hours.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = isOverrideClosed,
                            onCheckedChange = { isOverrideClosed = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFDC2626)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (currentBroadcast.isActive) {
                            OutlinedButton(
                                onClick = {
                                    CoverageDataStore.setEmergencyBroadcast(EmergencyBroadcast(isActive = false))
                                    Toast.makeText(context, "Emergency broadcast deactivated.", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFDC2626))
                            ) {
                                Text("Deactivate", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                val newBc = EmergencyBroadcast(
                                    isActive = true,
                                    title = titleInput,
                                    message = messageInput,
                                    affectedBarangayIds = if (affectedBrgyId == "ALL") emptyList() else listOf(affectedBrgyId),
                                    affectedBrands = if (affectedBrand == null) emptyList() else listOf(affectedBrand!!),
                                    isTemporaryClosedOverride = isOverrideClosed
                                )
                                CoverageDataStore.setEmergencyBroadcast(newBc)
                                Toast.makeText(context, "Broadcast published! Override active.", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOverrideClosed) Color(0xFFDC2626) else Color(0xFF0084D6)
                            )
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentBroadcast.isActive) "Update Broadcast" else "Publish Broadcast",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
