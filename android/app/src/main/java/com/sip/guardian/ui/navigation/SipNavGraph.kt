package com.sip.guardian.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.sip.guardian.ui.screen.auth.LoginScreen
import com.sip.guardian.ui.screen.dashboard.DashboardScreen
import com.sip.guardian.ui.screen.incidents.IncidentDetailScreen
import com.sip.guardian.ui.screen.incidents.IncidentFeedScreen

@Composable
fun SipNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(SipNavDestination.Login.route) {
            LoginScreen(onLoggedIn = {
                navController.navigate(SipNavDestination.Dashboard.route) {
                    popUpTo(SipNavDestination.Login.route) { inclusive = true }
                }
            })
        }
        composable(SipNavDestination.Dashboard.route) { DashboardScreen() }
        composable(SipNavDestination.IncidentFeed.route) {
            IncidentFeedScreen(onIncidentClick = { id ->
                navController.navigate(SipNavDestination.IncidentDetail.createRoute(id))
            })
        }
        composable(
            route = SipNavDestination.IncidentDetail.route,
            arguments = listOf(navArgument(SipNavDestination.IncidentDetail.ARG_ID) {
                type = NavType.StringType
            }),
        ) {
            IncidentDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
