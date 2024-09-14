package com.example.space_timetagger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.space_timetagger.ui.taglist.TagListView
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceTimeTaggerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TagListView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
