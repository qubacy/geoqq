package com.qubacy.geoqq.data.mate.request.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsDataResult
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MateRequestDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mHttpMateRequestDataSource: HttpMateRequestDataSource
    // todo: add a websocket source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "MateRequestDtRepstry"
    }

    suspend fun getMateRequests(
        offset: Int,
        count: Int
    ): LiveData<GetMateRequestsDataResult> {
        val resultLiveData = MutableLiveData<GetMateRequestsDataResult>()

        CoroutineScope(coroutineContext).launch {
            val getMateRequestsResponse = mHttpMateRequestDataSource.getMateRequests(offset, count)

            val resolveMateRequestsLiveData = resolveGetMateRequestsResponse(getMateRequestsResponse)

            var version = 0

            while (true) {
                val resolveMateRequestsResult = resolveMateRequestsLiveData.awaitUntilVersion(version)

                ++version

                resultLiveData.postValue(resolveMateRequestsResult)

                if (resolveMateRequestsResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    fun getMateRequestCount(): GetMateRequestCountDataResult {
        val getMateRequestCountResponse = mHttpMateRequestDataSource.getMateRequestCount()

        return GetMateRequestCountDataResult(getMateRequestCountResponse.count)
    }

    fun createMateRequest(userId: Long) {
        mHttpMateRequestDataSource.postMateRequest(userId)
    }

    fun answerMateRequest(id: Long, isAccepted: Boolean) {
        mHttpMateRequestDataSource.answerMateRequest(id, isAccepted)
    }

    private suspend fun resolveGetMateRequestsResponse(
        getMateRequestsResponse: GetMateRequestsResponse
    ) : LiveData<GetMateRequestsDataResult> {
        val resultLiveData = MutableLiveData<GetMateRequestsDataResult>()

        val userIds = getMateRequestsResponse.requests.map { it.userId }.toSet().toList()
        val resolveUsersResultLiveData = mUserDataRepository.resolveUsers(userIds)

        CoroutineScope(coroutineContext).launch {
            var version = 0

            while (true) {
                val resolveUsersResult = resolveUsersResultLiveData.awaitUntilVersion(version)
                val userIdUserMap = resolveUsersResult.userIdUserMap

                ++version

                val dataMateRequests = getMateRequestsResponse.requests.map {
                    it.toDataMateRequest(userIdUserMap[it.userId]!!)
                }

                resultLiveData.postValue(GetMateRequestsDataResult(
                    resolveUsersResult.isNewest, dataMateRequests))

                if (resolveUsersResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }
}