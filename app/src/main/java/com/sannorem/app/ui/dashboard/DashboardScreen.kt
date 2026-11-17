package com.sannorem.app.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sannorem.app.R
import com.sannorem.app.domain.geo.GeoUtils
import com.sannorem.app.domain.model.BarangayGroup
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import com.sannorem.app.ui.components.RetailBranchCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToMap: (lat: Double, lng: Double, name: String, isApproximate: Boolean, isCustomerTest: Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val customerPinLabel = stringResource(R.string.customer_pin_label)
    val locationFallbackMsg = stringResource(R.string.location_off_or_denied_fallback)
    val locationOutOfBoundsTemplate = stringResource(R.string.location_out_of_bounds_fallback)

    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    fun proceedWithLocation(lat: Double?, lng: Double?) {
        when (val result = viewModel.evaluateCustomerLocation(lat, lng)) {
            is LocationTestResult.Success -> {
                onNavigateToMap(
                    result.latitude,
                    result.longitude,
                    customerPinLabel,
                    false,
                    true
                )
            }
            is LocationTestResult.OutOfBounds -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        String.format(locationOutOfBoundsTemplate, result.distanceKm)
                    )
                }
                onNavigateToMap(
                    result.latitude,
                    result.longitude,
                    "Nasugbu Municipal Center",
                    true,
                    true
                )
            }
            is LocationTestResult.Failed -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(locationFallbackMsg)
                }
                onNavigateToMap(
                    result.fallbackLat,
                    result.fallbackLng,
                    "Nasugbu Municipal Center",
                    true,
                    true
                )
            }
        }
    }

    fun requestDeviceLocation() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        proceedWithLocation(loc.latitude, loc.longitude)
                    } else {
                        fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                            proceedWithLocation(lastLoc?.latitude, lastLoc?.longitude)
                        }.addOnFailureListener {
                            proceedWithLocation(null, null)
                        }
                    }
                }
                .addOnFailureListener {
                    proceedWithLocation(null, null)
                }
        } catch (_: SecurityException) {
            proceedWithLocation(null, null)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            requestDeviceLocation()
        } else {
            proceedWithLocation(null, null)
        }
    }

    fun onTestCustomerLocationClicked() {
        val fineCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineCheck || coarseCheck) {
            requestDeviceLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8FAFC) // bg-slate-50
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TOOLBAR / CONTROL PANEL CARD (exact match to HTML & reference image)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title & Subtitle
                        Column {
                            Text(
                                text = "Branch Inventory & Coverage",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Browse Nasugbu neighborhood stores and check customer proximity to each service radius.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Search Input
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = {
                                Text(
                                    text = "Search branch, ID, barangay...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFFCBD5E1),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        // Barangay Filter Dropdown
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = uiState.selectedBarangay?.let {
                                    "${it.name} (${uiState.branches.count { b -> b.barangayId.equals(it.id, ignoreCase = true) || b.address.contains(it.name, ignoreCase = true) }})"
                                } ?: "All 42 barangays",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFFCBD5E1),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                // "All 42 barangays" Option
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "All 42 barangays (${uiState.branches.size})",
                                            fontWeight = if (uiState.selectedBarangay == null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.selectBarangay(null)
                                        dropdownExpanded = false
                                    }
                                )

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                // Poblacion Group Header
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "POBLACION",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0084D6)
                                        )
                                    },
                                    onClick = {},
                                    enabled = false,
                                    colors = MenuDefaults.itemColors(disabledTextColor = Color(0xFF0084D6))
                                )

                                uiState.barangays
                                    .filter { it.group == BarangayGroup.POBLACION }
                                    .forEach { brgy ->
                                        val count = uiState.branches.count { b ->
                                            b.barangayId.equals(brgy.id, ignoreCase = true) ||
                                            b.address.contains(brgy.name, ignoreCase = true)
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = "${brgy.name} ($count)")
                                                    if (uiState.selectedBarangay?.id == brgy.id) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = Color(0xFF0084D6),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                viewModel.selectBarangay(brgy)
                                                dropdownExpanded = false
                                            }
                                        )
                                    }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                // Rural & Coastal Group Header
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "RURAL & COASTAL",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0084D6)
                                        )
                                    },
                                    onClick = {},
                                    enabled = false,
                                    colors = MenuDefaults.itemColors(disabledTextColor = Color(0xFF0084D6))
                                )

                                uiState.barangays
                                    .filter { it.group == BarangayGroup.RURAL_COASTAL }
                                    .forEach { brgy ->
                                        val count = uiState.branches.count { b ->
                                            b.barangayId.equals(brgy.id, ignoreCase = true) ||
                                            b.address.contains(brgy.name, ignoreCase = true)
                                        }
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = "${brgy.name} ($count)")
                                                    if (uiState.selectedBarangay?.id == brgy.id) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = Color(0xFF0084D6),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                viewModel.selectBarangay(brgy)
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                            }
                        }

                        // Brand Tabs Pill Bar (exact match to HTML: "All Brands", "7-Eleven", "Alfamart", "Dali")
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9), // bg-slate-100
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BrandPill(
                                    label = "All Brands",
                                    isSelected = uiState.selectedBrandFilter == null,
                                    onClick = { viewModel.selectBrandFilter(null) }
                                )
                                BrandPill(
                                    label = "7-Eleven",
                                    isSelected = uiState.selectedBrandFilter == Brand.SEVEN_ELEVEN,
                                    onClick = { viewModel.selectBrandFilter(Brand.SEVEN_ELEVEN) }
                                )
                                BrandPill(
                                    label = "Alfamart",
                                    isSelected = uiState.selectedBrandFilter == Brand.ALFAMART,
                                    onClick = { viewModel.selectBrandFilter(Brand.ALFAMART) }
                                )
                                BrandPill(
                                    label = "Dali",
                                    isSelected = uiState.selectedBrandFilter == Brand.DALI,
                                    onClick = { viewModel.selectBrandFilter(Brand.DALI) }
                                )
                            }
                        }

                        // Action Buttons: "Check My Proximity" & "View Full Map"
                        Button(
                            onClick = { onTestCustomerLocationClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF059669) // bg-emerald-600
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Check My Proximity",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Direct View Map Button
                        OutlinedButton(
                            onClick = {
                                val selected = uiState.selectedBarangay
                                val lat = selected?.latitude ?: GeoUtils.NASUGBU_CENTER_LAT
                                val lng = selected?.longitude ?: GeoUtils.NASUGBU_CENTER_LNG
                                onNavigateToMap(
                                    lat,
                                    lng,
                                    selected?.name ?: "Nasugbu Municipal Center",
                                    false,
                                    false
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open Coverage Map (OpenStreetMap)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Subtitle Info & Showing Count
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Showing ${uiState.filteredBranches.size} of ${uiState.branches.size} branches",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B) // text-slate-500
                    )
                    Text(
                        text = "Official branch network across Nasugbu. Tap any store pin on the map to inspect its coverage radius.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8) // text-slate-400
                    )
                }

                // Branch Cards List / Grid
                if (uiState.filteredBranches.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No mapped branches match this filter yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    uiState.filteredBranches.forEach { branch ->
                        RetailBranchCard(
                            branch = branch,
                            onViewOnMap = { selectedBranch ->
                                onNavigateToMap(
                                    selectedBranch.latitude,
                                    selectedBranch.longitude,
                                    selectedBranch.branchName,
                                    false,
                                    false
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        shadowElevation = if (isSelected) 1.dp else 0.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color(0xFF0F172A) else Color(0xFF64748B)
        )
    }
}
