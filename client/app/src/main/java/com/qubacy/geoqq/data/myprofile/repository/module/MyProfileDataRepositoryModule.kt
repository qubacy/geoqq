package com.qubacy.geoqq.data.myprofile.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.LocalMyProfileDataStoreDataSource
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.RemoteMyProfileHttpRestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MyProfileDataRepositoryModule {
    @Provides
    fun provideMyProfileDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        imageDataRepository: ImageDataRepository,
        localMyProfileDataStoreDataSource: LocalMyProfileDataStoreDataSource,
        remoteMyProfileHttpRestDataSource: RemoteMyProfileHttpRestDataSource
    ): MyProfileDataRepository {
        return MyProfileDataRepositoryImpl(
            mErrorSource = localErrorDataSource,
            mImageDataRepository = imageDataRepository,
            mLocalMyProfileDataStoreDataSource = localMyProfileDataStoreDataSource,
            mRemoteMyProfileHttpRestDataSource = remoteMyProfileHttpRestDataSource
        )
    }
}