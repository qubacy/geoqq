package com.qubacy.geoqq.ui.common.fragment.common.base.model.operation

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation

class ShowErrorUiOperation(
    val error: TypedErrorBase
) : UiOperation() {

}