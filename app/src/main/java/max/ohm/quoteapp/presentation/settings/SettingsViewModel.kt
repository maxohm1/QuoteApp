package max.ohm.quoteapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.AccentColor
import max.ohm.quoteapp.domain.model.FontSize
import max.ohm.quoteapp.domain.model.ThemeMode
import max.ohm.quoteapp.domain.model.UserSettings
import max.ohm.quoteapp.domain.repository.SettingsRepository
import javax.inject.Inject

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import max.ohm.quoteapp.util.NotificationScheduler

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())
    
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(themeMode = themeMode))
        }
    }
    
    fun updateAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(accentColor = accentColor))
        }
    }
    
    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(fontSize = fontSize))
        }
    }
    
    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(notificationEnabled = enabled))
            
            if (enabled) {
                // Schedule with existing time
                NotificationScheduler.scheduleDailyQuote(context, current.notificationTime)
            } else {
                NotificationScheduler.cancelDailyQuote(context)
            }
        }
    }
    
    fun updateNotificationTime(time: String) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings()
            settingsRepository.updateSettings(current.copy(notificationTime = time))
            
            if (current.notificationEnabled || true) { // Always schedule if updating time? Or only if enabled?
                // Logic: If user changes time, they probably want it enabled or ready. 
                // But let's check current.notificationEnabled. 
                // Note: current.notificationEnabled might be true.
                
                // If it IS enabled, reschedule.
                // If it is NOT enabled, do we schedule? No.
                
                // However, I should check the updated value? "current" is snapshot before update.
                // I am updating "notificationTime". "notificationEnabled" remains same.
                if (current.notificationEnabled) {
                     NotificationScheduler.scheduleDailyQuote(context, time)
                }
            }
        }
    }
}
