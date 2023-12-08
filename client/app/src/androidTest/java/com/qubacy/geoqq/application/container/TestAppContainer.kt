package com.qubacy.geoqq.application.container

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.qubacy.geoqq.applicaion.common.container.AppContainer
import com.qubacy.geoqq.applicaion.common.container.geo.chat.GeoChatContainer
import com.qubacy.geoqq.applicaion.common.container.geo.settings.GeoChatSettingsContainer
import com.qubacy.geoqq.applicaion.common.container.mate.chat.MateChatContainer
import com.qubacy.geoqq.applicaion.common.container.mate.chats.MateChatsContainer
import com.qubacy.geoqq.applicaion.common.container.mate.requests.MateRequestsContainer
import com.qubacy.geoqq.applicaion.common.container.myprofile.MyProfileContainer
import com.qubacy.geoqq.applicaion.common.container.signin.SignInContainer
import com.qubacy.geoqq.applicaion.common.container.signup.SignUpContainer
import com.qubacy.geoqq.common.util.AnyUtility
import com.qubacy.geoqq.data.common.repository.common.source.local.database.Database
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.geochat.chat.GeoChatUseCase
import com.qubacy.geoqq.domain.geochat.chat.state.GeoChatState
import com.qubacy.geoqq.domain.geochat.settings.GeoChatSettingsUseCase
import com.qubacy.geoqq.domain.geochat.settings.state.GeoChatSettingsState
import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.domain.geochat.signup.state.SignUpState
import com.qubacy.geoqq.domain.mate.chat.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModelFactory
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModelFactory
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModelFactory
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito

class TestAppContainer(context: Context) : AppContainer(context) {
    override val mDatabase = Room.databaseBuilder(
        context, Database::class.java, Database.DATABASE_NAME + "Test")
        .fallbackToDestructiveMigration()
        .createFromAsset(Database.DATABASE_NAME)
        .build()

    override val errorDataRepository: ErrorDataRepository =
        Mockito.mock(ErrorDataRepository::class.java)
    override val imageDataRepository: ImageDataRepository =
        Mockito.mock(ImageDataRepository::class.java)
    override val tokenDataRepository: TokenDataRepository =
        Mockito.mock(TokenDataRepository::class.java)
    override val signInDataRepository: SignInDataRepository =
        Mockito.mock(SignInDataRepository::class.java)

