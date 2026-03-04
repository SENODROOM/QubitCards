package com.flashcardai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flashcardai.presentation.screens.generate.GenerateScreen
import com.flashcardai.presentation.screens.home.HomeScreen
import com.flashcardai.presentation.screens.manage.ManageCardsScreen
import com.flashcardai.presentation.screens.quiz.QuizScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Generate : Screen("generate")
    object Quiz : Screen("quiz/{deckId}") {
        fun createRoute(deckId: Long) = "quiz/$deckId"
    }
    object ManageCards : Screen("manage/{deckId}") {
        fun createRoute(deckId: Long) = "manage/$deckId"
    }
}

@Composable
fun FlashCardNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGenerate = { navController.navigate(Screen.Generate.route) },
                onNavigateToQuiz = { deckId -> navController.navigate(Screen.Quiz.createRoute(deckId)) },
                onNavigateToManage = { deckId -> navController.navigate(Screen.ManageCards.createRoute(deckId)) }
            )
        }

        composable(Screen.Generate.route) {
            GenerateScreen(
                onBack = { navController.popBackStack() },
                onNavigateToQuiz = { deckId ->
                    navController.navigate(Screen.Quiz.createRoute(deckId)) {
                        popUpTo(Screen.Generate.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            QuizScreen(
                deckId = deckId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ManageCards.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: return@composable
            ManageCardsScreen(
                deckId = deckId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
