package com.ketchupzzz.isaom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ketchupzzz.isaom.presentation.main.bottombar.MainScreen
import com.ketchupzzz.isaom.presentation.routes.AppRouter
import com.ketchupzzz.isaom.presentation.routes.authNavGraph
import com.ketchupzzz.isaom.presentation.teacher.main.TeacherMainScreen
import com.ketchupzzz.isaom.ui.theme.ISaomTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISaomTheme {
                val windowSize = calculateWindowSizeClass(activity = this)
                IsaomApp(windowSizeClass = windowSize)
            }
        }
    }
}

@Composable
fun IsaomApp(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRouter.AuthRoutes.route) {
        authNavGraph(navController)
        composable(route = AppRouter.MainRoutes.route) {
            MainScreen(mainNav = navController)
        }
        composable(route = AppRouter.TeacherRoutes.route) {
            TeacherMainScreen(mainNav = navController)
        }
    }
}