package com.qubacy.geoqq.applicaion.common.container

import android.content.Context
import androidx.room.RoomDatabase
import com.qubacy.geoqq.applicaion.common.container.geochat.GeoChatContainer
import com.qubacy.geoqq.applicaion.common.container.mate.chat.MateChatContainer
import com.qubacy.geoqq.applicaion.common.container.mate.chats.MateChatsContainer
import com.qubacy.geoqq.applicaion.common.container.mate.requests.MateRequestsContainer
import com.qubacy.geoqq.applicaion.common.container.myprofile.MyProfileContainer
import com.qubacy.geoqq.applicaion.common.container.signin.SignInContainer
import com.qubacy.geoqq.applicaion.common.container.signup.SignUpContainer
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository

abstract class AppContainer(val context: Context) {
    protected abstract val mDatabase: RoomDatabase

    // Error:

    abstract val errorDataRepository: ErrorDataRepository

    // Image:

    abstract val imageDataRepository: ImageDataRepository

    // Token:

    abstract val tokenDataRepository: TokenDataRepository

    // Sign In:

    abstract val signInDataRepository: SignInDataRepository

    protected var mSignInContainer: SignInContainer? = null
    val signInContainer get() = mSignInContainer

    abstract fun initSignInContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        signInDataRepository: SignInDataRepository
    )

    open fun clearSignInContainer() { mSignInContainer  = null }

    // Sign Up:

    abstract val signUpDataRepository: SignUpDataRepository

    var mSignUpContainer: SignUpContainer? = null
    val signUpContainer get() = mSignUpContainer

    abstract fun initSignUpContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        signUpDataRepository: SignUpDataRepository
    )

    open fun clearSignUpContainer() { mSignUpContainer = null }

    // Geo Chat:

    abstract val geoMessageDataRepository: GeoMessageDataRepository

    protected var mGeoChatContainer: GeoChatContainer? = null

    val geoChatContainer get() = mGeoChatContainer

    abstract fun initGeoChatContainer(
        radius: Int,
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        geoMessageDataRepository: GeoMessageDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    )

    open fun clearGeoChatContainer() { mGeoChatContainer = null }

    // My Profile:

    abstract val myProfileDataRepository: MyProfileDataRepository

    protected var mMyProfileContainer: MyProfileContainer? = null
    val myProfileContainer get() = mMyProfileContainer

    abstract fun initMyProfileContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        myProfileDataRepository: MyProfileDataRepository,
        imageDataRepository: ImageDataRepository
    )

    open fun clearMyProfileContainer() { mMyProfileContainer = null }

    // User?:

    abstract val userDataRepository: UserDataRepository

    // Mate:

    abstract val mateMessageDataRepository: MateMessageDataRepository

    var mMateChatContainer: MateChatContainer? = null
    val mateChatContainer get() = mMateChatContainer

    abstract fun initMateChatContainer(
        chatId: Long,
        interlocutorUserId: Long,
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateMessageDataRepository: MateMessageDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    )

    open fun clearMateChatContainer() { mMateChatContainer = null }

    abstract val mateRequestDataRepository: MateRequestDataRepository

    protected var mMateRequestsContainer: MateRequestsContainer? = null
    val mateRequestsContainer get() = mMateRequestsContainer

    abstract fun initMateRequestsContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository
    )

    open fun clearMateRequestsContainer() { mMateRequestsContainer = null }

    abstract val mateChatDataRepository: MateChatDataRepository

    var mMateChatsContainer: MateChatsContainer? = null
    val mateChatsContainer get() = mMateChatsContainer

    abstract fun initMateChatsContainer(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        mateChatDataRepository: MateChatDataRepository,
        imageDataRepository: ImageDataRepository,
        userDataRepository: UserDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    )

    open fun clearMateChatsContainer() { mMateChatsContainer = null }
}