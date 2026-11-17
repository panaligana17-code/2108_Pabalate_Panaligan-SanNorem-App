package com.sannorem.app.ui.map

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sannorem.app.R
import com.sannorem.app.data.AssetBarangayRepository
import com.sannorem.app.data.AssetBranchRepository
import com.sannorem.app.domain.geo.GeoUtils
import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import com.sannorem.app.ui.components.displayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * High-Quality GIS Map Activity demonstrating Google Maps SDK:
 * 1. Clean JSON Map Styling for high-resolution visual quality.
 * 2. Barangay Selection & Animated Polygon Overlay (sleek translucent fill & sharp stroke).
 * 3. Smooth Camera Pan and Zoom to exact geographic boundaries using [CameraUpdateFactory.newLatLngBounds].
 * 4. Background-Thread Spatial Point-in-Polygon Filtering with AABB pre-filtering.
 * 5. Animated Marker Entrance (fade-in & pop-in) and BottomSheet Details Panel.
 */
class GoogleMapsProximityActivity : AppCompatActivity(), OnMapReadyCallback {

    data class BarangayPolygon(
        val id: String,
        val name: String,
        val points: List<LatLng>,
        val bounds: LatLngBounds
    )

    private var googleMap: GoogleMap? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    // UI Views
    private lateinit var spinnerBarangay: Spinner
    private lateinit var tvFilterStatus: TextView
    private lateinit var bottomSheetLayout: View
    private lateinit var tvBranchBrand: TextView
    private lateinit var tvBranchName: TextView
    private lateinit var tvBranchAddress: TextView
    private lateinit var tvBranchDistance: TextView
    private lateinit var tvBranchCoordinates: TextView
    private lateinit var btnCloseDetails: Button

    // Datasets
    private var barangays: List<Barangay> = emptyList()
    private var allBranches: List<RetailBranch> = emptyList()
    private val polygonMap = mutableMapOf<String, BarangayPolygon>()
    private var selectedBarangay: Barangay? = null

    // Overlays & Animations
    private var activePolygon: Polygon? = null
    private var polygonFadeAnimator: ValueAnimator? = null
    private val activeMarkers = mutableListOf<Marker>()

    companion object {
        private val NASUGBU_CENTER = LatLng(14.0734, 120.6315) // Nasugbu Municipal Center
        private val BRAND_BLUE = Color.parseColor("#0084D6")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_maps_proximity)

