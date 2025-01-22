package com.example.space_timetagger

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.space_timetagger.sessions.navigation.SttNavHost
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
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
