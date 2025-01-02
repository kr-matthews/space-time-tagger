package com.example.space_timetagger.sessions.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailScreen
import com.example.space_timetagger.sessions.presentation.sessionsList.SessionsListScreen
import com.example.space_timetagger.sessions.presentation.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object Sessions

@Serializable
data class Session(val id: String)

@Serializable
data object Settings

@Composable
fun SttNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Sessions) {
        composable<Sessions> {
            SessionsListScreen(
                onNavigateToSession = { id -> navController.navigate(Session(id)) },
                onSettingsTap = { navController.navigate(Settings) },
                modifier = modifier
            )
        }

        composable<Session> { entry ->
            val session = entry.toRoute<Session>()
            SessionDetailScreen(
                id = session.id,
                onBackTap = navController::popBackStack,
                onSettingsTap = { navController.navigate(Settings) },
                modifier = modifier
            )
        }

        composable<Settings> {
            SettingsScreen(
                onBackTap = navController::popBackStack,
                modifier = modifier
            )
        }
    }
}