        initViews()
        loadData()
        loadGeoJsonBoundaries()
        setupSpinner()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun initViews() {
        spinnerBarangay = findViewById(R.id.spinner_barangay)
        tvFilterStatus = findViewById(R.id.tv_filter_status)
        bottomSheetLayout = findViewById(R.id.bottom_sheet_details)
        tvBranchBrand = findViewById(R.id.tv_branch_brand)
        tvBranchName = findViewById(R.id.tv_branch_name)
        tvBranchAddress = findViewById(R.id.tv_branch_address)
        tvBranchDistance = findViewById(R.id.tv_branch_distance)
        tvBranchCoordinates = findViewById(R.id.tv_branch_coordinates)
        btnCloseDetails = findViewById(R.id.btn_close_details)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = true

        btnCloseDetails.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun loadData() {
        val barangayRepo = AssetBarangayRepository(applicationContext)
        val branchRepo = AssetBranchRepository(applicationContext)

        barangays = barangayRepo.getBarangays()
        allBranches = branchRepo.getBranches()
    }

    private fun loadGeoJsonBoundaries() {
        try {
            val jsonString = assets.open("nasugbu_barangays_geojson.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)
            val features = root.optJSONArray("features") ?: return

            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val properties = feature.optJSONObject("properties")
                val id = properties?.optString("id") ?: continue
                val name = properties.optString("name", id)

                val geometry = feature.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates").getJSONArray(0)

                val points = mutableListOf<LatLng>()
                val boundsBuilder = LatLngBounds.Builder()

                for (j in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(j)
                    val lng = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    val latLng = LatLng(lat, lng)
                    points.add(latLng)
                    boundsBuilder.include(latLng)
                }

                if (points.isNotEmpty()) {
                    polygonMap[id] = BarangayPolygon(id, name, points, boundsBuilder.build())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSpinner() {
        val spinnerItems = mutableListOf("All 42 Barangays (Overview)").apply {
            addAll(barangays.map { it.name })
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.item_barangay_spinner,
            spinnerItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBarangay.adapter = adapter

        spinnerBarangay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedBarangay = null
                    showAllBranchesOverview()
                } else {
                    val barangayIndex = position - 1
                    if (barangayIndex in barangays.indices) {
                        val barangay = barangays[barangayIndex]
                        selectedBarangay = barangay
                        onBarangaySelected(barangay)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Apply clean JSON map style
        try {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_clean))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = false

        map.setOnMarkerClickListener { marker ->
            handleMarkerClick(marker)
        }

        map.setOnMapClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        if (selectedBarangay != null) {
            onBarangaySelected(selectedBarangay!!)
        } else {
            showAllBranchesOverview()
        }
    }

    /**
     * Overview mode: displays all branches across Nasugbu with a municipal-wide camera view.
     */
    private fun showAllBranchesOverview() {
        val map = googleMap ?: return
        clearOverlays()

        val municipalBoundsBuilder = LatLngBounds.Builder()
        allBranches.forEach { municipalBoundsBuilder.include(LatLng(it.latitude, it.longitude)) }
        municipalBoundsBuilder.include(NASUGBU_CENTER)

        val bounds = municipalBoundsBuilder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120), 800, null)

        tvFilterStatus.text = "Showing all ${allBranches.size} branches in Nasugbu"

        allBranches.forEachIndexed { index, branch ->
            addAnimatedBranchMarker(branch, distanceKm = null, delayMs = (index * 25L).coerceAtMost(300L))
        }
    }

    /**
     * Feature 1 & 2: Barangay Selection, Animated Polygon Boundary,
     * Smooth Bounds Zoom, and Background Spatial Query.
     */
    private fun onBarangaySelected(barangay: Barangay) {
        val map = googleMap ?: return
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val polygonData = polygonMap[barangay.id]

        if (polygonData != null) {
            // 1. Draw sleek polygon overlay with smooth fade-in animation
            drawAnimatedPolygon(polygonData.points)

            // 2. Smoothly pan & zoom to the exact polygon bounding box
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(polygonData.bounds, 120)
            map.animateCamera(cameraUpdate, 800, null)

            // 3. Optimized Spatial Query off the main thread
            lifecycleScope.launch {
                val matchingBranches = withContext(Dispatchers.Default) {
                    filterBranchesInsidePolygon(allBranches, polygonData.points, polygonData.bounds)
                }

                clearMarkers()

                tvFilterStatus.text = if (matchingBranches.isNotEmpty()) {
                    "GIS Boundary: ${matchingBranches.size} branch(es) inside ${barangay.name}"
                } else {
                    "No retail branches inside ${barangay.name}"
                }

                val centerLat = (polygonData.bounds.northeast.latitude + polygonData.bounds.southwest.latitude) / 2
                val centerLng = (polygonData.bounds.northeast.longitude + polygonData.bounds.southwest.longitude) / 2

                matchingBranches.forEachIndexed { index, branch ->
                    val distKm = GeoUtils.haversineKm(centerLat, centerLng, branch.latitude, branch.longitude)
                    addAnimatedBranchMarker(branch, distKm, delayMs = (index * 40L).coerceAtMost(300L))
                }
            }
        } else {
            // Fallback if no polygon defined: center on coordinate
            clearOverlays()
            val lat = barangay.latitude ?: NASUGBU_CENTER.latitude
            val lng = barangay.longitude ?: NASUGBU_CENTER.longitude
            val target = LatLng(lat, lng)

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 15f), 800, null)

            val nearby = allBranches.filter {
                GeoUtils.haversineKm(lat, lng, it.latitude, it.longitude) <= 1.5
            }
            tvFilterStatus.text = "Proximity: ${nearby.size} branch(es) within 1.5 km"

            nearby.forEachIndexed { index, branch ->
                val distKm = GeoUtils.haversineKm(lat, lng, branch.latitude, branch.longitude)
                addAnimatedBranchMarker(branch, distKm, delayMs = (index * 40L).coerceAtMost(300L))
            }
        }
    }

    /**
     * Draws the polygon boundary with smooth fade-in animation.
     * Fill: Sleek translucent blue (~15% alpha).
     * Stroke: Sharp brand blue border (5f width).
     */
    private fun drawAnimatedPolygon(points: List<LatLng>) {
        val map = googleMap ?: return
        polygonFadeAnimator?.cancel()
        activePolygon?.remove()

        val polygon = map.addPolygon(
            PolygonOptions()
                .addAll(points)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(Color.TRANSPARENT)
                .strokeWidth(5f)
                .geodesic(true)
        )
        activePolygon = polygon

        polygonFadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500L
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                val progress = anim.animatedValue as Float
                val fillAlpha = (progress * 0.16f * 255).toInt()
                val strokeAlpha = (progress * 255).toInt()

                polygon.fillColor = ColorUtils.setAlphaComponent(BRAND_BLUE, fillAlpha)
                polygon.strokeColor = ColorUtils.setAlphaComponent(BRAND_BLUE, strokeAlpha)
            }
            start()
        }
    }

