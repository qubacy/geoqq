package com.qubacy.geoqq.ui.application.dependency

import android.content.Context
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.GeoChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.GeoSettingsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.login.LoginFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.MateChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.MateRequestsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.MyProfileFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface CustomApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CustomApplicationComponent
    }

    fun inject(fragment: LoginFragment)
    fun inject(fragment: GeoChatFragment)
    fun inject(fragment: GeoSettingsFragment)
    fun inject(fragment: MateChatsFragment)
    fun inject(fragment: MateChatFragment)
    fun inject(fragment: MateRequestsFragment)
    fun inject(fragment: MyProfileFragment)
}