package fr.imt.atlantique.codesvi.app.data.local.database


import androidx.room.Entity
import androidx.room.PrimaryKey

const val QCM_TABLE = "qcm_table"

@Entity(tableName = "settings_table")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val connexion: Boolean,
    val notifications: Boolean,
    val effetSonores: Boolean
)
