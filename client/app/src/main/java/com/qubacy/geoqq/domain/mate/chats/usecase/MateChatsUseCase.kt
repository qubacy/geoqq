package com.qubacy.geoqq.domain.mate.chats.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.result.GetChatsDomainResult

class MateChatsUseCase(
    errorDataRepository: ErrorDataRepository,
    private val mMateChatDataRepository: MateChatDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    fun getChats(offset: Int, count: Int) {
        executeLogic({

        }) {
            GetChatsDomainResult(error = it)
        }
    }
}