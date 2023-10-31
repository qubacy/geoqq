package com.qubacy.geoqq.data.common.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource

abstract class TokenBasedRepositoryTest(

) {
    private val mTokenSharedPreferences: SharedPreferences

    protected lateinit var mTokenDataRepository: TokenDataRepository

    init {
        mTokenSharedPreferences = InstrumentationRegistry.getInstrumentation()
            .targetContext.getSharedPreferences(
                LocalTokenDataSource.TOKEN_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    protected fun initTokenDataRepository(
        code: Int = 200,
        responseString: String = String()
    ) {
        val localTokenDataSource = LocalTokenDataSource(mTokenSharedPreferences)
        val networkTokenDataSource = NetworkTestContext
            .generateTestRetrofit(
                NetworkTestContext.generateDefaultTestInterceptor(code, responseString))
            .create(NetworkTokenDataSource::class.java)

        mTokenDataRepository = TokenDataRepository(localTokenDataSource, networkTokenDataSource)
    }
}