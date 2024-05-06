package com.qubacy.geoqq.data.mate.request.repository._test.mock

import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsDataResult
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class MateRequestDataRepositoryMockContainer {
    companion object {
        val DEFAULT_DATA_REQUEST = DataMateRequest(
            0, UserDataRepositoryMockContainer.DEFAULT_DATA_USER)
    }

    val mateRequestDataRepositoryMock: MateRequestDataRepository

    var getMateRequestsDataResults: List<GetMateRequestsDataResult>? = null
    var getMateRequestCountDataResult: GetMateRequestCountDataResult? = null

    private var mGetMateRequestsCallFlag = false
    val getMateRequestsCallFlag get() =  mGetMateRequestsCallFlag
    private var mCreateMateRequestCallFlag = false
    val createMateRequestCallFlag get() = mCreateMateRequestCallFlag
    private var mAnswerMateRequestCallFlag = false
    val answerMateRequestCallFlag get() = mAnswerMateRequestCallFlag
    private var mGetMateRequestCountCallFlag = false
    val getMateRequestCountCallFlag get() = getMateRequestsDataResults

    init {
        mateRequestDataRepositoryMock = mockMateRequestDataRepository()
    }

    fun clear() {
        getMateRequestsDataResults = null
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

                val resultLiveData = MutableLiveData<GetMateRequestsDataResult>()

                CoroutineScope(coroutineContext).launch {
                    for (result in getMateRequestsDataResults!!)
                        resultLiveData.postValue(result)
                }

                resultLiveData
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