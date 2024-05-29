package com.qubacy.geoqq.data.myprofile.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.LocalMyProfileDataStoreDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.RemoteMyProfileHttpRestDataSource
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class MyProfileDataRepositoryModule {
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideMyProfileDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            imageDataRepository: ImageDataRepository,
            localMyProfileDataStoreDataSource: LocalMyProfileDataStoreDataSource,
            remoteMyProfileHttpRestDataSource: RemoteMyProfileHttpRestDataSource
        ): MyProfileDataRepository {
            return MyProfileDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mImageDataRepository = imageDataRepository,
                mLocalMyProfileDataStoreDataSource = localMyProfileDataStoreDataSource,
                mRemoteMyProfileHttpRestDataSource = remoteMyProfileHttpRestDataSource
            )
        }
    }
}