package com.qubacy.geoqq.data.mate.chat.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsWithDatabaseResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsWithNetworkResult
import com.qubacy.geoqq.data.mate.chat.repository.result.InsertOrUpdateChatsEntitiesWithDatabaseResult
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatWithLastMessageModel
import com.qubacy.geoqq.data.mate.chat.repository.source.network.NetworkMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.common.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.websocket.WebSocketUpdateMateChatDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.toDataMessage
import retrofit2.Call

class MateChatDataRepository(
    val localMateChatDataSource: LocalMateChatDataSource,
    val networkMateChatDataSource: NetworkMateChatDataSource,
    val localMateMessageDataSource: LocalMateMessageDataSource,
    webSocketUpdateMateChatDataSource: WebSocketUpdateMateChatDataSource
) : UpdatableDataRepository(webSocketUpdateMateChatDataSource) {
    private var mPrevChatCount: Int = 0

    private fun getChatsWithDatabase(count: Int): Result {
        var chats: List<MateChatWithLastMessageModel>? = null

        try {
            chats = localMateChatDataSource.getChats(count)

        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        val dataChats = chats.map {
            val lastMessage = it.lastMessage?.toDataMessage()

            DataMateChat(
                it.mateChatEntity.id,
                it.mateChatEntity.userId,
                it.mateChatEntity.newMessageCount,
                lastMessage
            )
        }

        return GetChatsWithDatabaseResult(dataChats)
    }

    private fun getChatsWithNetworkAndSave(offset: Int, count: Int, accessToken: String): Result {
        val networkCall = networkMateChatDataSource.getChats(offset, count, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetChatsResponse

        val chatsFromNetwork = responseBody.chats.map { it.toDataMateChat() }

        val insertChatsIntoDatabaseResult = insertOrUpdateChatsEntitiesWithDatabase(chatsFromNetwork)

        if (insertChatsIntoDatabaseResult is ErrorResult) return insertChatsIntoDatabaseResult
        if (insertChatsIntoDatabaseResult is InterruptionResult) return insertChatsIntoDatabaseResult

        val insertChatsIntoDatabaseResultCast =
            insertChatsIntoDatabaseResult as InsertOrUpdateChatsEntitiesWithDatabaseResult

        return GetChatsWithNetworkResult(
            responseBody.chats.map { it.toDataMateChat() },
            insertChatsIntoDatabaseResultCast.areUpdatedOrInserted
        )
    }

    private fun insertOrUpdateChatsEntitiesWithDatabase(chats: List<DataMateChat>): Result {
        var updatedOrInsertedChatsCount = 0

        try {
            for (chat in chats) {
                // todo: isn't it cursed?? try to find a better way

                var lastMessageUpdated = false

                var gottenLastMessageEntity: MateMessageEntity? = null
                var lastMessageEntity: MateMessageEntity? = null

                if (chat.lastMessage != null) {
                    lastMessageEntity = MateMessageEntity(
                        chat.lastMessage.id,
                        chat.id,
                        chat.lastMessage.userId,
                        chat.lastMessage.text,
                        chat.lastMessage.time / 1000
                    )

                    gottenLastMessageEntity = localMateMessageDataSource
                        .getMateMessage(chat.id, chat.lastMessage.id)

                    if (gottenLastMessageEntity != lastMessageEntity)
                        lastMessageUpdated = true
                }

                val chatEntity = MateChatEntity(
                    chat.id, chat.userId, chat.newMessageCount, chat.lastMessage?.id)

                val gottenChatEntity = localMateChatDataSource.getChatById(chat.id)

                if (gottenChatEntity != null && !lastMessageUpdated) {
                    if (chatEntity == gottenChatEntity.mateChatEntity) continue
                }

                if (gottenChatEntity == null)
                    localMateChatDataSource.insertChat(chatEntity)
                else
                    localMateChatDataSource.updateChat(chatEntity)

                if (lastMessageUpdated) {
                    if (gottenLastMessageEntity == null)
                        localMateMessageDataSource.insertMateMessage(lastMessageEntity!!)
                    else
                        localMateMessageDataSource.updateMateMessage(lastMessageEntity!!)
                }

                updatedOrInsertedChatsCount++
            }
        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return InsertOrUpdateChatsEntitiesWithDatabaseResult(
            updatedOrInsertedChatsCount > 0
        )
    }

    // todo: USE CASE class has to add new users BEFORE CALLING THIS!!!!!
    suspend fun getChats(accessToken: String, count: Int) {
        val getChatsWithDatabaseResult = getChatsWithDatabase(count)

        if (getChatsWithDatabaseResult is ErrorResult) return emitResult(getChatsWithDatabaseResult)
        if (getChatsWithDatabaseResult is InterruptionResult)
            return emitResult(getChatsWithDatabaseResult)

        val getChatsWithDatabaseResultCast = getChatsWithDatabaseResult as GetChatsWithDatabaseResult

        if (getChatsWithDatabaseResultCast.chats.isNotEmpty())
            emitResult(GetChatsResult(getChatsWithDatabaseResultCast.chats, true))

        val curNetworkRequestChatCount = count - mPrevChatCount
        val getChatsWithNetworkResult = getChatsWithNetworkAndSave(
            mPrevChatCount, curNetworkRequestChatCount, accessToken)

        if (getChatsWithNetworkResult is ErrorResult) return emitResult(getChatsWithDatabaseResult)
        if (getChatsWithNetworkResult is InterruptionResult)
            return emitResult(getChatsWithDatabaseResult)

        mPrevChatCount = count
        val getChatsWithNetworkResultCast = getChatsWithNetworkResult as GetChatsWithNetworkResult

        if (getChatsWithNetworkResultCast.areNew)
            emitResult(GetChatsResult(getChatsWithNetworkResultCast.chats, false))

        val startChatsUpdateListeningResult = initUpdateSource()

        if (startChatsUpdateListeningResult is ErrorResult)
            return emitResult(startChatsUpdateListeningResult)
    }

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}