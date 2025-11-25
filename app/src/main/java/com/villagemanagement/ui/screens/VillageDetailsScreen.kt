package com.villagemanagement.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.villagemanagement.data.model.UserRole
import com.villagemanagement.ui.viewmodel.AuthViewModel
import com.villagemanagement.ui.viewmodel.VillageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillageDetailsScreen(
    villageId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToSuggestions: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    villageViewModel: VillageViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val villageState by villageViewModel.villageState.collectAsState()
    val user = authState.user
    val village = villageState.selectedVillage

    LaunchedEffect(villageId) {
        villageViewModel.loadVillage(villageId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(village?.name ?: "Village Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (villageState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (village != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Village Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(MaterialTheme.shapes.large)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🏘️",
                                    style = MaterialTheme.typography.displayLarge
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = village.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (village.description.isNotEmpty()) {
                                Text(
                                    text = village.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Village Info
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Village Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            InfoRow(
                                icon = Icons.Default.People,
                                label = "Population",
                                value = village.population.toString()
                            )
                            
                            InfoRow(
                                icon = Icons.Default.Person,
                                label = "Admin",
                                value = village.adminName
                            )
                            
                            InfoRow(
                                icon = Icons.Default.Phone,
                                label = "Contact",
                                value = village.adminContact
                            )
                            
                            if (village.area.isNotEmpty()) {
                                InfoRow(
                                    icon = Icons.Default.Landscape,
                                    label = "Area",
                                    value = "${village.area} sq km"
                                )
                            }
                        }
                    }
                }

                // Quick Actions
                item {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            icon = Icons.Default.Map,
                            title = "Map",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMap
                        )
                        ActionCard(
                            icon = Icons.Default.Help,
                            title = "Help",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToHelp
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            icon = Icons.Default.Lightbulb,
                            title = "Suggestions",
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSuggestions
                        )
                        
                        if (user?.getUserRole() == UserRole.VILLAGE_ADMIN || 
                            user?.getUserRole() == UserRole.SUPER_ADMIN) {
                            ActionCard(
                                icon = Icons.Default.AdminPanelSettings,
                                title = "Admin",
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToAdmin
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
