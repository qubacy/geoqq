package com.qubacy.geoqq.data.mate.request.repository._common._test.mock

import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common.result.get.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository._common.result.get.GetMateRequestsDataResult
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class MateRequestDataRepositoryMockContainer {
    val mateRequestDataRepositoryMock: MateRequestDataRepository

    var getMateRequestsDataResult: GetMateRequestsDataResult? = null
    var getMateRequestCountDataResult: GetMateRequestCountDataResult? = null

    private var mGetMateRequestsCallFlag = false
    val getMateRequestsCallFlag get() =  mGetMateRequestsCallFlag
    private var mCreateMateRequestCallFlag = false
    val createMateRequestCallFlag get() = mCreateMateRequestCallFlag
    private var mAnswerMateRequestCallFlag = false
    val answerMateRequestCallFlag get() = mAnswerMateRequestCallFlag
    private var mGetMateRequestCountCallFlag = false
    val getMateRequestCountCallFlag get() = getMateRequestsDataResult

    init {
        mateRequestDataRepositoryMock = mockMateRequestDataRepository()
    }

    fun clear() {
        getMateRequestsDataResult = null
        getMateRequestCountDataResult = null

        mGetMateRequestsCallFlag = false
        mCreateMateRequestCallFlag = false
        mAnswerMateRequestCallFlag = false
        mGetMateRequestCountCallFlag = false
    }

    private fun mockMateRequestDataRepository(): MateRequestDataRepository {
        val mateRequestDataRepositoryMock = Mockito.mock(MateRequestDataRepository::class.java)

        runTest {
            Mockito.`when`(mateRequestDataRepositoryMock.getMateRequests(
                Mockito.anyInt(), Mockito.anyInt()
            )).thenAnswer {
                mGetMateRequestsCallFlag = true
                MutableLiveData(getMateRequestsDataResult)
            }
            Mockito.`when`(mateRequestDataRepositoryMock.createMateRequest(
                Mockito.anyLong()
            )).thenAnswer {
                mCreateMateRequestCallFlag = true

                Unit
            }
            Mockito.`when`(mateRequestDataRepositoryMock.answerMateRequest(
                Mockito.anyLong(), Mockito.anyBoolean()
            )).thenAnswer {
                mAnswerMateRequestCallFlag = true

                Unit
            }
            Mockito.`when`(mateRequestDataRepositoryMock.getMateRequestCount()).thenAnswer {
                mGetMateRequestCountCallFlag = true
                getMateRequestCountDataResult
            }
        }

        return mateRequestDataRepositoryMock
    }
}