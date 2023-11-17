package com.qubacy.geoqq.domain.common.usecase.util.extension.user.result

import android.net.Uri
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetUsersAvatarUrisResult(
    val avatarUrisMap: Map<Long, Uri>
) : Result() {

}