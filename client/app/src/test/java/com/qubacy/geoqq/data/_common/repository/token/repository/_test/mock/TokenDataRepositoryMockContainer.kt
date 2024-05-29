package com.qubacy.geoqq.data._common.repository.token.repository._test.mock

import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.update.UpdateTokensDataResult
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class TokenDataRepositoryMockContainer {
    val tokenDataRepository: TokenDataRepository

    var getTokensDataResult: GetTokensDataResult? = null
    var updateTokensDataResult: UpdateTokensDataResult? = null

    var onUpdateTokensAction: (() -> Unit)? = null

    private var mGetTokensCallFlag = false
    val getTokensCallFlag get() = mGetTokensCallFlag

    private var mUpdateTokensCallFlag = false
    val updateTokensCallFlag get() = mUpdateTokensCallFlag

    private var mSaveTokensCallFlag = false
    val saveTokensCallFlag get() = mSaveTokensCallFlag

    private var mResetCallFlag = false
    val resetCallFlag get() = mResetCallFlag

    init {
        tokenDataRepository = mockTokenDataRepository()
    }

    fun clear() {
        getTokensDataResult = null
        updateTokensDataResult = null

        onUpdateTokensAction = null

        mGetTokensCallFlag = false
        mUpdateTokensCallFlag = false
        mSaveTokensCallFlag = false
        mResetCallFlag = false
    }

    private fun mockTokenDataRepository(): TokenDataRepository {
        val tokenDataRepository = Mockito.mock(TokenDataRepository::class.java)

        runTest {
            Mockito.`when`(tokenDataRepository.getTokens()).thenAnswer {
                mGetTokensCallFlag = true
                getTokensDataResult
            }
            Mockito.`when`(tokenDataRepository.updateTokens()).thenAnswer {
                mUpdateTokensCallFlag = true
                onUpdateTokensAction?.invoke()
                updateTokensDataResult
            }
            Mockito.`when`(tokenDataRepository.saveTokens(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mSaveTokensCallFlag = true

                Unit
            }
            Mockito.`when`(tokenDataRepository.reset()).thenAnswer {
                mResetCallFlag = true

                Unit
            }
        }

        return tokenDataRepository
    }
}