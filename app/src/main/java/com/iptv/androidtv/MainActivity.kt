package com.iptv.androidtv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iptv.androidtv.data.local.SettingsDataStore
import com.iptv.androidtv.ui.navigation.AppNavigation
import com.iptv.androidtv.ui.navigation.Routes
import com.iptv.androidtv.ui.theme.IPTVTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isFirstLaunch by settingsDataStore.isFirstLaunch
                .collectAsStateWithLifecycle(initialValue = true)

            IPTVTheme {
                val startDestination = if (isFirstLaunch) Routes.SETUP else Routes.HOME
                AppNavigation(startDestination = startDestination)
            }
        }
    }
}
