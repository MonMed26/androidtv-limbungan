package com.iptv.androidtv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iptv.androidtv.data.model.Channel
import com.iptv.androidtv.data.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val groupedChannels: Map<String, List<Channel>> = emptyMap(),
    val favorites: List<Channel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    private fun loadChannels() {
        viewModelScope.launch {
            // Observe grouped channels
            repository.groups.combine(repository.allChannels) { groups, allChannels ->
                val grouped = mutableMapOf<String, List<Channel>>()
                for (group in groups) {
                    grouped[group] = allChannels.filter { it.groupTitle == group }
                }
                grouped
            }.collect { grouped ->
                _uiState.value = _uiState.value.copy(
                    groupedChannels = grouped,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            repository.favorites.collect { favs ->
                _uiState.value = _uiState.value.copy(favorites = favs)
            }
        }
    }

    fun toggleFavorite(channelId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(channelId)
        }
    }

    fun refreshPlaylist() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.refreshPlaylist()
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }
}
