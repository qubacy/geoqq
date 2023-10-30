package com.qubacy.geoqq.data.token.repository.source.local

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalTokenDataSourceTest() {
    private lateinit var mTokenSharedPreferences: SharedPreferences

    @Before
    fun setup() {
        mTokenSharedPreferences = InstrumentationRegistry.getInstrumentation()
            .targetContext.getSharedPreferences(
                LocalTokenDataSource.TOKEN_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Test
    fun saveTokensThenLoadTokensTest() {
        val accessToken = "token"
        val refreshToken = "token"

        var localTokenDataSource = LocalTokenDataSource(mTokenSharedPreferences)

        localTokenDataSource.saveTokens(accessToken, refreshToken)

        localTokenDataSource = LocalTokenDataSource(mTokenSharedPreferences)

        val loadedRefreshToken = localTokenDataSource.loadRefreshToken()
        val gottenAccessToken = localTokenDataSource.accessToken

        Assert.assertEquals(refreshToken, loadedRefreshToken)
        Assert.assertNull(gottenAccessToken) // there is no sense to preserve this one;
    }
}