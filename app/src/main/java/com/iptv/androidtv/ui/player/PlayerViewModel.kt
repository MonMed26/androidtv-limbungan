package com.iptv.androidtv.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptv.androidtv.data.model.Channel
import com.iptv.androidtv.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentChannel: Channel? = null,
    val channels: List<Channel> = emptyList(),
    val currentIndex: Int = 0,
    val showOverlay: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: ChannelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val channelId: Long = savedStateHandle["channelId"] ?: 0L

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        loadChannel()
    }

    private fun loadChannel() {
        viewModelScope.launch {
            repository.allChannels.first().let { allChannels ->
                val index = allChannels.indexOfFirst { it.id == channelId }
                val channel = if (index >= 0) allChannels[index] else allChannels.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    currentChannel = channel,
                    channels = allChannels,
                    currentIndex = if (index >= 0) index else 0,
                    isLoading = false,
                    showOverlay = true
                )
            }
        }
    }

    fun channelUp() {
        val state = _uiState.value
        if (state.channels.isEmpty()) return
        val newIndex = if (state.currentIndex > 0) state.currentIndex - 1 else state.channels.lastIndex
        _uiState.value = state.copy(
            currentIndex = newIndex,
            currentChannel = state.channels[newIndex],
            showOverlay = true,
            error = null
        )
    }

    fun channelDown() {
        val state = _uiState.value
        if (state.channels.isEmpty()) return
        val newIndex = if (state.currentIndex < state.channels.lastIndex) state.currentIndex + 1 else 0
        _uiState.value = state.copy(
            currentIndex = newIndex,
            currentChannel = state.channels[newIndex],
            showOverlay = true,
            error = null
        )
    }

    fun selectChannel(channel: Channel) {
        val state = _uiState.value
        val index = state.channels.indexOfFirst { it.id == channel.id }
        if (index >= 0) {
            _uiState.value = state.copy(
                currentIndex = index,
                currentChannel = channel,
                showOverlay = false,
                error = null
            )
        }
    }

    fun toggleOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = !_uiState.value.showOverlay)
    }

    fun hideOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = false)
    }

    fun showOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = true)
    }

    fun onPlayerError(message: String) {
        _uiState.value = _uiState.value.copy(error = message, showOverlay = true)
    }

    fun toggleFavorite() {
        val channel = _uiState.value.currentChannel ?: return
        viewModelScope.launch {
            repository.toggleFavorite(channel.id)
            // Update local state
            _uiState.value = _uiState.value.copy(
                currentChannel = channel.copy(isFavorite = !channel.isFavorite)
            )
        }
    }
}
