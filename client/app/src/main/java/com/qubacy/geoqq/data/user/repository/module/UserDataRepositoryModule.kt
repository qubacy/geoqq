package com.qubacy.geoqq.data.user.repository.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object UserDataRepositoryModule {
    @Provides
    fun provideUserDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        imageDataRepository: ImageDataRepository,
        localUserDataSource: LocalUserDataSource,
        httpUserDataSource: HttpUserDataSource,
        httpClient: OkHttpClient
    ): UserDataRepository {
        return UserDataRepository(
            mErrorDataRepository = errorDataRepository,
            mTokenDataRepository = tokenDataRepository,
            mImageDataRepository = imageDataRepository,
            mLocalUserDataSource = localUserDataSource,
            mHttpUserDataSource = httpUserDataSource,
            mHttpClient = httpClient
        )
    }
}