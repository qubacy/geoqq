package com.qubacy.geoqq.applicaion.common.container.geo.settings

import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory

abstract class GeoChatSettingsContainer() {
    abstract val geoChatSettingsViewModelFactory: GeoChatSettingsViewModelFactory
}