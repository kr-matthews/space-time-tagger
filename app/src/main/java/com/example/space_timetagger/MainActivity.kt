package com.example.space_timetagger

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.space_timetagger.sessions.navigation.SttNavHost
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        lifecycleScope.launch {
            App.appModule.preferencesRepository.keepScreenOnIsEnabled.collect { isEnabled ->
                if (isEnabled) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }

        setContent {
            val navController = rememberNavController()

            SpaceTimeTaggerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SttNavHost(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}
