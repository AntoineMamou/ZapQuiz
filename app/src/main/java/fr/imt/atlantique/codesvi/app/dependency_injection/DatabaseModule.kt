package fr.imt.atlantique.codesvi.app.dependency_injection

/*
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.imt.atlantique.codesvi.app.data.local.database.AppDatabase
import fr.imt.atlantique.codesvi.app.data.local.database.QCM_TABLE
import fr.imt.atlantique.codesvi.app.data.local.database.QcmDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        QCM_TABLE
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun providesQCMDao(database: AppDatabase): QcmDao = database.qcmDao
}

 */