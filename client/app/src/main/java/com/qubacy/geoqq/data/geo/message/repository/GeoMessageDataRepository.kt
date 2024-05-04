package com.qubacy.geoqq.data.geo.message.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.geo.message.repository.result.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class GeoMessageDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mHttpGeoChatDataSource: HttpGeoChatDataSource,
    // todo: add a ws source..
) : ProducingDataRepository(coroutineDispatcher, coroutineScope), MessageDataRepository {
    suspend fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): LiveData<GetGeoMessagesDataResult> {
        val resultLiveData = MutableLiveData<GetGeoMessagesDataResult>()

        val getMessagesResponse = mHttpGeoChatDataSource.getMessages(radius, longitude, latitude)
        val resolveGetMessagesResultLiveData = resolveGetMessagesResponse(
            mUserDataRepository, getMessagesResponse)

        CoroutineScope(coroutineContext).launch {
            while (true) {
                val resolveGetMessagesResult = resolveGetMessagesResultLiveData.await()
                val messages = resolveGetMessagesResult.messages

                resultLiveData.postValue(GetGeoMessagesDataResult(
                    resolveGetMessagesResult.isNewest, messages))

                if (resolveGetMessagesResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    suspend fun sendMessage(
        text: String,
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        // todo: implement using the websocket data source;


        // todo: delete (for debug only!):
        mHttpGeoChatDataSource.sendMessage(text, radius, longitude, latitude)
    }
}