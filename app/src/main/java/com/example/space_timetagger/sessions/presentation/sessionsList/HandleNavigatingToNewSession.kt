package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun HandleNavigatingToNewSession(
    sessionIdToNavigateTo: Flow<String>,
    navigateToSession: (String) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // to ensure it doesn't get lost during, say, screen rotation
            withContext(Dispatchers.Main.immediate) {
                sessionIdToNavigateTo.collect(navigateToSession)
            }
        }
    }
}
