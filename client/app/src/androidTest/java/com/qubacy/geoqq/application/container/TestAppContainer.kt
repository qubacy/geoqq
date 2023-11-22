package com.qubacy.geoqq.application.container

import com.qubacy.geoqq.applicaion.common.container.AppContainer
import com.qubacy.geoqq.applicaion.common.container.mate.chat.MateChatContainer
import com.qubacy.geoqq.applicaion.common.container.mate.chats.MateChatsContainer
import com.qubacy.geoqq.applicaion.common.container.mate.requests.MateRequestsContainer
import com.qubacy.geoqq.applicaion.common.container.myprofile.MyProfileContainer
import com.qubacy.geoqq.applicaion.common.container.signin.SignInContainer
import com.qubacy.geoqq.applicaion.common.container.signup.SignUpContainer
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModelFactory
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModelFactory
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory
import org.mockito.Mockito

class TestAppContainer : AppContainer() {
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
        val signInViewModelMock = Mockito.mock(SignInViewModel::class.java)

        val signInViewModelFactoryMock = Mockito.mock(SignInViewModelFactory::class.java)

        Mockito.`when`(signInViewModelFactoryMock.create(signInViewModelMock::class.java))
            .thenAnswer { signInViewModelMock }

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
        val signUpViewModelMock = Mockito.mock(SignUpViewModel::class.java)

        val signUpViewModelFactoryMock = Mockito.mock(SignUpViewModelFactory::class.java)

        Mockito.`when`(signUpViewModelFactoryMock.create(signUpViewModelMock::class.java))
            .thenAnswer { signUpViewModelMock }

        mSignUpContainer = Mockito.mock(SignUpContainer::class.java)

        Mockito.`when`(mSignUpContainer!!.signUpViewModelFactory)
            .thenAnswer { signUpViewModelFactoryMock }
    }

    override val myProfileDataRepository: MyProfileDataRepository =
        Mockito.mock(MyProfileDataRepository::class.java)

    override fun initMyProfileContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        myProfileDataRepository: MyProfileDataRepository,
        imageDataRepository: ImageDataRepository
    ) {
        val myProfileViewModelMock = Mockito.mock(MyProfileViewModel::class.java)

        val myProfileViewModelFactoryMock = Mockito.mock(MyProfileViewModelFactory::class.java)

        Mockito.`when`(myProfileViewModelFactoryMock.create(myProfileViewModelMock::class.java))
            .thenAnswer { myProfileViewModelMock }

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
        val mateChatViewModelMock = Mockito.mock(MateChatViewModel::class.java)

        val mateChatViewModelFactoryMock = Mockito.mock(MateChatViewModelFactory::class.java)

        Mockito.`when`(mateChatViewModelFactoryMock.create(mateChatViewModelMock::class.java))
            .thenAnswer { mateChatViewModelMock }

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
        val mateRequestsViewModelMock = Mockito.mock(MateRequestsViewModel::class.java)

        val mateRequestsViewModelFactoryMock = Mockito.mock(MateRequestsViewModelFactory::class.java)

        Mockito.`when`(mateRequestsViewModelFactoryMock.create(mateRequestsViewModelMock::class.java))
            .thenAnswer { mateRequestsViewModelMock }

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
        val mateChatsViewModelMock = Mockito.mock(MateChatsViewModel::class.java)

        val mateChatsViewModelFactoryMock = Mockito.mock(MateChatsViewModelFactory::class.java)

        Mockito.`when`(mateChatsViewModelFactoryMock.create(mateChatsViewModelMock::class.java))
            .thenAnswer { mateChatsViewModelMock }

        mMateChatsContainer = Mockito.mock(MateChatsContainer::class.java)

        Mockito.`when`(mMateChatsContainer!!.mateChatsViewModelFactory)
            .thenAnswer { mateChatsViewModelFactoryMock }
    }
}