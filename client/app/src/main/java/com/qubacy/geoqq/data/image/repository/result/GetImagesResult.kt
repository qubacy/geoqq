package com.qubacy.geoqq.data.image.repository.result

import android.net.Uri
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetImagesResult(
    val imageIdToUriMap: Map<Long, Uri>,
    val isLocal: Boolean
) : Result() {

}