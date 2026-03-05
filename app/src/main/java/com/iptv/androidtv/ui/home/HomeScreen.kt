package com.iptv.androidtv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import com.iptv.androidtv.data.model.Channel
import com.iptv.androidtv.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onChannelClick: (Channel) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentBlue
            )
        } else {
            TvLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                // Top bar
                item {
                    TopBar(onSearchClick = onSearchClick, onSettingsClick = onSettingsClick)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Favorites row
                if (uiState.favorites.isNotEmpty()) {
                    item {
                        CategoryRow(
                            title = "★ Favorites",
                            channels = uiState.favorites,
                            onChannelClick = onChannelClick,
                            onLongClick = { viewModel.toggleFavorite(it.id) }
                        )
                    }
                }

                // Category rows
                val groups = uiState.groupedChannels.keys.toList()
                items(groups.size) { index ->
                    val group = groups[index]
                    val channels = uiState.groupedChannels[group] ?: emptyList()
                    CategoryRow(
                        title = group,
                        channels = channels,
                        onChannelClick = onChannelClick,
                        onLongClick = { viewModel.toggleFavorite(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LiveTv,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "IPTV Player",
            style = TVTextStyles.headlineLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        // Search button
        androidx.tv.material3.IconButton(
            onClick = onSearchClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Settings button
        androidx.tv.material3.IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun CategoryRow(
    title: String,
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit,
    onLongClick: (Channel) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = TVTextStyles.headlineMedium,
            color = TextPrimary,
            modifier = Modifier.padding(start = 48.dp, bottom = 12.dp)
        )

        TvLazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                ChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel) },
                    onLongClick = { onLongClick(channel) }
                )
            }
        }
    }
}
