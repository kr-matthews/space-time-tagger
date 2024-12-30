package com.example.space_timetagger.sessions.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailScreen
import com.example.space_timetagger.sessions.presentation.sessionsList.SessionsListScreen
import kotlinx.serialization.Serializable

@Serializable
object Sessions

@Serializable
data class Session(val id: String)

@Composable
fun SttNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Sessions) {
        composable<Sessions> {
            SessionsListScreen(modifier) { id -> navController.navigate(Session(id)) }
        }
        composable<Session> { entry ->
            val session = entry.toRoute<Session>()
            SessionDetailScreen(
                id = session.id,
                onBackTap = navController::popBackStack,
                onSettingsTap = {
                    // TODO: add settings destination, navigate to it
                },
                modifier = modifier
            )
        }
    }
}