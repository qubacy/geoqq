package com.qubacy.geoqq.applicaion.common.container

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.qubacy.geoqq.applicaion.common.container.mate.chat.MateChatContainerImpl
import com.qubacy.geoqq.applicaion.common.container.mate.chats.MateChatsContainerImpl
import com.qubacy.geoqq.applicaion.common.container.mate.requests.MateRequestsContainerImpl
import com.qubacy.geoqq.applicaion.common.container.myprofile.MyProfileContainerImpl
import com.qubacy.geoqq.applicaion.common.container.signin.SignInContainerImpl
import com.qubacy.geoqq.applicaion.common.container.signup.SignUpContainerImpl
import com.qubacy.geoqq.applicaion.impl.container.geo.chat.GeoChatContainerImpl
import com.qubacy.geoqq.applicaion.impl.container.geo.settings.GeoChatSettingsContainerImpl
import com.qubacy.geoqq.data.common.repository.common.source.local.database.Database
import com.qubacy.geoqq.data.common.repository.common.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.source.network.model.NetworkGeoMessageDataSource
import com.qubacy.geoqq.data.geochat.message.repository.source.websocket.WebSocketUpdateGeoMessageDataSource
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.network.NetworkImageDataSource
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.source.network.NetworkMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.websocket.WebSocketUpdateMateChatDataSource
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.source.network.NetworkMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.websocket.WebSocketUpdateMateMessageDataSource
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.source.network.NetworkMateRequestDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.websocket.WebSocketMateRequestDataSource
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.network.NetworkMyProfileDataSource
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.domain.geochat.chat.GeoChatUseCase
import com.qubacy.geoqq.domain.geochat.settings.GeoChatSettingsUseCase
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase

