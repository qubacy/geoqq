package com.qubacy.geoqq.data.user.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserDataRepositoryModule {
    @Provides
    fun provideUserDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        imageDataRepository: ImageDataRepository,
        localUserDatabaseDataSource: LocalUserDatabaseDataSource,
        httpUserDataSource: RemoteUserHttpRestDataSource
    ): UserDataRepository {
        return UserDataRepositoryImpl(
            mErrorSource = localErrorDataSource,
            mLocalTokenDataStoreDataSource = localTokenDataStoreDataSource,
            mImageDataRepository = imageDataRepository,
            mLocalUserDatabaseDataSource = localUserDatabaseDataSource,
            mRemoteUserHttpRestDataSource = httpUserDataSource
        )
    }
}