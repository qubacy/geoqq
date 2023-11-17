package com.qubacy.geoqq.data.image.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetImagesIdsByUrisResult(
    val imagesIds: List<Long>
) : Result() {

}