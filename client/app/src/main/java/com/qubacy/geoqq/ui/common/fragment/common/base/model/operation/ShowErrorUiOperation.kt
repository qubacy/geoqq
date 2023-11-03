package com.qubacy.geoqq.ui.common.fragment.common.base.model.operation

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation

class ShowErrorUiOperation(
    val error: Error
) : UiOperation() {

}