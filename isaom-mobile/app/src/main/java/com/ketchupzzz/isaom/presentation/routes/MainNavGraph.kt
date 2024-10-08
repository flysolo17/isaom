package com.ketchupzzz.isaom.presentation.routes

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.ketchupzzz.isaom.models.Users
import com.ketchupzzz.isaom.models.subject.Subjects
import com.ketchupzzz.isaom.models.subject.activities.Activity
import com.ketchupzzz.isaom.models.subject.module.Modules
import com.ketchupzzz.isaom.presentation.auth.change_password.ChangePasswordScreen
import com.ketchupzzz.isaom.presentation.auth.change_password.ChangePasswordViewModel
import com.ketchupzzz.isaom.presentation.auth.edit_profile.EditProfileScreen
import com.ketchupzzz.isaom.presentation.auth.edit_profile.EditProfileViewModel
import com.ketchupzzz.isaom.presentation.main.about.AboutScreen
import com.ketchupzzz.isaom.presentation.main.dictionary.DictionaryScreen
import com.ketchupzzz.isaom.presentation.main.dictionary.DictionaryViewModel
import com.ketchupzzz.isaom.presentation.main.game.GameScreen
import com.ketchupzzz.isaom.presentation.main.game.GameViewModel
import com.ketchupzzz.isaom.presentation.main.gaming.GamingScreen
import com.ketchupzzz.isaom.presentation.main.gaming.GamingViewModel
import com.ketchupzzz.isaom.presentation.main.home.HomeScreen
import com.ketchupzzz.isaom.presentation.main.home.HomeViewModel

import com.ketchupzzz.isaom.presentation.main.leaderboard.StudentLeaderboardScreen
import com.ketchupzzz.isaom.presentation.main.leaderboard.StudentLeaderboardViewModel
import com.ketchupzzz.isaom.presentation.main.lessons.LessonScreen
import com.ketchupzzz.isaom.presentation.main.lessons.LessonViewModel
import com.ketchupzzz.isaom.presentation.main.profle.ProfileScreen
import com.ketchupzzz.isaom.presentation.main.profle.ProfileViewModel
import com.ketchupzzz.isaom.presentation.main.subject.activities.view.StudentViewActivity
import com.ketchupzzz.isaom.presentation.main.subject.activities.view.StudentViewActivityViewModel
import com.ketchupzzz.isaom.presentation.main.subject.modules.view.StudentViewModuleScreen
import com.ketchupzzz.isaom.presentation.main.subject.modules.view.StudentViewModuleViewModel
import com.ketchupzzz.isaom.presentation.main.subject.view_subject.StudentViewSubjectScreen
import com.ketchupzzz.isaom.presentation.main.subject.view_subject.StudentViewSubjectViewModel
import com.ketchupzzz.isaom.presentation.main.translator.TranslatorScreen
import com.ketchupzzz.isaom.presentation.main.translator.TranslatorViewModel
import com.ketchupzzz.isaom.presentation.teacher.subject.view_subject.modules.edit_module_content.EditContentScreen
import com.ketchupzzz.isaom.presentation.teacher.subject.view_subject.modules.edit_module_content.EditContentViewModel
import com.ketchupzzz.isaom.presentation.teacher.subject.view_subject.modules.edit_module_content.data.ContentWithModuleID
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainNavGraph(navHostController: NavHostController,mainNav : NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = AppRouter.HomeScreen.route
    ) {
        composable(route = AppRouter.HomeScreen.route) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(navHostController = navHostController, state = viewModel.state, events = viewModel::events)
        }
        composable(route = AppRouter.AboutScreen.route) {
            AboutScreen(navHostController= navHostController)
        }
        composable(route = AppRouter.TranslatorScreen.route) {
            val viewModel = hiltViewModel<TranslatorViewModel>()
            TranslatorScreen(state = viewModel.state, events = viewModel::events)
        }
        composable(route = AppRouter.GameRoute.route) {
            val viewModel = hiltViewModel<GameViewModel>()
            GameScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
        composable(route = AppRouter.LeaderboardRoute.route) {
            val viewModel = hiltViewModel<StudentLeaderboardViewModel>()
            StudentLeaderboardScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
        composable(route = AppRouter.GamingRoute.route) {
            val viewModel = hiltViewModel<GamingViewModel>()
            GamingScreen(state = viewModel.state, events = viewModel::events, navHostController = navHostController)
        }

        composable(route = AppRouter.StudentViewSubject.route) {

            val args = it.arguments?.getString("args")
            val decodedJson = URLDecoder.decode(args, StandardCharsets.UTF_8.toString())
            val content = Gson().fromJson(decodedJson, Subjects::class.java)
            val viewModel = hiltViewModel<StudentViewSubjectViewModel>()
            StudentViewSubjectScreen(
                subjectID = content.id ?: "",
                state = viewModel.state,
                event = viewModel::events,
                navHostController = navHostController,
            )
        }
        composable(route = AppRouter.StudentViewModule.route) {
            val args = it.arguments?.getString("args")
            val decodedJson = URLDecoder.decode(args, StandardCharsets.UTF_8.toString())
            val content = Gson().fromJson(decodedJson, Modules::class.java)
            val viewModel = hiltViewModel<StudentViewModuleViewModel>()
            StudentViewModuleScreen(
                modules = content,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(route = AppRouter.StudentViewActivity.route) {
            val args = it.arguments?.getString("args")
            val decodedJson = URLDecoder.decode(args, StandardCharsets.UTF_8.toString())
            val content = Gson().fromJson(decodedJson, Activity::class.java)
            val viewModel = hiltViewModel<StudentViewActivityViewModel>()
            StudentViewActivity(
                activity = content,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }
        composable(route = AppRouter.Dictionary.route) {
            val viewModel = hiltViewModel<DictionaryViewModel>()
            DictionaryScreen(navHostController = navHostController, state = viewModel.state, events = viewModel::events )
        }
        composable(route = AppRouter.Lessons.route) {
            val viewModel = hiltViewModel<LessonViewModel>()
            LessonScreen(navHostController = navHostController, state = viewModel.state, events = viewModel::events )
        }
        composable(route = AppRouter.ProfileScreen.route) {
            val viewModel = hiltViewModel<ProfileViewModel>()
            ProfileScreen(mainNav = mainNav, navHostController = navHostController, state = viewModel.state, events = viewModel::events)
        }
        composable(route = AppRouter.ChangePassword.route) {
            val viewModel = hiltViewModel<ChangePasswordViewModel>()
            ChangePasswordScreen(state = viewModel.state, events = viewModel::events, navHostController = navHostController)
        }
        composable(
            route = AppRouter.EditProfileRoute.route,

            ) {backStackEntry ->
            val args = backStackEntry.arguments?.getString("args")
            val decodedJson = URLDecoder.decode(args, StandardCharsets.UTF_8.toString())
            val users = Gson().fromJson(decodedJson, Users::class.java)
            val viewModel = hiltViewModel<EditProfileViewModel>()
            users?.let {
                EditProfileScreen(
                    users = it,
                    state = viewModel.state,
                    events = viewModel::events,
                    navHostController = navHostController
                )
            }
        }
    }

}