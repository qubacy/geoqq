package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.tokenDataStore
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.impl.LocalTokenDataStoreDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalTokenDataStoreDataSourceImplTest {
    private lateinit var mTokenDataStore: DataStore<Preferences>

    private lateinit var mTokenDataSource: LocalTokenDataStoreDataSourceImpl

    @Before
    fun setup() {
        initTokenDataSource()
    }

    @After
    fun clear() = runTest {
        mTokenDataStore.edit { it.clear() }
    }

    private fun initTokenDataSource() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        mTokenDataStore = context.tokenDataStore
        mTokenDataSource = LocalTokenDataStoreDataSourceImpl(mTokenDataStore)
    }

    @Test
    fun getRefreshTokenTest() = runTest {
        val expectedRefreshToken = "test"

        mTokenDataStore.edit {
            it[LocalTokenDataStoreDataSource.REFRESH_TOKEN_KEY] = expectedRefreshToken
        }

        val gottenRefreshToken = mTokenDataSource.getRefreshToken()

        Assert.assertEquals(expectedRefreshToken, gottenRefreshToken)
    }

    @Test
    fun getAccessTokenTest() = runTest {
        val expectedAccessToken = "test"

        mTokenDataStore.edit {
            it[LocalTokenDataStoreDataSource.ACCESS_TOKEN_KEY] = expectedAccessToken
        }

        val gottenAccessToken = mTokenDataSource.getAccessToken()

        Assert.assertEquals(expectedAccessToken, gottenAccessToken)
    }

    @Test
    fun saveTokensTest() = runTest {
        val expectedAccessToken = "test"
        val expectedRefreshToken = "test"

        mTokenDataSource.saveTokens(expectedAccessToken, expectedRefreshToken)

        val gottenAccessToken = mTokenDataSource.getRefreshToken()
        val gottenRefreshToken = mTokenDataSource.getRefreshToken()

        Assert.assertEquals(expectedAccessToken, gottenAccessToken)
        Assert.assertEquals(expectedRefreshToken, gottenRefreshToken)
    }

    @Test
    fun clearTokensTest() = runTest {
        val accessToken = "test"
        val refreshToken = "test"

        mTokenDataStore.edit {
            it[LocalTokenDataStoreDataSource.ACCESS_TOKEN_KEY] = accessToken
            it[LocalTokenDataStoreDataSource.REFRESH_TOKEN_KEY] = refreshToken
        }

        mTokenDataSource.clearTokens()

        val gottenAccessToken = mTokenDataSource.getAccessToken()
        val gottenRefreshToken = mTokenDataSource.getRefreshToken()

        Assert.assertTrue(gottenAccessToken.isNullOrEmpty())
        Assert.assertTrue(gottenRefreshToken.isNullOrEmpty())
    }
}