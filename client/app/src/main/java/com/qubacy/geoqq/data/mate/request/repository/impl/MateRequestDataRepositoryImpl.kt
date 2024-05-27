package com.qubacy.geoqq.data.mate.request.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common.result.GetMateRequestCountDataResult
import com.qubacy.geoqq.data.mate.request.repository._common.result.GetMateRequestsDataResult
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MateRequestDataRepositoryImpl @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mRemoteMateRequestHttpRestDataSource: RemoteMateRequestHttpRestDataSource
    // todo: add a websocket source;
) : MateRequestDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "MateRequestDtRepstry"
    }

    override suspend fun getMateRequests(
        offset: Int,
        count: Int
    ): LiveData<GetMateRequestsDataResult> {
        val resultLiveData = MutableLiveData<GetMateRequestsDataResult>()

        CoroutineScope(coroutineContext).launch {
            val getMateRequestsResponse = mRemoteMateRequestHttpRestDataSource.getMateRequests(offset, count)

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

    override fun getMateRequestCount(): GetMateRequestCountDataResult {
        val getMateRequestCountResponse = mRemoteMateRequestHttpRestDataSource.getMateRequestCount()

        return GetMateRequestCountDataResult(getMateRequestCountResponse.count)
    }

    override fun createMateRequest(userId: Long) {
        mRemoteMateRequestHttpRestDataSource.postMateRequest(userId)
    }

    override fun answerMateRequest(id: Long, isAccepted: Boolean) {
        mRemoteMateRequestHttpRestDataSource.answerMateRequest(id, isAccepted)
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

                resultLiveData.postValue(
                    GetMateRequestsDataResult(
                    resolveUsersResult.isNewest, dataMateRequests)
                )

                if (resolveUsersResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }
}