package com.sannorem.app.ui.map

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sannorem.app.R
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.ui.components.brandColor
import com.sannorem.app.ui.components.defaultRadiusKm
import com.sannorem.app.ui.components.displayName
import com.sannorem.app.ui.components.markerHue
import com.sannorem.app.ui.components.shortName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverageMapScreen(
    viewModel: CoverageMapViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.initialCenter, 15f)
    }

    var useOpenStreetMap by rememberSaveable { mutableStateOf(true) }

    // Feature 1: Smoothly center & zoom to Level 15 when barangay selection changes (Google Maps mode)
    // (OpenStreetMap safely handles its own smooth camera animation in OsmCoverageMapView)
    if (!useOpenStreetMap) {
        LaunchedEffect(uiState.initialCenter) {
            try {
                MapsInitializer.initialize(context)
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(uiState.initialCenter, 15f),
                    800
                )
            } catch (_: Throwable) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(uiState.initialCenter, 15f)
            }
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLegendDialog by rememberSaveable { mutableStateOf(false) }
    var expandedBarangayDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.selectedBarangay?.name ?: uiState.locationTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = if (useOpenStreetMap) "OpenStreetMap (Live Street Tiles)" else stringResource(R.string.app_tagline),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { useOpenStreetMap = !useOpenStreetMap }) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = if (useOpenStreetMap) "Switch to Google Maps" else "Switch to OpenStreetMap"
                        )
                    }
                    IconButton(onClick = { showLegendDialog = !showLegendDialog }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.map_legend_title)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (useOpenStreetMap) {
                // Free OpenStreetMap (Live tiles, 1.5 km proximity filter, smooth zoom to 15, Details BottomSheet)
                OsmCoverageMapView(
                    uiState = uiState,
                    onMarkerClick = { branch -> viewModel.onMarkerClicked(branch) },
                    onMapClick = { latLng -> viewModel.onMapClick(latLng) },
                    onClearActiveRadius = { viewModel.clearActiveRadius() },
                    modifier = Modifier.fillMaxSize(),
                    onDismissBranchSheet = { viewModel.dismissBranchSheet() }
                )
            } else {
                // Google Map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        mapToolbarEnabled = true
                    ),
                    onMapClick = { latLng ->
                        viewModel.onMapClick(latLng)
                    }
                ) {
                    // Note: Barangay center is NOT pinned per user request ("dont pin the barangay center")

                    // Feature 2: Only show nearby branches (<= 1.5 km). Far branches are completely excluded.
                    uiState.filteredBranches.forEach { branch ->
                        val markerState = remember(branch.id) {
                            MarkerState(position = LatLng(branch.latitude, branch.longitude))
                        }
                        Marker(
                            state = markerState,
                            title = branch.branchName,
                            snippet = "${branch.brand.displayName} • ${branch.address.ifBlank { "Nasugbu" }}",
                            icon = BitmapDescriptorFactory.defaultMarker(branch.brand.markerHue),
                            contentDescription = stringResource(
                                R.string.cd_brand_marker,
                                branch.brand.shortName,
                                branch.branchName
                            ),
                            onClick = {
                                viewModel.onMarkerClicked(branch)
                                false
                            },
                            onInfoWindowClose = {
                                viewModel.clearActiveRadius()
                            }
                        )
                    }

                    // Customer Pin Marker
                    uiState.customerLocation?.let { customerLoc ->
                        val customerMarkerState = remember(customerLoc) {
                            MarkerState(position = customerLoc)
                        }
                        Marker(
                            state = customerMarkerState,
                            title = stringResource(R.string.customer_pin_label),
                            snippet = stringResource(
                                R.string.coverage_sheet_covered_by,
                                uiState.customerCoverageResults.size
                            ),
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                            contentDescription = stringResource(R.string.cd_customer_marker)
                        )
                    }

                    // Active Radius Circle (At most one exists at a time)
                    if (uiState.activeRadiusBranch != null) {
                        val branch = uiState.activeRadiusBranch!!
                        Circle(
                            center = LatLng(branch.latitude, branch.longitude),
                            radius = branch.serviceRadiusKm * 1000.0,
                            fillColor = branch.brand.brandColor.copy(alpha = 0.15f),
                            strokeColor = branch.brand.brandColor,
                            strokeWidth = 3f,
                            clickable = true,
                            onClick = {
                                viewModel.clearActiveRadius()
                            }
                        )
                    }
                }
            }

            // Top Floating Controls: Barangay Dropdown/Spinner & Brand Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Missing API Key Banner (only shown if Google Maps is active and key is missing)
                if (!useOpenStreetMap && uiState.isMapsApiKeyMissing) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.missing_api_key_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = stringResource(R.string.missing_api_key_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // ── Feature 1 & 2: Barangay Selection Dropdown / Spinner Card ──
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SELECT BARANGAY (NASUGBU)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedBarangayDropdown,
                            onExpandedChange = { expandedBarangayDropdown = !expandedBarangayDropdown }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedBarangay?.name ?: uiState.locationTitle,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBarangayDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = expandedBarangayDropdown,
                                onDismissRequest = { expandedBarangayDropdown = false },
                                modifier = Modifier.heightIn(max = 280.dp)
                            ) {
                                uiState.barangays.forEach { barangay ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = barangay.name,
                                                fontWeight = if (barangay == uiState.selectedBarangay) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            expandedBarangayDropdown = false
                                            viewModel.selectBarangay(barangay)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Proximity status badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Proximity: 1.5 km • ${uiState.filteredBranches.size} nearby stores",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Approximate Center Notice Banner
                if (uiState.isApproximateCenter) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(
                                    R.string.approximate_center_notice,
                                    uiState.locationTitle
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Brand Filter Chips
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Brand.entries.forEach { brand ->
                            val isSelected = uiState.activeBrandFilters.contains(brand)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleBrandFilter(brand) },
                                label = {
                                    Text(
                                        text = brand.shortName,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(brand.brandColor)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brand.brandColor.copy(alpha = 0.18f),
                                    selectedLabelColor = brand.brandColor
                                )
                            )
                        }
                    }
                }
            }

            // Floating Active Circle Indicator (Allows explicit clearing)
            AnimatedVisibility(
                visible = uiState.activeRadiusBranch != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                val branch = uiState.activeRadiusBranch
                if (branch != null) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(branch.brand.brandColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${branch.branchName} (${branch.serviceRadiusKm} km radius)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.clearActiveRadius() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cd_clear_circle),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Legend Dialog / Bottom Sheet
    if (showLegendDialog) {
        ModalBottomSheet(
            onDismissRequest = { showLegendDialog = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.map_legend_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.tap_map_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Brand.entries.forEach { brand ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(brand.brandColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = brand.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(
                                    R.string.service_radius_info,
                                    brand.defaultRadiusKm
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // ── Feature 6: Details Panel for Google Maps mode (OSM renders inside OsmCoverageMapView) ──
    if (!useOpenStreetMap && uiState.isBranchSheetVisible && uiState.selectedBranch != null) {
        val branch = uiState.selectedBranch!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissBranchSheet() },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp, top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(branch.brand.brandColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = branch.brand.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Feature 6: Branch Name
                Text(
                    text = branch.branchName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Feature 6: Address
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = branch.brand.brandColor,
                        modifier = Modifier.size(18.dp).padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = branch.address.ifBlank { "Nasugbu, Batangas" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Distance from Barangay Center
                uiState.branchDistancesKm[branch.id]?.let { distKm ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "%.2f km from %s".format(
                            distKm,
                            uiState.selectedBarangay?.name ?: uiState.locationTitle
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(branch.brand.brandColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Service radius: ${branch.serviceRadiusKm} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { viewModel.dismissBranchSheet() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = branch.brand.brandColor, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Customer Location Coverage Bottom Sheet
    if (uiState.isBottomSheetVisible && uiState.customerLocation != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissBottomSheet() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.coverage_sheet_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                val coverageCount = uiState.customerCoverageResults.size
                Text(
                    text = if (coverageCount > 0) {
                        stringResource(R.string.coverage_sheet_covered_by, coverageCount)
                    } else {
                        stringResource(R.string.coverage_sheet_no_coverage)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (coverageCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                if (coverageCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.coverage_sheet_no_coverage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.customerCoverageResults) { result ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(result.branch.brand.brandColor)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.branch.branchName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = result.branch.brand.displayName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        val distMeters = (result.distanceKm * 1000.0).toInt()
                                        val distanceText = if (distMeters < 1000) {
                                            stringResource(R.string.distance_meters, distMeters)
                                        } else {
                                            stringResource(R.string.distance_km, result.distanceKm)
                                        }

                                        Text(
                                            text = distanceText,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.service_radius_info,
                                                result.branch.serviceRadiusKm
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

fun isGoogleMapsApiKeyMissing(context: Context): Boolean {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val key = appInfo.metaData?.getString("com.google.android.geo.API_KEY")
        key.isNullOrBlank() || key == "YOUR_GOOGLE_MAPS_API_KEY"
    } catch (_: Exception) {
        true
    }
}
