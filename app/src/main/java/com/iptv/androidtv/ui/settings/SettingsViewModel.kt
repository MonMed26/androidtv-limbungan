package com.iptv.androidtv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptv.androidtv.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val playlistUrl: String = "",
    val channelCount: Int = 0,
    val isReloading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.playlistUrl.collect { url ->
                _uiState.value = _uiState.value.copy(playlistUrl = url)
            }
        }
        viewModelScope.launch {
            val count = repository.getChannelCount()
            _uiState.value = _uiState.value.copy(channelCount = count)
        }
    }

    fun onUrlChanged(url: String) {
        _uiState.value = _uiState.value.copy(playlistUrl = url, message = null)
    }

    fun reloadPlaylist() {
        val url = _uiState.value.playlistUrl.trim()
        if (url.isEmpty()) {
            _uiState.value = _uiState.value.copy(message = "URL cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isReloading = true, message = null)
            val result = repository.loadPlaylist(url)
            result.onSuccess { count ->
                _uiState.value = _uiState.value.copy(
                    isReloading = false,
                    channelCount = count,
                    message = "Loaded $count channels successfully"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isReloading = false,
                    message = "Error: ${error.message}"
                )
            }
        }
    }
}
