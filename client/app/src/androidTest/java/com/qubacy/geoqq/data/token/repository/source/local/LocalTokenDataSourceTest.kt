package com.qubacy.geoqq.data.token.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalTokenDataSourceTest {
    private lateinit var mTokenDataSource: LocalTokenDataSource

    @Before
    fun setup() {
        initTokenDataSource()
    }

    private fun initTokenDataSource() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        mTokenDataSource = LocalTokenDataSource(context.tokenDataStore)
    }

    @Test
    fun saveTokensTest() = runTest {
        val expectedAccessToken = "test"
        val expectedRefreshToken = "test"

        mTokenDataSource.saveTokens(expectedAccessToken, expectedRefreshToken)

        val gottenAccessToken = mTokenDataSource.lastAccessToken
        val gottenRefreshToken = mTokenDataSource.getRefreshToken()

        Assert.assertEquals(expectedAccessToken, gottenAccessToken)
        Assert.assertEquals(expectedRefreshToken, gottenRefreshToken)
    }

    @Test
    fun clearTokensTest() = runTest {
        val accessToken = "test"
        val refreshToken = "test"

        mTokenDataSource.saveTokens(accessToken, refreshToken)
        mTokenDataSource.clearTokens()

        val gottenAccessToken = mTokenDataSource.lastAccessToken
        val gottenRefreshToken = mTokenDataSource.getRefreshToken()

        Assert.assertTrue(gottenAccessToken.isNullOrEmpty())
        Assert.assertTrue(gottenRefreshToken.isNullOrEmpty())
    }
}