    /**
     * High-performance spatial Point-in-Polygon query.
     * Includes Fast AABB (Axis-Aligned Bounding Box) pre-filter before Ray-Casting.
     */
    private fun filterBranchesInsidePolygon(
        branches: List<RetailBranch>,
        polygonPoints: List<LatLng>,
        bounds: LatLngBounds
    ): List<RetailBranch> {
        val insideBranches = mutableListOf<RetailBranch>()
        for (branch in branches) {
            val pt = LatLng(branch.latitude, branch.longitude)
            // 1. O(1) Fast AABB Rejection
            if (!bounds.contains(pt)) continue

            // 2. Exact Ray-Casting test
            if (isPointInPolygon(pt, polygonPoints)) {
                insideBranches.add(branch)
            }
        }
        return insideBranches
    }

    private fun isPointInPolygon(point: LatLng, polygon: List<LatLng>): Boolean {
        if (polygon.size < 3) return false
        var inside = false
        val n = polygon.size
        var j = n - 1

        val px = point.longitude
        val py = point.latitude

        for (i in 0 until n) {
            val xi = polygon[i].longitude
            val yi = polygon[i].latitude
            val xj = polygon[j].longitude
            val yj = polygon[j].latitude

            val intersect = ((yi > py) != (yj > py)) &&
                    (px < (xj - xi) * (py - yi) / (yj - yi) + xi)

            if (intersect) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    /**
     * Adds a branch marker with a smooth fade-in and scale pop-in animation.
     */
    private fun addAnimatedBranchMarker(branch: RetailBranch, distanceKm: Double?, delayMs: Long = 0L) {
        val map = googleMap ?: return

        val markerColor = when (branch.brand) {
            Brand.DALI -> BitmapDescriptorFactory.HUE_AZURE
            Brand.SEVEN_ELEVEN -> BitmapDescriptorFactory.HUE_ORANGE
            Brand.ALFAMART -> BitmapDescriptorFactory.HUE_RED
        }

        val snippetText = if (distanceKm != null) {
            "${branch.brand.displayName} • %.2f km".format(distanceKm)
        } else {
            branch.brand.displayName
        }

        val marker = map.addMarker(
            MarkerOptions()
                .position(LatLng(branch.latitude, branch.longitude))
                .title(branch.branchName)
                .snippet(snippetText)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                .alpha(0f)
        ) ?: return

        marker.tag = branch to distanceKm
        activeMarkers.add(marker)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350L
            startDelay = delayMs
            interpolator = OvershootInterpolator(1.1f)
            addUpdateListener { anim ->
                marker.alpha = anim.animatedValue as Float
            }
            start()
        }
    }

    private fun clearMarkers() {
        activeMarkers.forEach { it.remove() }
        activeMarkers.clear()
    }

    private fun clearOverlays() {
        polygonFadeAnimator?.cancel()
        activePolygon?.remove()
        activePolygon = null
        clearMarkers()
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleMarkerClick(marker: Marker): Boolean {
        val tagData = marker.tag as? Pair<RetailBranch, Double?>
        if (tagData != null) {
            val (branch, distanceKm) = tagData
            val barangayName = selectedBarangay?.name ?: "Barangay"

            tvBranchBrand.text = branch.brand.displayName.uppercase()
            when (branch.brand) {
                Brand.DALI -> tvBranchBrand.setBackgroundColor("#2563EB".toColorInt())
                Brand.SEVEN_ELEVEN -> tvBranchBrand.setBackgroundColor("#EA580C".toColorInt())
                Brand.ALFAMART -> tvBranchBrand.setBackgroundColor("#DC2626".toColorInt())
            }

            tvBranchName.text = branch.branchName
            tvBranchAddress.text = branch.address.ifBlank { "Nasugbu, Batangas" }

            tvBranchDistance.text = if (distanceKm != null) {
                getString(R.string.branch_distance_format, distanceKm, barangayName)
            } else {
                "Verified Retail Branch"
            }

            tvBranchCoordinates.text = getString(
                R.string.branch_coordinates_format,
                branch.latitude,
                branch.longitude
            )

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            return true
        }
        return false
    }

    override fun onDestroy() {
        polygonFadeAnimator?.cancel()
        super.onDestroy()
    }
}
