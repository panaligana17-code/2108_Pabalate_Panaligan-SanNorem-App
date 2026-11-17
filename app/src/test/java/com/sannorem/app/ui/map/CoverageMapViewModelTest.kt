package com.sannorem.app.ui.map

import com.sannorem.app.data.BarangayRepository
import com.sannorem.app.data.BranchRepository
import com.sannorem.app.domain.model.Barangay
import com.sannorem.app.domain.model.BarangayGroup
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch
import com.sannorem.app.domain.usecase.FindCoveringBranchesUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoverageMapViewModelTest {

    private lateinit var fakeBarangayRepo: BarangayRepository
    private lateinit var fakeBranchRepo: BranchRepository
    private lateinit var findCoveringBranchesUseCase: FindCoveringBranchesUseCase

    private val sampleBarangay1 = Barangay(
        id = "barangay_1",
        name = "Barangay 1",
        group = BarangayGroup.POBLACION,
        latitude = 14.0734,
        longitude = 120.6315
    )

    private val sampleBarangayPapaya = Barangay(
        id = "barangay_papaya",
        name = "Papaya",
        group = BarangayGroup.RURAL_COASTAL,
        latitude = 14.2494,
        longitude = 120.6133
    )

    // Branch 1: Near Barangay 1 (approx 0.2 km away)
    private val branchNear = RetailBranch(
        id = "branch_near",
        branchName = "Dali Barangay 1",
        brand = Brand.DALI,
        latitude = 14.0740,
        longitude = 120.6320,
        serviceRadiusKm = 1.0,
        address = "J.P. Laurel St., Barangay 1, Nasugbu"
    )

    // Branch 2: Far from Barangay 1 (in Papaya, ~19 km away)
    private val branchFar = RetailBranch(
        id = "branch_far",
        branchName = "Alfamart Papaya",
        brand = Brand.ALFAMART,
        latitude = 14.2490,
        longitude = 120.6130,
        serviceRadiusKm = 0.5,
        address = "Papaya Main Road, Nasugbu"
    )

    @Before
    fun setup() {
        val barangays = listOf(sampleBarangay1, sampleBarangayPapaya)
        val branches = listOf(branchNear, branchFar)

        fakeBarangayRepo = object : BarangayRepository {
            override fun getBarangays(): List<Barangay> = barangays
            override fun findByNameOrAlias(query: String): Barangay? =
                barangays.firstOrNull { it.name.equals(query, ignoreCase = true) }
        }

        fakeBranchRepo = object : BranchRepository {
            override fun getBranches(): List<RetailBranch> = branches
        }

        findCoveringBranchesUseCase = FindCoveringBranchesUseCase(fakeBranchRepo)
    }

    @Test
    fun `initialization with Barangay 1 filters nearby branches within 1_5 km and excludes far branches`() {
        val viewModel = CoverageMapViewModel(
            barangayRepository = fakeBarangayRepo,
            branchRepository = fakeBranchRepo,
            findCoveringBranchesUseCase = findCoveringBranchesUseCase,
            initialLat = 14.0734,
            initialLng = 120.6315,
            title = "Barangay 1",
            isApproximate = false,
            isCustomerTest = false,
            isMapsApiKeyMissing = false
        )

        val state = viewModel.uiState.value
        assertEquals("Barangay 1", state.selectedBarangay?.name)
        assertEquals(1, state.filteredBranches.size)
        assertEquals("branch_near", state.filteredBranches.first().id)
        assertFalse("Far branch must be completely excluded", state.filteredBranches.any { it.id == "branch_far" })

        // Check distance calculated in branchDistancesKm
        val distNear = state.branchDistancesKm["branch_near"]
        assertNotNull(distNear)
        assertTrue(distNear!! <= 1.5)
    }

    @Test
    fun `selectBarangay updates center coordinates and applies 1_5 km proximity filter`() {
        val viewModel = CoverageMapViewModel(
            barangayRepository = fakeBarangayRepo,
            branchRepository = fakeBranchRepo,
            findCoveringBranchesUseCase = findCoveringBranchesUseCase,
            initialLat = 14.0734,
            initialLng = 120.6315,
            title = "Barangay 1",
            isApproximate = false,
            isCustomerTest = false,
            isMapsApiKeyMissing = false
        )

        // Switch to Papaya
        viewModel.selectBarangay(sampleBarangayPapaya)

        val state = viewModel.uiState.value
        assertEquals("Papaya", state.selectedBarangay?.name)
        assertEquals(14.2494, state.initialCenter.latitude, 0.0001)
        assertEquals(120.6133, state.initialCenter.longitude, 0.0001)

        // Now branchFar (in Papaya) is nearby (<= 1.5 km), and branchNear (in Barangay 1) is far
        assertEquals(1, state.filteredBranches.size)
        assertEquals("branch_far", state.filteredBranches.first().id)
        assertFalse("Barangay 1 branch must now be excluded", state.filteredBranches.any { it.id == "branch_near" })
    }

    @Test
    fun `marker click triggers branch details bottom sheet with branch name and address`() {
        val viewModel = CoverageMapViewModel(
            barangayRepository = fakeBarangayRepo,
            branchRepository = fakeBranchRepo,
            findCoveringBranchesUseCase = findCoveringBranchesUseCase,
            initialLat = 14.0734,
            initialLng = 120.6315,
            title = "Barangay 1",
            isApproximate = false,
            isCustomerTest = false,
            isMapsApiKeyMissing = false
        )

        assertFalse(viewModel.uiState.value.isBranchSheetVisible)

        viewModel.onMarkerClicked(branchNear)

        val stateAfterClick = viewModel.uiState.value
        assertTrue(stateAfterClick.isBranchSheetVisible)
        assertEquals(branchNear, stateAfterClick.selectedBranch)
        assertEquals("Dali Barangay 1", stateAfterClick.selectedBranch?.branchName)
        assertEquals("J.P. Laurel St., Barangay 1, Nasugbu", stateAfterClick.selectedBranch?.address)

        // Dismiss sheet
        viewModel.dismissBranchSheet()
        assertFalse(viewModel.uiState.value.isBranchSheetVisible)
    }
}
