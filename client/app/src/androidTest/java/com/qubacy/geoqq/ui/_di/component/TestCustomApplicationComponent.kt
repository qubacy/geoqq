package com.qubacy.geoqq.ui._di.component

import com.qubacy.geoqq.ui._di.module.CustomApplicationSubcomponentsModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.module.FakeGeoChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.module.FakeGeoSettingsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module.FakeLoginViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module.FakeMateChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.FakeMateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module.FakeMateRequestsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.module.FakeMyProfileViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    CustomApplicationSubcomponentsModule::class,

    FakeGeoChatViewModelModule::class,
    FakeGeoSettingsViewModelModule::class,
    FakeLoginViewModelModule::class,
    FakeMateChatsViewModelModule::class,
    FakeMateChatViewModelModule::class,
    FakeMateRequestsViewModelModule::class,
    FakeMyProfileViewModelModule::class
])
interface TestCustomApplicationComponent : CustomApplicationComponent {

}