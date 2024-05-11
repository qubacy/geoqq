package com.qubacy.geoqq.domain.mate.request.usecase._test.mock

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.impl.MateRequestUseCaseImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

class MateRequestUseCaseMockContainer {
    val mateRequestUseCaseMock: MateRequestUseCaseImpl

    val resultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()

    private var mAnswerMateRequestCallFlag = false
    val answerMateRequestCallFlag get() = mAnswerMateRequestCallFlag

    private var mSendMateRequestCallFlag = false
    val sendMateRequestCallFlag get() = mSendMateRequestCallFlag

    init {
        mateRequestUseCaseMock = mockMateRequestUseCase()
    }

    fun clear() {
        mAnswerMateRequestCallFlag = false
        mSendMateRequestCallFlag = false
    }

    private fun mockMateRequestUseCase(): MateRequestUseCaseImpl {
        val mateRequestUseCaseMock = Mockito.mock(MateRequestUseCaseImpl::class.java)

        Mockito.`when`(mateRequestUseCaseMock.resultFlow).thenReturn(resultFlow)
        Mockito.`when`(mateRequestUseCaseMock.answerMateRequest(
            Mockito.anyLong(), Mockito.anyBoolean()
        )).thenAnswer {
            mAnswerMateRequestCallFlag = true

            Unit
        }
        Mockito.`when`(mateRequestUseCaseMock.sendMateRequest(Mockito.anyLong())).thenAnswer {
            mSendMateRequestCallFlag = true

            Unit
        }

        return mateRequestUseCaseMock
    }
}