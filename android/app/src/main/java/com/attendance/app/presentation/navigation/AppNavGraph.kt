package com.attendance.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.attendance.app.presentation.screen.MarkAttendanceScreen
import com.attendance.app.presentation.screen.StatisticsScreen
import com.attendance.app.presentation.screen.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val MARK  = "mark/{lessonId}/{groupId}"
    const val STATS = "stats/{groupId}"

    fun mark(lessonId: String, groupId: String) = "mark/$lessonId/$groupId"
    fun stats(groupId: String) = "stats/$groupId"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onSuccess = { lessonId, groupId ->
                navController.navigate(Routes.mark(lessonId, groupId)) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }
        composable(
            route = Routes.MARK,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("groupId")  { type = NavType.StringType }
            )
        ) {
            MarkAttendanceScreen(
                onOpenStats = { groupId -> navController.navigate(Routes.stats(groupId)) }
            )
        }
        composable(
            route = Routes.STATS,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) {
            StatisticsScreen(onBack = { navController.popBackStack() })
        }
    }
}
