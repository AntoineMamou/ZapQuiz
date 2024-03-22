package fr.imt.atlantique.codesvi.app.repository

/*
import fr.imt.atlantique.codesvi.app.data.local.database.QcmDao
import fr.imt.atlantique.codesvi.app.data.local.database.QcmEntity
import fr.imt.atlantique.codesvi.app.dependency_injection.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QcmRepository@Inject constructor(
    private val qcmDao: QcmDao,
    @IoDispatcher val dispatcher: CoroutineDispatcher
) {

    fun getAllQCM() = qcmDao.getAllQCM()

    suspend fun insertQCM(qcm: QcmEntity) =  withContext(dispatcher) { qcmDao.insertQCM(qcm) }
}

 */