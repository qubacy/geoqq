package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._test.mock

import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class LocalTokenDataStoreDataSourceMockContainer {
    companion object {
        const val VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJleHAiOjE5MDAwMDAwMDAsInVzZXItaWQiOjB9.4BS0ogDvWxgxfaHYkYfQhLw-Csg-y7vgTmHTz4jTqA4"
    }

    val localTokenDataStoreDataSourceMock: LocalTokenDataStoreDataSource

    var getAccessToken: String? = null
    var getRefreshToken: String? = null

    private var mGetAccessTokenCallFlag = false
    val getAccessTokenCallFlag get() = mGetAccessTokenCallFlag
    private var mGetRefreshTokenCallFlag = false
    val getRefreshTokenCallFlag get() = mGetRefreshTokenCallFlag
    private var mSaveTokensCallFlag = false
    val saveTokensCallFlag get() = mSaveTokensCallFlag
    private var mClearTokensCallFlag = false
    val clearTokensCallFlag get() = mClearTokensCallFlag

    init {
        localTokenDataStoreDataSourceMock = Mockito.mock(LocalTokenDataStoreDataSource::class.java)

        runTest {
            Mockito.`when`(localTokenDataStoreDataSourceMock.getAccessToken()).thenAnswer {
                mGetAccessTokenCallFlag = true
                getAccessToken
            }
            Mockito.`when`(localTokenDataStoreDataSourceMock.getRefreshToken()).thenAnswer {
                mGetRefreshTokenCallFlag = true
                getRefreshToken
            }
            Mockito.`when`(localTokenDataStoreDataSourceMock.saveTokens(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mSaveTokensCallFlag = true

                Unit
            }
            Mockito.`when`(localTokenDataStoreDataSourceMock.clearTokens()).thenAnswer {
                mClearTokensCallFlag = true

                Unit
            }
        }
    }

    fun reset() {
        getAccessToken = null
        getRefreshToken = null

        mGetAccessTokenCallFlag = false
        mGetRefreshTokenCallFlag = false
        mSaveTokensCallFlag = false
        mClearTokensCallFlag = false
    }
}