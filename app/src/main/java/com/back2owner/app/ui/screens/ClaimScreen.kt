package com.back2owner.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.back2owner.app.ui.viewmodel.ClaimDetailViewModel
import com.back2owner.app.ui.viewmodel.ItemDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimScreen(
    itemId: String,
    itemViewModel: ItemDetailViewModel = hiltViewModel(),
    claimViewModel: ClaimDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {
    val item by itemViewModel.item.collectAsState()
    val isItemLoading by itemViewModel.isLoading.collectAsState()
    
    val isClaimLoading by claimViewModel.isLoading.collectAsState()
    val error by claimViewModel.error.collectAsState()
    val success by claimViewModel.success.collectAsState()

    var answer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    LaunchedEffect(itemId) {
        itemViewModel.loadItem(itemId)
    }

    LaunchedEffect(success) {
        if (success) {
            onSuccess()
            claimViewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Ownership") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isItemLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (item != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Instruction Header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = "Submit a Claim",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Please answer the security question set by the founder to verify your ownership.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Security Question Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Security Question:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = item!!.securityQuestion,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Answer Input
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Your Answer") },
                        placeholder = { Text("Type your answer here...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) }
                    )

                    // Optional Message
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message (Optional)") },
                        placeholder = { Text("Add any additional details...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) }
                    )

                    // Error display
                    AnimatedVisibility(visible = error != null) {
                        Text(
                            text = error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            claimViewModel.clearError()
                            claimViewModel.submitClaim(itemId, answer, message)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isClaimLoading,
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (isClaimLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Submit Verification Answer", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
