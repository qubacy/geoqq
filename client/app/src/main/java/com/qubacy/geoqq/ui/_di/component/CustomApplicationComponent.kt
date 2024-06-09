package com.qubacy.geoqq.ui._di.component

import android.content.ContentResolver
import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common._di.module.DatabaseModule
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao._di.module.LocalErrorDataSourceDaoModule
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._di.module.LocalErrorDataSourceModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client._di.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor._di.module.HttpCallExecutorModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.json.factory.moshi._di.module.MoshiModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._di.module.AuthorizationRequestMiddlewareModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter._di.module.ErrorResponseContentJsonAdapterModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter._di.module.ErrorResponseJsonAdapterModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api._di.module.HttpRestApiModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.retrofit._di.module.RetrofitModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._di.module.AuthorizationHttpRestInterceptorModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._di.module.ActionJsonAdapterModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter._di.module.ErrorEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.success.json.adapter._di.module.SuccessEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._di.module.WebSocketAdapterModule
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._di.module.LocalTokenDataStoreDataSourceModule
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api._di.module.RemoteTokenHttpRestDataSourceApiModule
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._di.module.RemoteTokenHttpRestDataSourceModule
import com.qubacy.geoqq.data._common.repository.token.repository._di.module.TokenDataRepositoryModule
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._di.module.LocalAuthDatabaseDataSourceModule
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api._di.module.RemoteAuthHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._di.module.RemoteAuthHttpRestDataSourceModule
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._di.module.RemoteAuthHttpWebSocketDataSourceModule
import com.qubacy.geoqq.data.auth.repository._di.module.AuthDataRepositoryModule
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api._di.module.RemoteGeoMessageHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._di.module.RemoteGeoMessageHttpRestDataSourceModule
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.location.json.adapter._di.module.GeoLocationActionPayloadJsonAdapterModule
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.message.json.adapter._di.module.GeoMessageActionPayloadJsonAdapterModule
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.json.adapter._di.module.GeoMessageAddedEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._di.module.RemoteGeoMessageHttpWebSocketDataSourceModule
import com.qubacy.geoqq.data.geo.message.repository._di.module.GeoMessageDataRepositoryModule
import com.qubacy.geoqq.data.image.repository._common.source.local.content._di.module.LocalImageContentStoreDataSourceModule
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api._di.module.RemoteImageHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._di.module.RemoteImageHttpRestDataSourceModule
import com.qubacy.geoqq.data.image.repository._di.module.ImageDataRepositoryModule
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao._di.module.LocalMateChatDatabaseDataSourceDaoModule
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._di.module.LocalMateChatDatabaseDataSourceModule
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api._di.module.RemoteMateChatHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._di.module.RemoteMateChatHttpRestDataSourceModule
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.json.adapter._di.module.MateChatEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._di.module.RemoteMateChatHttpWebSocketDataSourceModule
import com.qubacy.geoqq.data.mate.chat.repository._di.module.MateChatDataRepositoryModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao._di.module.LocalMateMessageDatabaseDataSourceDaoModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._di.module.LocalMateMessageDatabaseDataSourceModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api._di.module.RemoteMateMessageHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._di.module.RemoteMateMessageHttpRestDataSourceModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add.json.adapter.AddMateMessageActionPayloadJsonAdapterModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.json.adapter._di.module.MateMessageAddedEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._di.module.RemoteMateMessageHttpWebSocketDataSourceModule
import com.qubacy.geoqq.data.mate.message.repository._di.module.MateMessageDataRepositoryModule
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api._di.module.RemoteMateRequestHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._di.module.RemoteMateRequestHttpRestDataSourceModule
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.json.adapter._di.module.MateRequestAddedEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._di.module.RemoteMateRequestHttpWebSocketDataSourceModule
import com.qubacy.geoqq.data.mate.request.repository._di.module.MateRequestDataRepositoryModule
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._di.module.LocalMyProfileDataStoreDataSourceModule
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api._di.module.RemoteMyProfileHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._di.module.RemoteMyProfileHttpRestDataSourceModule
import com.qubacy.geoqq.data.myprofile.repository._di.module.MyProfileDataRepositoryModule
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao._di.module.LocalUserDatabaseDataSourceDaoModule
import com.qubacy.geoqq.data.user.repository._common.source.local.database._di.module.LocalUserDatabaseDataSourceModule
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api._di.module.RemoteUserHttpRestDataSourceApiModule
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._di.module.RemoteUserHttpRestDataSourceModule
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.payload.updated.json.adapter._di.module.UserUpdatedEventPayloadJsonAdapterModule
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._di.module.RemoteUserHttpWebSocketDataSourceModule
import com.qubacy.geoqq.data.user.repository._di.module.UserDataRepositoryModule
import com.qubacy.geoqq.domain.geo.chat.usecase._di.module.GeoChatUseCaseModule
import com.qubacy.geoqq.domain.login.usecase._di.module.LoginUseCaseModule
import com.qubacy.geoqq.domain.logout.usecase._di.module.LogoutUseCaseModule
import com.qubacy.geoqq.domain.mate.chat.usecase._di.module.MateChatUseCaseModule
import com.qubacy.geoqq.domain.mate.chats.usecase._di.module.MateChatsUseCaseModule
import com.qubacy.geoqq.domain.mate.request.usecase._di.module.MateRequestUseCaseModule
import com.qubacy.geoqq.domain.mate.requests.usecase._di.module.MateRequestsUseCaseModule
import com.qubacy.geoqq.domain.myprofile.usecase._di.module.MyProfileUseCaseModule
import com.qubacy.geoqq.domain.user.usecase._di.module.UserUseCaseModule
import com.qubacy.geoqq.ui._di.module.CustomApplicationSubcomponentsModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.GeoChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._di.module.GeoChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.GeoSettingsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._di.module.GeoSettingsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.login.LoginFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._di.module.LoginViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.MateChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._di.module.MateChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._di.module.MateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.MateRequestsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._di.module.MateRequestsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.MyProfileFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._di.module.MyProfileViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [

    ],
    modules = [
        CustomApplicationSubcomponentsModule::class,

        WebSocketAdapterModule::class,

        LoginViewModelModule::class,
        GeoSettingsViewModelModule::class,
        GeoChatViewModelModule::class,
        MateChatsViewModelModule::class,
        MateChatViewModelModule::class,
        MateRequestsViewModelModule::class,
        MyProfileViewModelModule::class,

        UserUseCaseModule::class,
        LoginUseCaseModule::class,
        LogoutUseCaseModule::class,
        GeoChatUseCaseModule::class,
        MateChatUseCaseModule::class,
        MateChatsUseCaseModule::class,
        MateRequestUseCaseModule::class,
        MateRequestsUseCaseModule::class,
        MyProfileUseCaseModule::class,

        TokenDataRepositoryModule::class,
        AuthDataRepositoryModule::class,
        ImageDataRepositoryModule::class,
        UserDataRepositoryModule::class,
        MyProfileDataRepositoryModule::class,
        MateRequestDataRepositoryModule::class,
        MateChatDataRepositoryModule::class,
        MateMessageDataRepositoryModule::class,
        GeoMessageDataRepositoryModule::class,

        LocalErrorDataSourceDaoModule::class,
        LocalErrorDataSourceModule::class,
        LocalTokenDataStoreDataSourceModule::class,
        LocalAuthDatabaseDataSourceModule::class,
        LocalUserDatabaseDataSourceDaoModule::class,
        LocalUserDatabaseDataSourceModule::class,
        LocalImageContentStoreDataSourceModule::class,
        LocalMateChatDatabaseDataSourceDaoModule::class,
        LocalMateChatDatabaseDataSourceModule::class,
        LocalMateMessageDatabaseDataSourceDaoModule::class,
        LocalMateMessageDatabaseDataSourceModule::class,
        LocalMyProfileDataStoreDataSourceModule::class,

        RemoteGeoMessageHttpRestDataSourceApiModule::class,
        RemoteGeoMessageHttpRestDataSourceModule::class,
        RemoteGeoMessageHttpWebSocketDataSourceModule::class,
        RemoteTokenHttpRestDataSourceApiModule::class,
        RemoteTokenHttpRestDataSourceModule::class,
        RemoteAuthHttpRestDataSourceApiModule::class,
        RemoteAuthHttpRestDataSourceModule::class,
        RemoteAuthHttpWebSocketDataSourceModule::class,
        RemoteUserHttpRestDataSourceApiModule::class,
        RemoteUserHttpRestDataSourceModule::class,
        RemoteUserHttpWebSocketDataSourceModule::class,
        RemoteImageHttpRestDataSourceApiModule::class,
        RemoteImageHttpRestDataSourceModule::class,
        RemoteMateChatHttpRestDataSourceApiModule::class,
        RemoteMateChatHttpRestDataSourceModule::class,
        RemoteMateChatHttpWebSocketDataSourceModule::class,
        RemoteMateMessageHttpRestDataSourceApiModule::class,
        RemoteMateMessageHttpRestDataSourceModule::class,
        RemoteMateMessageHttpWebSocketDataSourceModule::class,
        RemoteMateRequestHttpRestDataSourceApiModule::class,
        RemoteMateRequestHttpRestDataSourceModule::class,
        RemoteMateRequestHttpWebSocketDataSourceModule::class,
        RemoteMyProfileHttpRestDataSourceApiModule::class,
        RemoteMyProfileHttpRestDataSourceModule::class,

        HttpCallExecutorModule::class,
        MoshiModule::class,
        HttpClientModule::class,
        HttpRestApiModule::class,
        AuthorizationRequestMiddlewareModule::class,
        AuthorizationHttpRestInterceptorModule::class,

        ErrorResponseJsonAdapterModule::class,
        ErrorResponseContentJsonAdapterModule::class,
        ErrorEventPayloadJsonAdapterModule::class,
        SuccessEventPayloadJsonAdapterModule::class,

        UserUpdatedEventPayloadJsonAdapterModule::class,
        MateRequestAddedEventPayloadJsonAdapterModule::class,
        GeoMessageAddedEventPayloadJsonAdapterModule::class,
        MateChatEventPayloadJsonAdapterModule::class,
        MateMessageAddedEventPayloadJsonAdapterModule::class,
        ActionJsonAdapterModule::class,

        GeoLocationActionPayloadJsonAdapterModule::class,
        GeoMessageActionPayloadJsonAdapterModule::class,
        AddMateMessageActionPayloadJsonAdapterModule::class,

        DatabaseModule::class,
        RetrofitModule::class
    ]
)
interface CustomApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance contentResolver: ContentResolver = context.contentResolver
        ): CustomApplicationComponent
    }

    fun inject(fragment: LoginFragment)
    fun inject(fragment: GeoChatFragment)
    fun inject(fragment: GeoSettingsFragment)
    fun inject(fragment: MateChatsFragment)
    fun inject(fragment: MateChatFragment)
    fun inject(fragment: MateRequestsFragment)
    fun inject(fragment: MyProfileFragment)
}