class AppContainerImpl(
    context: Context
) : AppContainer(context) {
    // Common:

    override val mDatabase = Room.databaseBuilder(
        context, Database::class.java, Database.DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .createFromAsset(Database.DATABASE_NAME)
        .build()

    // Error:

    private val localErrorDataSource = mDatabase.getErrorDAO()

    override val errorDataRepository = ErrorDataRepository(localErrorDataSource)

    // Image:

    private val localImageDataSource = LocalImageDataSource(context.contentResolver)
    private val networkImageDataSource = NetworkDataSourceContext.retrofit
        .create(NetworkImageDataSource::class.java)

    override val imageDataRepository = ImageDataRepository(
        localImageDataSource, networkImageDataSource)

    // Token:

    private val localTokenDataSource = LocalTokenDataSource(
        context.getSharedPreferences(
            LocalTokenDataSource.TOKEN_SHARED_PREFERENCES_NAME, MODE_PRIVATE))
    private val networkTokenDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkTokenDataSource::class.java)

    override val tokenDataRepository = TokenDataRepository(
        localTokenDataSource, networkTokenDataSource)

    // Sign In:

    private val networkSignInDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignInDataSource::class.java)

    override val signInDataRepository = SignInDataRepository(networkSignInDataSource)

    override fun initSignInContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        signInDataRepository: SignInDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        signInDataRepository.reset()

        val signInUseCase = SignInUseCase(
            errorDataRepository, tokenDataRepository, signInDataRepository
        )

        mSignInContainer = SignInContainerImpl(signInUseCase)
    }

    override fun clearSignInContainer() { mSignInContainer = null }

    // Sign Up:

    private val networkSignUpDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignUpDataSource::class.java)

    override val signUpDataRepository = SignUpDataRepository(networkSignUpDataSource)

    override fun initSignUpContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        signUpDataRepository: SignUpDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        signUpDataRepository.reset()

        val signUpUseCase = SignUpUseCase(
            errorDataRepository, tokenDataRepository, signUpDataRepository
        )

        mSignUpContainer = SignUpContainerImpl(signUpUseCase)
    }

    override fun clearSignUpContainer() { mSignUpContainer = null }

    // Geo Chat Settings

    override fun initGeoChatSettingsContainer(errorDataRepository: ErrorDataRepository) {
        errorDataRepository.reset()

        val geoChatSettingsUseCase = GeoChatSettingsUseCase(errorDataRepository)

        mGeoChatSettingsContainer = GeoChatSettingsContainerImpl(geoChatSettingsUseCase)
    }

    // Geo Chat

    private val networkGeoMessageDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkGeoMessageDataSource::class.java)
    private val webSocketUpdateGeoMessageDataSource = WebSocketUpdateGeoMessageDataSource()

    override val geoMessageDataRepository = GeoMessageDataRepository(
        networkGeoMessageDataSource, webSocketUpdateGeoMessageDataSource)

    override fun initGeoChatContainer(
        radius: Int,
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        geoMessageDataRepository: GeoMessageDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        geoMessageDataRepository.reset()
        imageDataRepository.reset()
        userDataRepository.reset()
        mateRequestDataRepository.reset()

        val geoChatUseCase = GeoChatUseCase(
            errorDataRepository,
            tokenDataRepository,
            geoMessageDataRepository,
            imageDataRepository,
            userDataRepository,
            mateRequestDataRepository
        )

        mGeoChatContainer = GeoChatContainerImpl(radius, geoChatUseCase)
    }

    override fun clearGeoChatContainer() {
        mGeoChatContainer = null
    }

    // My Profile:

    private val networkMyProfileDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkMyProfileDataSource::class.java)
    private val localMyProfileDataSource =
        LocalMyProfileDataSource(context.getSharedPreferences(
            LocalMyProfileDataSource.MY_PROFILE_SHARED_PREFERENCES_NAME, MODE_PRIVATE))

    override val myProfileDataRepository = MyProfileDataRepository(
        localMyProfileDataSource, networkMyProfileDataSource)

    override fun initMyProfileContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        myProfileDataRepository: MyProfileDataRepository,
        imageDataRepository: ImageDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        myProfileDataRepository.reset()
        imageDataRepository.reset()

        val myProfileUseCase = MyProfileUseCase(
            errorDataRepository, tokenDataRepository,
            myProfileDataRepository, imageDataRepository
        )

        mMyProfileContainer = MyProfileContainerImpl(myProfileUseCase)
    }

    override fun clearMyProfileContainer() { mMyProfileContainer = null }

    // User?:

    private val localUserDataSource = mDatabase.getUserDAO()
    private val networkUserDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkUserDataSource::class.java)

    override val userDataRepository = UserDataRepository(localUserDataSource, networkUserDataSource)


    // Mate:

    private val localMateMessageDataSource = mDatabase.getMateMessageDAO()
    private val networkMateMessageDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkMateMessageDataSource::class.java)
    private val webSocketUpdateMateMessageDataSource = WebSocketUpdateMateMessageDataSource()

    override val mateMessageDataRepository = MateMessageDataRepository(
        localMateMessageDataSource, networkMateMessageDataSource, webSocketUpdateMateMessageDataSource)

    override fun initMateChatContainer(
        chatId: Long,
        interlocutorUserId: Long,
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateMessageDataRepository: MateMessageDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        mateMessageDataRepository.reset()
        imageDataRepository.reset()
        userDataRepository.reset()
        mateRequestDataRepository.reset()

        val mateChatUseCase = MateChatUseCase(
            errorDataRepository, tokenDataRepository,
            mateMessageDataRepository, imageDataRepository,
            userDataRepository, mateRequestDataRepository
        )

        mMateChatContainer = MateChatContainerImpl(chatId, interlocutorUserId, mateChatUseCase)
    }

    override fun clearMateChatContainer() { mMateChatContainer = null }

    private val networkMateRequestDataSource = NetworkDataSourceContext.retrofit
        .create(NetworkMateRequestDataSource::class.java)
    private val webSocketUpdateMateRequestDataSource = WebSocketMateRequestDataSource()

    override val mateRequestDataRepository = MateRequestDataRepository(
        networkMateRequestDataSource, webSocketUpdateMateRequestDataSource
    )

    override fun initMateRequestsContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        mateRequestDataRepository.reset()
        imageDataRepository.reset()
        userDataRepository.reset()

        val mateRequestsUseCase = MateRequestsUseCase(
            errorDataRepository, tokenDataRepository,
            mateRequestDataRepository, userDataRepository,
            imageDataRepository
        )

        mMateRequestsContainer = MateRequestsContainerImpl(mateRequestsUseCase)
    }

    override fun clearMateRequestsContainer() { mMateRequestsContainer = null }

    private val localMateChatDataSource = mDatabase.getMateChatDAO()
    private val networkMateChatDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkMateChatDataSource::class.java)
    private val webSocketMateChatDataSource = WebSocketUpdateMateChatDataSource()

    override val mateChatDataRepository = MateChatDataRepository(
        localMateChatDataSource, networkMateChatDataSource,
        localMateMessageDataSource, webSocketMateChatDataSource
    )

    override fun initMateChatsContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateChatDataRepository: MateChatDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        mateChatDataRepository.reset()
        imageDataRepository.reset()
        userDataRepository.reset()
        mateRequestDataRepository.reset()

        val mateChatsUseCase = MateChatsUseCase(
            errorDataRepository, tokenDataRepository,
            mateChatDataRepository, imageDataRepository,
            userDataRepository, mateRequestDataRepository
        )

        mMateChatsContainer = MateChatsContainerImpl(mateChatsUseCase)
    }

    override fun clearMateChatsContainer() { mMateChatsContainer = null }
}