package com.sannorem.app.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.model.LatLng
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import com.sannorem.app.ui.components.brandColor
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/**
 * OpenStreetMap composable that handles:
 *  - Displaying branch markers (filtered by [CoverageMapUiState.filteredBranches])
 *  - Drawing the active service-radius circle on marker tap
 *  - Customer-location pin
 *
 * Feature 6: when a branch marker is tapped the caller sets
 * [CoverageMapUiState.isBranchSheetVisible] = true with the [CoverageMapUiState.selectedBranch],
 * and this composable renders the Material 3 ModalBottomSheet with branch name + address.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsmCoverageMapView(
    uiState: CoverageMapUiState,
    onMarkerClick: (RetailBranch) -> Unit,
    onMapClick: (LatLng) -> Unit,
    onClearActiveRadius: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissBranchSheet: () -> Unit = {},
) {
    val context = LocalContext.current

    val mapView = remember {
        Configuration.getInstance().load(
            context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = context.packageName

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            controller.setZoom(15.0)
            controller.setCenter(
                GeoPoint(uiState.initialCenter.latitude, uiState.initialCenter.longitude)
            )
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    // --- Smooth camera re-centering when initialCenter changes (Feature 1 zoom-to-barangay) ---
    LaunchedEffect(uiState.initialCenter) {
        try {
            mapView.controller.animateTo(
                GeoPoint(uiState.initialCenter.latitude, uiState.initialCenter.longitude),
                15.0,
                800L
            )
        } catch (_: Throwable) {
            try {
                mapView.controller.setCenter(
                    GeoPoint(uiState.initialCenter.latitude, uiState.initialCenter.longitude)
                )
            } catch (_: Throwable) {}
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            // 1. Map tap → customer coverage lookup (also closes branch sheet implicitly)
            val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    onMapClick(LatLng(p.latitude, p.longitude))
                    return true
                }
                override fun longPressHelper(p: GeoPoint): Boolean = false
            })
            map.overlays.add(eventsOverlay)

            // 2. Active service-radius circle (translucent, at most one)
            uiState.activeRadiusBranch?.let { branch ->
                val circle = Polygon(map).apply {
                    val center = GeoPoint(branch.latitude, branch.longitude)
                    points = Polygon.pointsAsCircle(center, branch.serviceRadiusKm * 1000.0)
                    val baseColor = branch.brand.brandColor.toArgb()
                    val fillColorInt = (0x26 shl 24) or (baseColor and 0x00FFFFFF)
                    fillPaint.color = fillColorInt
                    outlinePaint.color = baseColor
                    outlinePaint.strokeWidth = 4f
                    setOnClickListener { _, _, _ -> onClearActiveRadius(); true }
                }
                map.overlays.add(circle)
            }

            // Note: Barangay center is NOT pinned per user request ("dont pin the barangay center")

            // 3. Branch markers (proximity-filtered – only nearby branches are in filteredBranches)
            uiState.filteredBranches.forEach { branch ->
                val marker = Marker(map).apply {
                    position = GeoPoint(branch.latitude, branch.longitude)
                    title = branch.branchName
                    // address shown in info window snippet
                    snippet = branch.address.ifBlank { branch.brand.name }
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = getMarkerIcon(
                        context = context,
                        color = branch.brand.brandColor.toArgb(),
                        symbol = when (branch.brand) {
                            Brand.DALI -> "D"
                            Brand.SEVEN_ELEVEN -> "7"
                            Brand.ALFAMART -> "A"
                        }
                    )
                    setOnMarkerClickListener { _, _ ->
                        // Feature 6: triggers branch bottom-sheet
                        onMarkerClick(branch)
                        true  // suppress default info window; sheet shows details
                    }
                }
                map.overlays.add(marker)
            }

            // 4. Customer location pin
            uiState.customerLocation?.let { loc ->
                val customerMarker = Marker(map).apply {
                    position = GeoPoint(loc.latitude, loc.longitude)
                    title = "Customer Location"
                    snippet = "${uiState.customerCoverageResults.size} stores covering this spot"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = getMarkerIcon(context, android.graphics.Color.parseColor("#7C4DFF"), "★")
                }
                map.overlays.add(customerMarker)
            }

            map.invalidate()
        }
    )

    // ─── Feature 6: Branch Details BottomSheet ───────────────────────────────
    // Hidden by default; slides up when a highlighted marker is tapped.
    if (uiState.isBranchSheetVisible && uiState.selectedBranch != null) {
        val branch = uiState.selectedBranch
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismissBranchSheet,
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp, top = 4.dp)
            ) {
                // Brand colour pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(branch.brand.brandColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (branch.brand) {
                            Brand.DALI -> "DALI Everyday Grocery"
                            Brand.SEVEN_ELEVEN -> "7-Eleven"
                            Brand.ALFAMART -> "Alfamart"
                        }.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Branch Name (Feature 6 – required detail)
                Text(
                    text = branch.branchName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Address (Feature 6 – required detail)
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

                // Proximity distance from selected barangay
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

                // Service radius info
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
                    onClick = onDismissBranchSheet,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = branch.brand.brandColor, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Shared marker icon renderer ───────────────────────────────────────────────
private fun getMarkerIcon(context: Context, color: Int, symbol: String): Drawable {
    val density = context.resources.displayMetrics.density
    val width = (36 * density).toInt()
    val height = (46 * density).toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // shadow
    paint.color = 0x40000000
    canvas.drawCircle(width / 2f, height - (4 * density), 6 * density, paint)
    // pin head
    paint.color = color
    val headRadius = (width / 2f) - (2 * density)
    val headCenterY = headRadius + (2 * density)
    canvas.drawCircle(width / 2f, headCenterY, headRadius, paint)
    // pin point triangle
    val path = android.graphics.Path().apply {
        moveTo(width / 2f - (6 * density), headCenterY + (headRadius * 0.5f))
        lineTo(width / 2f, height - (4 * density))
        lineTo(width / 2f + (6 * density), headCenterY + (headRadius * 0.5f))
        close()
    }
    canvas.drawPath(path, paint)
    // white ring
    paint.color = android.graphics.Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    canvas.drawCircle(width / 2f, headCenterY, headRadius - (1.5f * density), paint)
    // symbol text
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.WHITE
    paint.textSize = 14 * density
    paint.textAlign = Paint.Align.CENTER
    paint.isFakeBoldText = true
    val textBounds = android.graphics.Rect()
    paint.getTextBounds(symbol, 0, symbol.length, textBounds)
    canvas.drawText(symbol, width / 2f, headCenterY + (textBounds.height() / 2f) - density, paint)

    return BitmapDrawable(context.resources, bitmap)
}
