package com.example.space_timetagger

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.space_timetagger.ui.sessionDetail.SessionDetailView
import com.example.space_timetagger.ui.sessionsList.SessionsListView
import kotlinx.serialization.Serializable

@Serializable
object Sessions

@Serializable
data class Session(val id: String)

@Composable
fun SttNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Sessions) {
        composable<Sessions> {
            SessionsListView(modifier) { id -> navController.navigate(Session(id)) }
        }
        composable<Session> { entry ->
            val session = entry.toRoute<Session>()
            SessionDetailView(session.id, modifier)
        }
    }
}