package com.qubacy.geoqq.domain.myprofile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.qubacy.geoqq.common.util.mock.AnyUtility
import com.qubacy.geoqq.common.util.mock.BitmapMockContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageByUriResult
import com.qubacy.geoqq.data.myprofile.model.avatar.linked.DataMyProfileWithLinkedAvatar
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileResult
import com.qubacy.geoqq.data.myprofile.repository.result.UpdateMyProfileResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

@RunWith(JUnit4::class)
class MyProfileUseCaseTest {
    companion object {
        init {
            BitmapMockContext.mockBitmapFactory()
            UriMockContext.mockUri()
        }
    }

    private lateinit var mMyProfileUseCase: MyProfileUseCase

    private lateinit var mMyProfileStateAtomicRef: AtomicReference<MyProfileState?>

    private fun emitOriginalState(originalMyProfileState: MyProfileState) = runBlocking {
        val stateFlowFieldReflection = UseCase::class.java.getDeclaredField("mStateFlow")
            .apply { isAccessible = true }
        val stateFlow = stateFlowFieldReflection.get(mMyProfileUseCase)
                as MutableStateFlow<MyProfileState>

        stateFlow.emit(originalMyProfileState)
    }

    private fun initMyProfileUseCase(
        getTokensResult: GetTokensResult = GetTokensResult(String(), String()),
        getMyProfileResult: GetMyProfileResult = GetMyProfileResult(DataMyProfileWithLinkedAvatar(
            String(), String(), MyProfileDataModelContext.HitUpOption.NEGATIVE, Uri.parse(String())
        )),
        getImageByUriResult: GetImageByUriResult = GetImageByUriResult(
            BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)
        ),
        updateMyProfileResult: UpdateMyProfileResult = UpdateMyProfileResult(),
        originalMyProfileState: MyProfileState? = null
    ) = runBlocking {
        val errorDataRepository = Mockito.mock(ErrorDataRepository::class.java)

        val tokenDataRepository = Mockito.mock(TokenDataRepository::class.java)

        Mockito.`when`(tokenDataRepository.getTokens()).thenReturn(getTokensResult)

        val myProfileDataRepository = Mockito.mock(MyProfileDataRepository::class.java)

        Mockito.`when`(myProfileDataRepository.resultFlow).thenAnswer {
            MutableSharedFlow<Result>()
        }

        val imageDataRepository = Mockito.mock(ImageDataRepository::class.java)

        Mockito.`when`(imageDataRepository.getImageByUri(AnyUtility.any(Uri::class.java)))
            .thenReturn(getImageByUriResult)

        mMyProfileUseCase = MyProfileUseCase(
            errorDataRepository, tokenDataRepository, myProfileDataRepository, imageDataRepository
        )

        val processResultMethodReflection = ConsumingUseCase::class.memberFunctions
            .find { it.name == "processResult" }!!.apply {
                isAccessible = true
            }
        if (originalMyProfileState != null)
            emitOriginalState(originalMyProfileState)

        Mockito.`when`(myProfileDataRepository.getMyProfile(Mockito.anyString())).thenAnswer {
            runBlocking {
                processResultMethodReflection.callSuspend(mMyProfileUseCase, getMyProfileResult)
            }
        }
        Mockito.`when`(myProfileDataRepository.updateMyProfile(
            Mockito.anyString(),
            AnyUtility.any(Bitmap::class.java),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            AnyUtility.any(MyProfileDataModelContext.HitUpOption::class.java))
        ).thenAnswer {
            runBlocking {
                processResultMethodReflection.callSuspend(mMyProfileUseCase, updateMyProfileResult)
            }

            UpdateMyProfileResult()
        }

        mMyProfileStateAtomicRef = AtomicReference(null)

        GlobalScope.launch(Dispatchers.IO) {
            mMyProfileUseCase.stateFlow.collect {
                if (it == null) return@collect

                mMyProfileStateAtomicRef.set(it)
            }
        }
    }

    @Before
    fun setup() {
        initMyProfileUseCase()
    }

    @Test
    fun getMyProfileTest() {
        val dataMyProfile = DataMyProfileWithLinkedAvatar(
            "test", "test",
            MyProfileDataModelContext.HitUpOption.NEGATIVE, Uri.parse(String())
        )

        initMyProfileUseCase(getMyProfileResult = GetMyProfileResult(dataMyProfile))

        mMyProfileUseCase.getMyProfile()

        while (mMyProfileStateAtomicRef.get() == null ) { }

        val gottenMyProfileState = mMyProfileStateAtomicRef.get()

        Assert.assertEquals(dataMyProfile.username, gottenMyProfileState!!.username)
    }

    @Test
    fun updateMyProfileTest() {
        val avatarUri = Uri.parse(String())
        val description = String()
        val password = String()
        val newPassword = String()
        val hitUpOption = MyProfileDataModelContext.HitUpOption.NEGATIVE

        val prevMyProfileState = MyProfileState(
            avatarUri, "test", description, hitUpOption, listOf())

        initMyProfileUseCase(
            updateMyProfileResult = UpdateMyProfileResult(),
            originalMyProfileState = prevMyProfileState)

        mMyProfileUseCase.updateMyProfile(avatarUri, description, password, newPassword, hitUpOption)

        while (mMyProfileStateAtomicRef.get() == null) { }
        while (mMyProfileStateAtomicRef.get() == prevMyProfileState) { }

        val gottenMyProfileState = mMyProfileStateAtomicRef.get()

        val successfulProfileSavingCallbackOperation = gottenMyProfileState!!.newOperations.first()

        Assert.assertNotNull(successfulProfileSavingCallbackOperation)
        Assert.assertEquals(
            SuccessfulProfileSavingCallbackOperation::class,
            successfulProfileSavingCallbackOperation::class
        )
    }
}