package com.qubacy.geoqq.data.user.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserDataRepositoryModule {
    @Provides
    fun provideUserDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        imageDataRepository: ImageDataRepository,
        localUserDataSource: LocalUserDataSource,
        httpUserDataSource: HttpUserDataSource
    ): UserDataRepository {
        return UserDataRepository(
            mErrorSource = localErrorDataSource,
            mLocalTokenDataStoreDataSource = localTokenDataStoreDataSource,
            mImageDataRepository = imageDataRepository,
            mLocalUserDataSource = localUserDataSource,
            mHttpUserDataSource = httpUserDataSource
        )
    }
}