package com.iptv.androidtv.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import com.iptv.androidtv.data.model.Channel
import com.iptv.androidtv.ui.theme.*

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onChannelClick: (Channel) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

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
                    text = "Search Channels",
                    style = TVTextStyles.headlineLarge,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search input
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                placeholder = { Text("Type channel name...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = TextSecondary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .focusRequester(focusRequester),
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

            // Results
            if (uiState.isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = AccentBlue
                )
            } else if (uiState.results.isEmpty() && uiState.query.length >= 2) {
                Text(
                    text = "No channels found for \"${uiState.query}\"",
                    style = TVTextStyles.bodyLarge,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = "${uiState.results.size} result(s)",
                    style = TVTextStyles.bodyMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                TvLazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.results, key = { it.id }) { channel ->
                        SearchResultItem(
                            channel = channel,
                            onClick = { onChannelClick(channel) }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun SearchResultItem(
    channel: Channel,
    onClick: () -> Unit
) {
    androidx.tv.material3.Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = androidx.tv.material3.ClickableSurfaceDefaults.shape(
            shape = RoundedCornerShape(8.dp)
        ),
        colors = androidx.tv.material3.ClickableSurfaceDefaults.colors(
            containerColor = CardBackground,
            focusedContainerColor = CardBackgroundFocused
        ),
        border = androidx.tv.material3.ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
                shape = RoundedCornerShape(8.dp)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${channel.channelNumber}",
                style = TVTextStyles.titleMedium,
                color = AccentBlue,
                modifier = Modifier.width(48.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    style = TVTextStyles.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (channel.groupTitle != null) {
                    Text(
                        text = channel.groupTitle,
                        style = TVTextStyles.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            if (channel.isFavorite) {
                Icon(Icons.Default.Star, "Favorite", tint = AccentOrange, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.PlayArrow, "Play", tint = TextSecondary, modifier = Modifier.size(24.dp))
        }
    }
}
