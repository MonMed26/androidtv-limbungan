package com.iptv.androidtv.ui.player

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.iptv.androidtv.data.model.Channel
import com.iptv.androidtv.ui.theme.*
import android.view.KeyEvent
import androidx.compose.ui.input.key.*
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Create ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // Auto-hide overlay after 5 seconds
    LaunchedEffect(uiState.showOverlay, uiState.currentChannel) {
        if (uiState.showOverlay) {
            delay(5000)
            viewModel.hideOverlay()
        }
    }

    // Update player when channel changes
    LaunchedEffect(uiState.currentChannel) {
        uiState.currentChannel?.let { channel ->
            exoPlayer.stop()
            exoPlayer.setMediaItem(MediaItem.fromUri(channel.url))
            exoPlayer.prepare()
        }
    }

    // Player error listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                viewModel.onPlayerError("Playback error: ${error.message}")
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Full screen player with key handling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            viewModel.channelUp()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            viewModel.channelDown()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            viewModel.toggleOverlay()
                            true
                        }
                        KeyEvent.KEYCODE_BACK -> {
                            if (uiState.showOverlay) {
                                viewModel.hideOverlay()
                                true
                            } else {
                                onBack()
                                true
                            }
                        }
                        KeyEvent.KEYCODE_MENU -> {
                            viewModel.toggleFavorite()
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        // ExoPlayer View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Channel Info Overlay
        AnimatedVisibility(
            visible = uiState.showOverlay,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ChannelOverlay(
                channel = uiState.currentChannel,
                channelIndex = uiState.currentIndex,
                totalChannels = uiState.channels.size,
                error = uiState.error
            )
        }

        // Channel switching indicator
        AnimatedVisibility(
            visible = uiState.showOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        OverlaySurface,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "Channel Up", tint = TextSecondary, modifier = Modifier.size(24.dp))
                Text("CH", style = TVTextStyles.labelLarge, color = TextPrimary)
                Icon(Icons.Default.KeyboardArrowDown, "Channel Down", tint = TextSecondary, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun ChannelOverlay(
    channel: Channel?,
    channelIndex: Int,
    totalChannels: Int,
    error: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, OverlayBackground)
                )
            )
            .padding(top = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel number
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(AccentBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${channelIndex + 1}",
                    style = TVTextStyles.headlineLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Channel info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel?.name ?: "Unknown",
                    style = TVTextStyles.headlineLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (channel?.groupTitle != null) {
                        Text(
                            text = channel.groupTitle,
                            style = TVTextStyles.bodyLarge,
                            color = AccentBlue
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(
                        text = "${channelIndex + 1} / $totalChannels",
                        style = TVTextStyles.bodyLarge,
                        color = TextSecondary
                    )

                    if (channel?.isFavorite == true) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            Icons.Default.Star,
                            "Favorite",
                            tint = AccentOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = error,
                        style = TVTextStyles.bodyMedium,
                        color = ErrorRed
                    )
                }
            }

            // Controls hint
            Column(horizontalAlignment = Alignment.End) {
                Text("OK: Show/Hide", style = TVTextStyles.bodyMedium, color = TextMuted)
                Text("▲▼: Switch CH", style = TVTextStyles.bodyMedium, color = TextMuted)
                Text("MENU: Favorite", style = TVTextStyles.bodyMedium, color = TextMuted)
            }
        }
    }
}
