package com.qubacy.geoqq.data.myprofile.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.image.repository.impl.ImageDataRepositoryImpl
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.impl.LocalMyProfileDataStoreDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest.impl.RemoteMyProfileHttpRestDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MyProfileDataRepositoryModule {
    @Provides
    fun provideMyProfileDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        imageDataRepository: ImageDataRepositoryImpl,
        localMyProfileDataSource: LocalMyProfileDataStoreDataSourceImpl,
        httpMyProfileDataSource: RemoteMyProfileHttpRestDataSourceImpl
    ): MyProfileDataRepositoryImpl {
        return MyProfileDataRepositoryImpl(
            mErrorSource = localErrorDataSource,
            mImageDataRepository = imageDataRepository,
            mLocalMyProfileDataSource = localMyProfileDataSource,
            mHttpMyProfileDataSource = httpMyProfileDataSource
        )
    }
}