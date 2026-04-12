package com.back2owner.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.back2owner.app.ui.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onSignOut: () -> Unit,
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val signedOut by viewModel.signedOut.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    val scrollState = rememberScrollState()
    
    // Edit mode state
    var displayNameInput by remember(user) { mutableStateOf(user?.displayName ?: "") }
    var biographyInput by remember(user) { mutableStateOf(user?.biography ?: "") }

    LaunchedEffect(signedOut) {
        if (signedOut) {
            onSignOut()
        }
    }

    LaunchedEffect(user) {
        if (user != null && !isEditMode) {
            displayNameInput = user!!.displayName
            biographyInput = user!!.biography
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    if (!isEditMode) {
                        IconButton(onClick = { viewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }
                    IconButton(onClick = { viewModel.signOut() }, enabled = !isEditMode) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            } else if (user != null) {
                val currentUser = user!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (isEditMode) {
                        EditProfileForm(
                            displayName = displayNameInput,
                            onDisplayNameChange = { displayNameInput = it },
                            biography = biographyInput,
                            onBiographyChange = { biographyInput = it },
                            isSaving = isSaving,
                            onSave = {
                                viewModel.updateProfile(displayNameInput, biographyInput)
                            },
                            onCancel = {
                                viewModel.toggleEditMode()
                                displayNameInput = currentUser.displayName
                                biographyInput = currentUser.biography
                            }
                        )
                    } else {
                        ViewProfileContent(currentUser = currentUser)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ViewProfileContent(currentUser: com.back2owner.app.data.model.User) {
    // Profile Header
    Surface(
        modifier = Modifier.size(120.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        if (currentUser.profilePhotoURL != null) {
            AsyncImage(
                model = currentUser.profilePhotoURL,
                contentDescription = "Profile Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = currentUser.displayName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currentUser.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Stat Cards
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Reported",
            value = currentUser.itemsReported.toString(),
            icon = Icons.Default.Assignment,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Found",
            value = currentUser.itemsFound.toString(),
            icon = Icons.Default.EmojiEvents,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Rating",
            value = String.format("%.1f", currentUser.rating),
            icon = Icons.Default.Star,
            modifier = Modifier.weight(1f)
        )
    }

    // BioSection
    if (currentUser.biography.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "About Me",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = currentUser.biography,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Bottom list
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Account Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        ListItem(
            headlineContent = { Text("College ID") },
            trailingContent = { Text(currentUser.collegeID) }
        )
        ListItem(
            headlineContent = { Text("Member Since") },
            trailingContent = { Text(SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(currentUser.createdAt))) }
        )
    }
}

@Composable
private fun EditProfileForm(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    biography: String,
    onBiographyChange: (String) -> Unit,
    isSaving: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Text(
        "Edit Profile",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    OutlinedTextField(
        value = displayName,
        onValueChange = onDisplayNameChange,
        label = { Text("Display Name") },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isSaving,
        singleLine = true
    )

    OutlinedTextField(
        value = biography,
        onValueChange = onBiographyChange,
        label = { Text("About Me") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        enabled = !isSaving,
        maxLines = 5
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            enabled = !isSaving
        ) {
            Text("Cancel")
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
