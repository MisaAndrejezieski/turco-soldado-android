package com.trucosoldado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trucosoldado.ui.screens.TrucoScreen
import com.trucosoldado.viewmodel.TrucoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: TrucoViewModel = viewModel()
            TrucoScreen(viewModel = viewModel)
        }
    }
}
