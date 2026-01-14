package max.ohm.quoteapp.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import max.ohm.quoteapp.domain.model.QuoteCategory
import max.ohm.quoteapp.presentation.auth.AuthViewModel
import max.ohm.quoteapp.presentation.auth.ForgotPasswordScreen
import max.ohm.quoteapp.presentation.auth.LoginScreen
import max.ohm.quoteapp.presentation.auth.SignUpScreen
import max.ohm.quoteapp.presentation.collections.CollectionsScreen
import max.ohm.quoteapp.presentation.collections.CollectionsViewModel
import max.ohm.quoteapp.presentation.favorites.FavoritesScreen
import max.ohm.quoteapp.presentation.home.HomeScreen
import max.ohm.quoteapp.presentation.profile.ProfileScreen
import max.ohm.quoteapp.presentation.settings.SettingsScreen
import max.ohm.quoteapp.presentation.share.ShareScreen

data class BottomNavItemData(
    val route: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItemData(
        route = Screen.Home,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItemData(
        route = Screen.Favorites,
        title = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItemData(
        route = Screen.Collections,
        title = "Collections",
        selectedIcon = Icons.Filled.CollectionsBookmark,
        unselectedIcon = Icons.Outlined.CollectionsBookmark
    ),
    BottomNavItemData(
        route = Screen.Profile,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Login,
    collectionsViewModel: CollectionsViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val collectionsUiState by collectionsViewModel.uiState.collectAsState()
    
    // Determine if bottom bar should be visible
    val showBottomBar = remember(currentDestination) {
        currentDestination?.let { dest ->
            bottomNavItems.any { dest.hasRoute(it.route::class) }
        } ?: false
    }
    
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true
                        
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Auth Screens
            composable<Screen.Login> {
                LoginScreen(
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignUp)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword)
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    }
                )
            }
            
            composable<Screen.SignUp> {
                SignUpScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    }
                )
            }
            
            composable<Screen.ForgotPassword> {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Main Screens
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateToCategory = { category ->
                        navController.navigate(Screen.CategoryQuotes(category.name))
                    },
                    onNavigateToQuoteDetail = { quoteId ->
                        navController.navigate(Screen.QuoteDetail(quoteId))
                    },
                    onNavigateToShare = { quoteId ->
                        navController.navigate(Screen.ShareQuote(quoteId))
                    },
                    onAddToCollection = { quoteId ->
                        collectionsViewModel.showAddToCollectionDialog(quoteId)
                    }
                )
            }
            
            composable<Screen.Favorites> {
                FavoritesScreen(
                    onNavigateToQuoteDetail = { quoteId ->
                        navController.navigate(Screen.QuoteDetail(quoteId))
                    },
                    onNavigateToShare = { quoteId ->
                        navController.navigate(Screen.ShareQuote(quoteId))
                    },
                    onAddToCollection = { quoteId ->
                        collectionsViewModel.showAddToCollectionDialog(quoteId)
                    }
                )
            }
            
            composable<Screen.Collections> {
                CollectionsScreen(
                    onNavigateToCollectionDetail = { collectionId ->
                        navController.navigate(Screen.CollectionDetail(collectionId))
                    },
                    viewModel = collectionsViewModel
                )
            }
            
            composable<Screen.Profile> {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login) {
                            popUpTo(Screen.Home) { inclusive = true }
                        }
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites)
                    },
                    onNavigateToCollections = {
                        navController.navigate(Screen.Collections)
                    }
                )
            }
            
            composable<Screen.Settings> {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<Screen.ShareQuote> { backStackEntry ->
                val shareQuote: Screen.ShareQuote = backStackEntry.toRoute()
                ShareScreen(
                    quoteId = shareQuote.quoteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<Screen.CategoryQuotes> { backStackEntry ->
                val categoryQuotes: Screen.CategoryQuotes = backStackEntry.toRoute()
                // For now, navigate back - could add a dedicated category screen
                HomeScreen(
                    onNavigateToCategory = { },
                    onNavigateToQuoteDetail = { quoteId ->
                        navController.navigate(Screen.QuoteDetail(quoteId))
                    },
                    onNavigateToShare = { quoteId ->
                        navController.navigate(Screen.ShareQuote(quoteId))
                    },
                    onAddToCollection = { quoteId ->
                        collectionsViewModel.showAddToCollectionDialog(quoteId)
                    }
                )
            }
        }
    }
    
    // Add to Collection Dialog
    if (collectionsUiState.showAddToCollectionDialog) {
        max.ohm.quoteapp.presentation.collections.AddToCollectionDialog(
            collections = collectionsUiState.collections,
            onCollectionSelected = { collectionId ->
                collectionsViewModel.addQuoteToCollection(collectionId)
            },
            onDismiss = { collectionsViewModel.hideAddToCollectionDialog() },
            onCreateNew = {
                collectionsViewModel.hideAddToCollectionDialog()
                collectionsViewModel.showCreateDialog()
            }
        )
    }
}
