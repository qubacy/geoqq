package com.qubacy.geoqq.data.mate.message.repository.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MateMessageDataRepositoryModule {
    @Provides
    fun provideMateMessageDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        localMateMessageDataSource: LocalMateMessageDataSource,
        httpMateMessageDataSource: HttpMateMessageDataSource
    ): MateMessageDataRepository {
        return MateMessageDataRepository(
            errorDataRepository = errorDataRepository,
            tokenDataRepository = tokenDataRepository,
            localMateMessageDataSource = localMateMessageDataSource,
            httpMateMessageDataSource = httpMateMessageDataSource
        )
    }
}