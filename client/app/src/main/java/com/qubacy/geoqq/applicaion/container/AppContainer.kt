package com.qubacy.geoqq.applicaion.container

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.qubacy.geoqq.applicaion.container.mate.chat.MateChatContainer
import com.qubacy.geoqq.applicaion.container.mate.chats.MateChatsContainer
import com.qubacy.geoqq.applicaion.container.mate.requests.MateRequestsContainer
import com.qubacy.geoqq.applicaion.container.myprofile.MyProfileContainer
import com.qubacy.geoqq.applicaion.container.signin.SignInContainer
import com.qubacy.geoqq.applicaion.container.signup.SignUpContainer
import com.qubacy.geoqq.data.common.repository.common.source.local.database.Database
import com.qubacy.geoqq.data.common.repository.common.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
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
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase

class AppContainer(
    context: Context
) {
    // Common:

    private val database = Room.databaseBuilder(
        context, Database::class.java, Database.DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .createFromAsset(Database.DATABASE_NAME)
        .build()

    // Error:

    private val localErrorDataSource = database.getErrorDAO()

    val errorDataRepository = ErrorDataRepository(localErrorDataSource)

    // Image:

    private val localImageDataSource = LocalImageDataSource(context.contentResolver)
    private val networkImageDataSource = NetworkDataSourceContext.retrofit
        .create(NetworkImageDataSource::class.java)

    val imageDataRepository = ImageDataRepository(localImageDataSource, networkImageDataSource)

    // Token:

    private val localTokenDataSource = LocalTokenDataSource(
        context.getSharedPreferences(
            LocalTokenDataSource.TOKEN_SHARED_PREFERENCES_NAME, MODE_PRIVATE))
    private val networkTokenDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkTokenDataSource::class.java)

    val tokenDataRepository = TokenDataRepository(
        localTokenDataSource, networkTokenDataSource)

    // Sign In:

    private val networkSignInDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignInDataSource::class.java)

    val signInDataRepository = SignInDataRepository(networkSignInDataSource)

    var signInContainer: SignInContainer? = null

    fun initSignInContainer(
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

        signInContainer = SignInContainer(signInUseCase)
    }

    fun clearSignInContainer() { signUpContainer  = null }

    // Sign Up:

    private val networkSignUpDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkSignUpDataSource::class.java)

    val signUpDataRepository = SignUpDataRepository(networkSignUpDataSource)

    var signUpContainer: SignUpContainer? = null

    fun initSignUpContainer(
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

        signUpContainer = SignUpContainer(signUpUseCase)
    }

    fun clearSignUpContainer() {
        signUpContainer = null
    }

    // My Profile:

    private val networkMyProfileDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkMyProfileDataSource::class.java)
    private val localMyProfileDataSource =
        LocalMyProfileDataSource(context.getSharedPreferences(
            LocalMyProfileDataSource.MY_PROFILE_SHARED_PREFERENCES_NAME, MODE_PRIVATE))

    val myProfileDataRepository = MyProfileDataRepository(
        localMyProfileDataSource, networkMyProfileDataSource)

    var myProfileContainer: MyProfileContainer? = null

    fun initMyProfileContainer(
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

        myProfileContainer = MyProfileContainer(myProfileUseCase)
    }

    fun clearMyProfileContainer() {
        myProfileContainer = null
    }

    // User?:

    private val localUserDataSource = database.getUserDAO()
    private val networkUserDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkUserDataSource::class.java)

    val userDataRepository = UserDataRepository(localUserDataSource, networkUserDataSource)


    // Mate:

    private val localMateMessageDataSource = database.getMateMessageDAO()
    private val networkMateMessageDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkMateMessageDataSource::class.java)
    private val webSocketUpdateMateMessageDataSource = WebSocketUpdateMateMessageDataSource()

    val mateMessageDataRepository = MateMessageDataRepository(
        localMateMessageDataSource, networkMateMessageDataSource, webSocketUpdateMateMessageDataSource)

    var mateChatContainer: MateChatContainer? = null

    fun initMateChatContainer(
        chatId: Long,
        interlocutorUserId: Long,
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateMessageDataRepository: MateMessageDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
    ) {
        errorDataRepository.reset()
        tokenDataRepository.reset()
        mateMessageDataRepository.reset()
        imageDataRepository.reset()
        userDataRepository.reset()

        val mateChatUseCase = MateChatUseCase(
            errorDataRepository, tokenDataRepository,
            mateMessageDataRepository, imageDataRepository, userDataRepository
        )

        mateChatContainer = MateChatContainer(chatId, interlocutorUserId, mateChatUseCase)
    }

    fun clearMateChatContainer() {
        mateChatContainer = null
    }

    private val networkMateRequestDataSource = NetworkDataSourceContext.retrofit
        .create(NetworkMateRequestDataSource::class.java)
    private val webSocketUpdateMateRequestDataSource = WebSocketMateRequestDataSource()

    val mateRequestDataRepository = MateRequestDataRepository(
        networkMateRequestDataSource, webSocketUpdateMateRequestDataSource
    )

    var mateRequestsContainer: MateRequestsContainer? = null

    fun initMateRequestsContainer() {
        mateRequestsContainer = MateRequestsContainer()
    }

    fun clearMateRequestsContainer() {
        mateRequestsContainer = null
    }

    private val localMateChatDataSource = database.getMateChatDAO()
    private val networkMateChatDataSource =
        NetworkDataSourceContext.retrofit.create(NetworkMateChatDataSource::class.java)
    private val webSocketMateChatDataSource = WebSocketUpdateMateChatDataSource()

    val mateChatDataRepository = MateChatDataRepository(
        localMateChatDataSource, networkMateChatDataSource,
        localMateMessageDataSource, webSocketMateChatDataSource
    )

    var mateChatsContainer: MateChatsContainer? = null

    fun initMateChatsContainer(
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

        mateChatsContainer = MateChatsContainer(mateChatsUseCase)
    }

    fun clearMateChatsContainer() {
        mateChatsContainer = null
    }
}