package com.sannorem.app.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sannorem.app.data.BarangayRepository
import com.sannorem.app.data.BranchRepository
import com.sannorem.app.domain.usecase.FindCoveringBranchesUseCase
import com.sannorem.app.ui.dashboard.DashboardScreen
import com.sannorem.app.ui.dashboard.DashboardViewModel
import com.sannorem.app.ui.login.LoginScreen
import com.sannorem.app.ui.map.CoverageMapScreen
import com.sannorem.app.ui.map.CoverageMapViewModel
import com.sannorem.app.ui.map.isGoogleMapsApiKeyMissing

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object CoverageMap : Screen("coverage_map/{lat}/{lng}/{name}/{isApproximate}/{isCustomerTest}") {
        fun createRoute(
            lat: Double,
            lng: Double,
            name: String,
            isApproximate: Boolean,
            isCustomerTest: Boolean
        ): String {
            val encodedName = Uri.encode(name)
            return "coverage_map/$lat/$lng/$encodedName/$isApproximate/$isCustomerTest"
        }
    }
}

@Composable
fun SanNoremNavHost(
    barangayRepository: BarangayRepository,
    branchRepository: BranchRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val findCoveringBranchesUseCase = FindCoveringBranchesUseCase(branchRepository)

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Login.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onContinueAsGuest = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Dashboard.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            }
        ) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.provideFactory(
                    barangayRepository = barangayRepository,
                    branchRepository = branchRepository
                )
            )

            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToMap = { lat, lng, name, isApproximate, isCustomerTest ->
                    navController.navigate(
                        Screen.CoverageMap.createRoute(
                            lat = lat,
                            lng = lng,
                            name = name,
                            isApproximate = isApproximate,
                            isCustomerTest = isCustomerTest
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.CoverageMap.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("isApproximate") { type = NavType.BoolType },
                navArgument("isCustomerTest") { type = NavType.BoolType }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) { backStackEntry ->
            val latStr = backStackEntry.arguments?.getString("lat") ?: "14.0734"
            val lngStr = backStackEntry.arguments?.getString("lng") ?: "120.6315"
            val rawName = backStackEntry.arguments?.getString("name") ?: "Nasugbu"
            val name = Uri.decode(rawName)
            val isApproximate = backStackEntry.arguments?.getBoolean("isApproximate") ?: false
            val isCustomerTest = backStackEntry.arguments?.getBoolean("isCustomerTest") ?: false

            val lat = latStr.toDoubleOrNull() ?: 14.0734
            val lng = lngStr.toDoubleOrNull() ?: 120.6315
            val isApiKeyMissing = isGoogleMapsApiKeyMissing(context)

            val coverageMapViewModel: CoverageMapViewModel = viewModel(
                factory = CoverageMapViewModel.provideFactory(
                    barangayRepository = barangayRepository,
                    branchRepository = branchRepository,
                    findCoveringBranchesUseCase = findCoveringBranchesUseCase,
                    initialLat = lat,
                    initialLng = lng,
                    title = name,
                    isApproximate = isApproximate,
                    isCustomerTest = isCustomerTest,
                    isMapsApiKeyMissing = isApiKeyMissing
                )
            )

            CoverageMapScreen(
                viewModel = coverageMapViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