    override fun initSignInContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        signInDataRepository: SignInDataRepository
    ) {
        val signInUseCaseMock = Mockito.mock(SignInUseCase::class.java)

        Mockito.`when`(signInUseCaseMock.signInWithLocalToken()).thenAnswer {  }
        Mockito.`when`(signInUseCaseMock.signInWithLoginPassword(
            Mockito.anyString(), Mockito.anyString())).thenAnswer {  }
        Mockito.`when`(signInUseCaseMock.interruptOperation()).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(signInUseCaseMock, MutableStateFlow<SignInState?>(null))

        val signInViewModel = SignInViewModel(signInUseCaseMock)

        val signInViewModelFactoryMock = Mockito.mock(SignInViewModelFactory::class.java)

        Mockito.`when`(signInViewModelFactoryMock.create(SignInViewModel::class.java))
            .thenAnswer { signInViewModel }

        mSignInContainer = Mockito.mock(SignInContainer::class.java)

        Mockito.`when`(mSignInContainer!!.signInViewModelFactory)
            .thenAnswer { signInViewModelFactoryMock }
    }

    override val signUpDataRepository: SignUpDataRepository =
        Mockito.mock(SignUpDataRepository::class.java)

    override fun initSignUpContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        signUpDataRepository: SignUpDataRepository
    ) {
        val signUpUseCaseMock = Mockito.mock(SignUpUseCase::class.java)

        Mockito.`when`(signUpUseCaseMock.signUp(Mockito.anyString(), Mockito.anyString()))
            .thenAnswer {  }
        Mockito.`when`(signUpUseCaseMock.interruptOperation()).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(signUpUseCaseMock, MutableStateFlow<SignUpState?>(null))

        val signUpViewModel = SignUpViewModel(signUpUseCaseMock)

        val signUpViewModelFactoryMock = Mockito.mock(SignUpViewModelFactory::class.java)

        Mockito.`when`(signUpViewModelFactoryMock.create(SignUpViewModel::class.java))
            .thenAnswer { signUpViewModel }

        mSignUpContainer = Mockito.mock(SignUpContainer::class.java)

        Mockito.`when`(mSignUpContainer!!.signUpViewModelFactory)
            .thenAnswer { signUpViewModelFactoryMock }
    }

    override fun initGeoChatSettingsContainer(errorDataRepository: ErrorDataRepository) {
        val geoChatSettingsUseCaseMock = Mockito.mock(GeoChatSettingsUseCase::class.java)

        Mockito.`when`(geoChatSettingsUseCaseMock.getError(Mockito.anyLong())).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(
            geoChatSettingsUseCaseMock, MutableStateFlow<GeoChatSettingsState?>(null))

        val geoChatSettingsViewModel = GeoChatSettingsViewModel(geoChatSettingsUseCaseMock)

        val geoChatSettingsViewModelFactoryMock =
            Mockito.mock(GeoChatSettingsViewModelFactory::class.java)

        Mockito.`when`(geoChatSettingsViewModelFactoryMock.create(GeoChatSettingsViewModel::class.java))
            .thenAnswer { geoChatSettingsViewModel }

        mGeoChatSettingsContainer = Mockito.mock(GeoChatSettingsContainer::class.java)

        Mockito.`when`(mGeoChatSettingsContainer!!.geoChatSettingsViewModelFactory)
            .thenAnswer { geoChatSettingsViewModelFactoryMock }
    }

    override val geoMessageDataRepository: GeoMessageDataRepository =
        Mockito.mock(GeoMessageDataRepository::class.java)

    override fun initGeoChatContainer(
        radius: Int,
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        geoMessageDataRepository: GeoMessageDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    ) {
        val geoChatUseCaseMock = Mockito.mock(GeoChatUseCase::class.java)

        Mockito.`when`(geoChatUseCaseMock.getGeoChat(
            Mockito.anyInt(),
            Mockito.anyDouble(),
            Mockito.anyDouble())
        ).thenAnswer {  }
        Mockito.`when`(geoChatUseCaseMock.createMateRequest(Mockito.anyLong())).thenAnswer {  }
        Mockito.`when`(geoChatUseCaseMock.getUserDetails(Mockito.anyLong())).thenAnswer {  }
        Mockito.`when`(geoChatUseCaseMock.sendGeoMessage(
            Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString())
        ).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(geoChatUseCaseMock, MutableStateFlow<GeoChatState?>(null))

        val geoChatViewModel = GeoChatViewModel(0, geoChatUseCaseMock)

        val geoChatViewModelFactoryMock = Mockito.mock(GeoChatViewModelFactory::class.java)

        Mockito.`when`(geoChatViewModelFactoryMock.create(GeoChatViewModel::class.java))
            .thenAnswer { geoChatViewModel }

        mGeoChatContainer = Mockito.mock(GeoChatContainer::class.java)

        Mockito.`when`(mGeoChatContainer!!.geoChatViewModelFactory)
            .thenAnswer { geoChatViewModelFactoryMock }
    }

    override val myProfileDataRepository: MyProfileDataRepository =
        Mockito.mock(MyProfileDataRepository::class.java)

    override fun initMyProfileContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        myProfileDataRepository: MyProfileDataRepository,
        imageDataRepository: ImageDataRepository
    ) {
        val myProfileUseCaseMock = Mockito.mock(MyProfileUseCase::class.java)

        Mockito.`when`(myProfileUseCaseMock.getMyProfile()).thenAnswer {  }
        Mockito.`when`(myProfileUseCaseMock.updateMyProfile(
            AnyUtility.any(Uri::class.java),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            AnyUtility.any(DataMyProfile.HitUpOption::class.java))).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(myProfileUseCaseMock, MutableStateFlow<MyProfileState?>(null))

        val myProfileViewModel = MyProfileViewModel(myProfileUseCaseMock)

        val myProfileViewModelFactoryMock = Mockito.mock(MyProfileViewModelFactory::class.java)

        Mockito.`when`(myProfileViewModelFactoryMock.create(MyProfileViewModel::class.java))
            .thenAnswer { myProfileViewModel }

        mMyProfileContainer = Mockito.mock(MyProfileContainer::class.java)

        Mockito.`when`(mMyProfileContainer!!.myProfileViewModelFactory)
            .thenAnswer { myProfileViewModelFactoryMock }
    }

    override val userDataRepository: UserDataRepository =
        Mockito.mock(UserDataRepository::class.java)
    override val mateMessageDataRepository: MateMessageDataRepository =
        Mockito.mock(MateMessageDataRepository::class.java)

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
        val mateChatUseCaseMock = Mockito.mock(MateChatUseCase::class.java)

        Mockito.`when`(mateChatUseCaseMock.getChat(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt())).thenAnswer {  }
        Mockito.`when`(mateChatUseCaseMock.createMateRequest(Mockito.anyLong())).thenAnswer {  }
        Mockito.`when`(mateChatUseCaseMock.getInterlocutorUserDetails()).thenAnswer {  }
        Mockito.`when`(mateChatUseCaseMock.sendMessage(Mockito.anyLong(), Mockito.anyString()))
            .thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(mateChatUseCaseMock, MutableStateFlow<MateChatState?>(null))

        val mateChatViewModel = MateChatViewModel(0L, 1L, mateChatUseCaseMock)

        val mateChatViewModelFactoryMock = Mockito.mock(MateChatViewModelFactory::class.java)

        Mockito.`when`(mateChatViewModelFactoryMock.create(MateChatViewModel::class.java))
            .thenAnswer { mateChatViewModel }

        mMateChatContainer = Mockito.mock(MateChatContainer::class.java)

        Mockito.`when`(mMateChatContainer!!.mateChatViewModelFactory)
            .thenAnswer { mateChatViewModelFactoryMock }
    }

    override val mateRequestDataRepository: MateRequestDataRepository =
        Mockito.mock(MateRequestDataRepository::class.java)

    override fun initMateRequestsContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository
    ) {
        val mateRequestsUseCaseMock = Mockito.mock(MateRequestsUseCase::class.java)

        Mockito.`when`(mateRequestsUseCaseMock.getMateRequests(
            Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean())).thenAnswer {  }
        Mockito.`when`(mateRequestsUseCaseMock.answerMateRequest(
            Mockito.anyLong(), Mockito.anyBoolean())).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(mateRequestsUseCaseMock, MutableStateFlow<SignUpState?>(null))

        val mateRequestsViewModel = MateRequestsViewModel(mateRequestsUseCaseMock)

        val mateRequestsViewModelFactoryMock = Mockito.mock(MateRequestsViewModelFactory::class.java)

        Mockito.`when`(mateRequestsViewModelFactoryMock.create(MateRequestsViewModel::class.java))
            .thenAnswer { mateRequestsViewModel }

        mMateRequestsContainer = Mockito.mock(MateRequestsContainer::class.java)

        Mockito.`when`(mMateRequestsContainer!!.mateRequestsViewModelFactory)
            .thenAnswer { mateRequestsViewModelFactoryMock }
    }

    override val mateChatDataRepository: MateChatDataRepository =
        Mockito.mock(MateChatDataRepository::class.java)

    override fun initMateChatsContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateChatDataRepository: MateChatDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    ) {
        val mateChatsUseCaseMock = Mockito.mock(MateChatsUseCase::class.java)

        Mockito.`when`(mateChatsUseCaseMock.getMateChats(Mockito.anyInt())).thenAnswer {  }

        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("stateFlow")
            .apply { isAccessible = true }

        stateFlowFieldReflection.set(mateChatsUseCaseMock, MutableStateFlow<MateChatsState?>(null))

        val mateChatsViewModel = MateChatsViewModel(mateChatsUseCaseMock)

        val mateChatsViewModelFactoryMock = Mockito.mock(MateChatsViewModelFactory::class.java)

        Mockito.`when`(mateChatsViewModelFactoryMock.create(MateChatsViewModel::class.java))
            .thenAnswer { mateChatsViewModel }

        mMateChatsContainer = Mockito.mock(MateChatsContainer::class.java)

        Mockito.`when`(mMateChatsContainer!!.mateChatsViewModelFactory)
            .thenAnswer { mateChatsViewModelFactoryMock }
    }
}