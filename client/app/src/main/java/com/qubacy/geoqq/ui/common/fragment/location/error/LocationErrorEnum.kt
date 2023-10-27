package com.qubacy.geoqq.ui.common.fragment.location.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class LocationErrorEnum(val error: LocalError) {
    LOCATION_PERMISSIONS_DENIED(
        LocalError(R.string.error_location_lacking_location_permissions, ErrorBase.Level.CRITICAL)),
    LOCATION_SERVICES_NOT_ENABLED(
        LocalError(R.string.error_location_lacking_location_permissions, ErrorBase.Level.CRITICAL)),
    GMS_API_NOT_AVAILABLE(
        LocalError(R.string.error_gms_api_not_available, ErrorBase.Level.CRITICAL)),
    ;
}