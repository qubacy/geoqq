package com.qubacy.geoqq.data.myprofile.repository.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object MyProfileDataRepositoryModule {
    @Provides
    fun provideMyProfileDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        imageDataRepository: ImageDataRepository,
        localMyProfileDataSource: LocalMyProfileDataSource,
        httpMyProfileDataSource: HttpMyProfileDataSource,
        httpClient: OkHttpClient
    ): MyProfileDataRepository {
        return MyProfileDataRepository(
            mErrorDataRepository = errorDataRepository,
            mTokenDataRepository = tokenDataRepository,
            mImageDataRepository = imageDataRepository,
            mLocalMyProfileDataSource = localMyProfileDataSource,
            mHttpMyProfileDataSource = httpMyProfileDataSource,
            mHttpClient = httpClient
        )
    }
}