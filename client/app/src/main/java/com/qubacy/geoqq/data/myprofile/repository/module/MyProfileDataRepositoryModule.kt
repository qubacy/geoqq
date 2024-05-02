package com.qubacy.geoqq.data.myprofile.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MyProfileDataRepositoryModule {
    @Provides
    fun provideMyProfileDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        imageDataRepository: ImageDataRepository,
        localMyProfileDataSource: LocalMyProfileDataSource,
        httpMyProfileDataSource: HttpMyProfileDataSource
    ): MyProfileDataRepository {
        return MyProfileDataRepository(
            mErrorSource = localErrorDataSource,
            mImageDataRepository = imageDataRepository,
            mLocalMyProfileDataSource = localMyProfileDataSource,
            mHttpMyProfileDataSource = httpMyProfileDataSource
        )
    }
}