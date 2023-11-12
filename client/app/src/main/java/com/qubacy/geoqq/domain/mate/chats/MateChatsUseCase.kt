package com.qubacy.geoqq.domain.mate.chats

import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MateChatsUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val mateChatDataRepository: MateChatDataRepository,
    val imageDataRepository: ImageDataRepository,
    val userDataRepository: UserDataRepository
) : ConsumingUseCase<MateChatsState>(errorDataRepository, mateChatDataRepository) {
    override suspend fun processResult(result: Result) {
        // todo: implement a handler..


    }

    override fun generateState(operations: List<Operation>): MateChatsState {
        return MateChatsState(newOperations = operations)
    }



    fun getMateChats(count: Int) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

            val getTokensResultCast = getTokensResult as GetTokensResult

            mCurrentRepository = mateChatDataRepository
            mateChatDataRepository.getChats(getTokensResultCast.accessToken, count)
        }
    }
}