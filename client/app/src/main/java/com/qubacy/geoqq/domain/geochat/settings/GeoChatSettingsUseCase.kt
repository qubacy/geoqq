package com.qubacy.geoqq.domain.geochat.settings

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.geochat.settings.state.GeoChatSettingsState

open class GeoChatSettingsUseCase(
    errorDataRepository: ErrorDataRepository
) : UseCase<GeoChatSettingsState>(errorDataRepository) {
    override fun generateState(
        operations: List<Operation>,
        prevState: GeoChatSettingsState?
    ): GeoChatSettingsState {
        return GeoChatSettingsState(operations)
    }
}