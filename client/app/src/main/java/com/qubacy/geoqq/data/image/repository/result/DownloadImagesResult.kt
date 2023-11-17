package com.qubacy.geoqq.data.image.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.image.repository.source.network.model.response.NetworkImageModel

class DownloadImagesResult(
    val images: List<NetworkImageModel>
) : Result() {

}