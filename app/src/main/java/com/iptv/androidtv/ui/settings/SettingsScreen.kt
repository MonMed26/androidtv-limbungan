package com.iptv.androidtv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iptv.androidtv.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.tv.material3.IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    style = TVTextStyles.headlineLarge,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Playlist URL section
            Text("Playlist URL", style = TVTextStyles.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.playlistUrl,
                onValueChange = viewModel::onUrlChanged,
                modifier = Modifier.fillMaxWidth(0.6f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = TextMuted,
                    cursorColor = AccentBlue,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Reload button
            androidx.tv.material3.Button(
                onClick = { viewModel.reloadPlaylist() },
                enabled = !uiState.isReloading,
                modifier = Modifier.height(48.dp),
                shape = androidx.tv.material3.ButtonDefaults.shape(
                    shape = RoundedCornerShape(12.dp)
                ),
                colors = androidx.tv.material3.ButtonDefaults.colors(
                    containerColor = AccentBlue,
                    focusedContainerColor = FocusBorder
                )
            ) {
                if (uiState.isReloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.Refresh, "Reload", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reload Playlist")
            }

            // Status message
            if (uiState.message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.message!!,
                    style = TVTextStyles.bodyLarge,
                    color = if (uiState.message!!.startsWith("Error")) ErrorRed else SuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Channel count info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LiveTv, null, tint = AccentBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Total Channels: ${uiState.channelCount}",
                    style = TVTextStyles.titleMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // App info
            Text(
                text = "IPTV Player v1.0.0",
                style = TVTextStyles.bodyMedium,
                color = TextMuted
            )
        }
    }
}
