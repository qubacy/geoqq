package com.qubacy.geoqq.applicaion.impl.container.geo.settings

import com.qubacy.geoqq.applicaion.common.container.geo.settings.GeoChatSettingsContainer
import com.qubacy.geoqq.domain.geochat.settings.GeoChatSettingsUseCase
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory

class GeoChatSettingsContainerImpl(
    val geoChatSettingsUseCase: GeoChatSettingsUseCase
) : GeoChatSettingsContainer() {
    override val geoChatSettingsViewModelFactory =
        GeoChatSettingsViewModelFactory(geoChatSettingsUseCase)
}