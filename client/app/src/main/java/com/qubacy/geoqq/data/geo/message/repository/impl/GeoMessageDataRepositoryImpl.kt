package com.qubacy.geoqq.data.geo.message.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.result.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class GeoMessageDataRepositoryImpl(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mRemoteGeoMessageHttpRestDataSource: RemoteGeoMessageHttpRestDataSource,
    // todo: add a ws source..
) : GeoMessageDataRepository(coroutineDispatcher, coroutineScope) {
    override suspend fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): LiveData<GetGeoMessagesDataResult> {
        val resultLiveData = MutableLiveData<GetGeoMessagesDataResult>()

        val getMessagesResponse = mRemoteGeoMessageHttpRestDataSource.getMessages(radius, longitude, latitude)
        val resolveGetMessagesResultLiveData = resolveGetMessagesResponse(
            mUserDataRepository, getMessagesResponse)

        CoroutineScope(coroutineContext).launch {
            var version = 0

            while (true) {
                val resolveGetMessagesResult = resolveGetMessagesResultLiveData
                    .awaitUntilVersion(version)
                val messages = resolveGetMessagesResult.messages

                ++version

                resultLiveData.postValue(
                    GetGeoMessagesDataResult(
                    resolveGetMessagesResult.isNewest, messages)
                )

                if (resolveGetMessagesResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    override suspend fun sendMessage(
        text: String,
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        // todo: implement using the websocket data source;


        // todo: delete (for debug only!):
        mRemoteGeoMessageHttpRestDataSource.sendMessage(text, radius, longitude, latitude)
    }
}