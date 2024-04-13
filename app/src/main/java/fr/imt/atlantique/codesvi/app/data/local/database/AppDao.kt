package fr.imt.atlantique.codesvi.app.data.local.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert
    suspend fun insertSettings(settings: SettingsEntity)

    @Query("SELECT * FROM settings_table WHERE id = 0")
    suspend fun getSettings(): SettingsEntity?

    @Update
    suspend fun updateSettings(settings: SettingsEntity)
}