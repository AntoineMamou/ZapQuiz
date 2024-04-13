package fr.imt.atlantique.codesvi.app.data.model

import fr.imt.atlantique.codesvi.app.data.local.database.SettingsDao
import fr.imt.atlantique.codesvi.app.data.local.database.SettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsRepository(private val settingsDao: SettingsDao) {

    suspend fun insertSettings(settings: SettingsEntity) {
        withContext(Dispatchers.IO) {
            settingsDao.insertSettings(settings)
        }
    }

    suspend fun getSettings(): SettingsEntity? {
        return withContext(Dispatchers.IO) {
            settingsDao.getSettings()
        }
    }

    suspend fun updateSettings(settings: SettingsEntity) {
        withContext(Dispatchers.IO) {
            settingsDao.updateSettings(settings)
        }
    }
}
