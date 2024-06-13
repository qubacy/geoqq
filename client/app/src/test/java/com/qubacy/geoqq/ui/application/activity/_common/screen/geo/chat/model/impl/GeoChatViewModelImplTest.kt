package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.chat.result.SendMessageDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import com.qubacy.geoqq.domain.geo._common.test.context.GeoUseCaseTestContext
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.get.GetGeoMessagesDomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update.UserUpdatedDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.location.SendLocationDomainResult
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.added.GeoMessageAddedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.model.ChatViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo._common._test.context.GeoTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.add.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.sending.ChangeMessageSendingAllowedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.update.UpdateGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.GeoMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessagePresentation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class GeoChatViewModelImplTest : BusinessViewModelTest<
    GeoChatUiState, GeoChatUseCase, GeoChatViewModelImpl
>(
    GeoChatUseCase::class.java
), ChatViewModelTest<GeoChatViewModelImpl> {
    companion object {
        val DEFAULT_USER = UseCaseTestContext.DEFAULT_USER
        val DEFAULT_GEO_MESSAGE = GeoUseCaseTestContext.DEFAULT_GEO_MESSAGE

        val DEFAULT_GEO_MESSAGE_PRESENTATION = GeoTestContext
            .DEFAULT_GEO_MESSAGE_PRESENTATION
    }

    private var mUseCaseGetLocalUserId: Long? = null

    private var mUseCaseGetMessagesCallFlag = false
    private var mUseCaseSendMessageCallFlag = false
    private var mUseCaseGetInterlocutorCallFlag = false
    private var mUseCaseGetLocalUserIdCallFlag = false
    private var mUseCaseSendMateRequestToInterlocutorCallFlag = false

    override fun clear() {
        super.clear()

        mUseCaseGetLocalUserId = null

        mUseCaseGetInterlocutorCallFlag = false
        mUseCaseSendMessageCallFlag = false
        mUseCaseGetInterlocutorCallFlag = false
        mUseCaseGetLocalUserIdCallFlag = false
        mUseCaseSendMateRequestToInterlocutorCallFlag = false
    }

    override fun initUseCase(): GeoChatUseCase {
        val useCase = super.initUseCase()

        Mockito.`when`(useCase.getMessages(
            Mockito.anyInt(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mUseCaseGetMessagesCallFlag = true

            Unit
        }
        Mockito.`when`(useCase.sendMessage(
            Mockito.anyString(), Mockito.anyInt(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mUseCaseSendMessageCallFlag = true

            Unit
        }
        Mockito.`when`(useCase.getInterlocutor(Mockito.anyLong())).thenAnswer {
            mUseCaseGetInterlocutorCallFlag = true

            Unit
        }
        Mockito.`when`(useCase.getLocalUserId()).thenAnswer {
            mUseCaseGetLocalUserIdCallFlag = true
            mUseCaseGetLocalUserId
        }
        Mockito.`when`(useCase.sendMateRequestToInterlocutor(Mockito.anyLong())).thenAnswer {
            mUseCaseSendMateRequestToInterlocutorCallFlag = true

            Unit
        }

        return useCase
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDatabaseDataSource
    ): GeoChatViewModelImpl {
        return GeoChatViewModelImpl(savedStateHandle, errorDataSource, mUseCase)
    }

    @Test
    fun setLocationContextTest() {
        val initRadius = 0
        val initLatitude = 0f
        val initLongitude = 0f

        val expectedRadius = 100
        val expectedLatitude = 10f
        val expectedLongitude = 5f

        setRadius(initRadius)
        setLatitude(initLatitude)
        setLongitude(initLongitude)

        mModel.setLocationContext(expectedRadius, expectedLongitude, expectedLatitude)

        val gottenRadius = getRadius()
        val gottenLatitude = getLatitude()
        val gottenLongitude = getLongitude()

        Assert.assertEquals(expectedRadius, gottenRadius)
        Assert.assertEquals(expectedLatitude, gottenLatitude)
        Assert.assertEquals(expectedLongitude, gottenLongitude)
    }

    @Test
    fun isLocationContextSetTest() {
        class TestCase(
            val radius: Int?,
            val latitude: Float?,
            val longitude: Float?,
            val expectedIsSet: Boolean
        )

        val testCases = listOf(
            TestCase(null, null, null, false),
            TestCase(10, null, null, false),
            TestCase(null, 1f, null, false),
            TestCase(null, null, 1f, false),
            TestCase(10, 1f, null, false),
            TestCase(10, null, 1f, false),
            TestCase(null, 1f, 1f, false),
            TestCase(10, 1f, 1f, true)
        )

        for (testCase in testCases) {
            testCase.also {
                setRadius(it.radius)
                setLatitude(it.latitude)
                setLongitude(it.longitude)
            }

            val gottenIsSet = mModel.isLocationContextSet()

            Assert.assertEquals(testCase.expectedIsSet, gottenIsSet)
        }
    }

    @Test
    fun getLocalUserIdTest() {
        val expectedLocalUserId = 0L

        mUseCaseGetLocalUserId = expectedLocalUserId

        val gottenLocalUserId = mModel.getLocalUserId()

        Assert.assertTrue(mUseCaseGetLocalUserIdCallFlag)
        Assert.assertEquals(expectedLocalUserId, gottenLocalUserId)
    }

    @Test
    fun getMessagesTest() = runTest {
        val initRadius = 0
        val initLatitude = 0f
        val initLongitude = 0f
        val initLoadingState = false

        val expectedLoadingState = true

        setRadius(initRadius)
        setLatitude(initLatitude)
        setLongitude(initLongitude)
        setUiState(GeoChatUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.getMessages()

            val operation = awaitItem()

            Assert.assertTrue(mUseCaseGetMessagesCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading
            val finalState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalState.isLoading)
        }
    }

    @Test
    fun areMessagesLoadedTest() {
        class TestCase(
            val messages: MutableList<GeoMessagePresentation>,
            val expectedAreLoaded: Boolean
        )

        val testCases = listOf(
            TestCase(mutableListOf(), false),
            TestCase(mutableListOf(DEFAULT_GEO_MESSAGE_PRESENTATION), true)
        )

        for (testCase in testCases) {
            setUiState(GeoChatUiState(messages = testCase.messages))

            val gottenAreLoaded = mModel.areMessagesLoaded()

            Assert.assertEquals(testCase.expectedAreLoaded, gottenAreLoaded)
        }
    }

    @Test
    fun isMessageTextValidTest() {
        class TestCase(
            val text: String,
            val expectedIsValid: Boolean
        )

        val testCases = listOf(
            TestCase("", false),
            TestCase(" f", true),
            TestCase("f ", true)
        )

        for (testCase in testCases) {
            val gottenIsValid = mModel.isMessageTextValid(testCase.text)

            Assert.assertEquals(testCase.expectedIsValid, gottenIsValid)
        }
    }

    @Test
    fun getUserProfileByMessagePositionTest() {
        val initMessages = mutableListOf(DEFAULT_GEO_MESSAGE_PRESENTATION)

        val position = 0

        setUiState(GeoChatUiState(messages = initMessages))

        mModel.getUserProfileByMessagePosition(position)

        Assert.assertTrue(mUseCaseGetInterlocutorCallFlag)
    }

    @Test
    fun addInterlocutorAsMateTest() = runTest {
        val initLoadingState = false

        val userId = 0L

        val expectedLoadingState = true

        setUiState(GeoChatUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.addInterlocutorAsMate(userId)

            val operation = awaitItem()

            Assert.assertTrue(mUseCaseSendMateRequestToInterlocutorCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading
            val finalState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalState.isLoading)
        }
    }

    @Test
    fun sendMessageTest() = runTest {
        val initRadius = 0
        val initLatitude = 0f
        val initLongitude = 0f
        val initLoadingState = false

        val text = "test"

        val expectedLoadingState = true

        setRadius(initRadius)
        setLatitude(initLatitude)
        setLongitude(initLongitude)
        setUiState(GeoChatUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mModel.sendMessage(text)

            val operation = awaitItem()

            Assert.assertTrue(mUseCaseSendMessageCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading
            val finalState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalState.isLoading)
        }
    }

    @Test
    fun resetMessagesTest() {
        val initMessages = mutableListOf(DEFAULT_GEO_MESSAGE_PRESENTATION)

        val expectedMessages = mutableListOf<GeoMessagePresentation>()

        setUiState(GeoChatUiState(messages = initMessages))

        mModel.resetMessages()

        val gottenMessages = mModel.uiState.messages

        AssertUtils.assertEqualContent(expectedMessages, gottenMessages)
    }

    @Test
    fun onGeoChatGetGeoMessagesTest() = runTest {
        val initLoadingState = true

        val geoMessages = listOf(DEFAULT_GEO_MESSAGE)
        val getGeoMessagesDomainResult = GetGeoMessagesDomainResult(messages = geoMessages)

        val expectedLoadingState = false
        val expectedMessages = geoMessages.map { it.toGeoMessagePresentation() }

        setUiState(GeoChatUiState(isLoading = initLoadingState))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getGeoMessagesDomainResult)

            val messagesOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(AddGeoMessagesUiOperation::class, messagesOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenMessages = (messagesOperation as AddGeoMessagesUiOperation).messages
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            AssertUtils.assertEqualContent(expectedMessages, gottenMessages)

            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
            AssertUtils.assertEqualContent(expectedMessages.toMutableList(), finalUiState.messages)
        }
    }

    @Test
    fun onGeoChatNewGeoMessagesTest() = runTest {
        val initUiState = GeoChatUiState(messages = mutableListOf())

        val geoMessage = DEFAULT_GEO_MESSAGE
        val geoMessageAddedDomainResult = GeoMessageAddedDomainResult(message = geoMessage)
        val geoMessagePresentation = geoMessage.toGeoMessagePresentation()

        val expectedMessages = mutableListOf(geoMessagePresentation)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(geoMessageAddedDomainResult)

            val operation = awaitItem()

            Assert.assertEquals(AddGeoMessagesUiOperation::class, operation::class)

            val gottenMessages = (operation as AddGeoMessagesUiOperation).messages
            val finalUiState = mModel.uiState

            AssertUtils.assertEqualContent(expectedMessages, gottenMessages)
            AssertUtils.assertEqualContent(expectedMessages, finalUiState.messages)
        }
    }

    @Test
    override fun onChatSendMessageTest() = runTest {
        val initUiState = GeoChatUiState()

        val sendMessageDomainResult = SendMessageDomainResult()

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(sendMessageDomainResult)

            // nothing to check rn..
        }
    }

    @Test
    fun onGeoChatSendLocationTest() = runTest {
        val initIsMessageSendingAllowed = false
        val initUiState = GeoChatUiState(isMessageSendingAllowed = initIsMessageSendingAllowed)

        val sendLocationDomainResult = SendLocationDomainResult()

        val expectedIsMessageSendingAllowed = true

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(sendLocationDomainResult)

            val operation = awaitItem()

            Assert.assertEquals(ChangeMessageSendingAllowedUiOperation::class, operation::class)

            val gottenIsMessageSendingAllowed =
                (operation as ChangeMessageSendingAllowedUiOperation).isAllowed
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedIsMessageSendingAllowed, gottenIsMessageSendingAllowed)
            Assert.assertEquals(expectedIsMessageSendingAllowed, finalUiState.isMessageSendingAllowed)
        }
    }

    @Test
    fun onUserUpdateUserTest() = runTest {
        val initMessages = mutableListOf(
            DEFAULT_GEO_MESSAGE_PRESENTATION
        )

        val interlocutor = DEFAULT_USER.copy(username = "updated test")
        val updateInterlocutorDomainResult = UserUpdatedDomainResult(user = interlocutor)

        val expectedInterlocutorPresentation = interlocutor.toUserPresentation()
        val expectedMessagePositions = initMessages.indices.toList()
        val expectedMessages = initMessages.map { it.copy(user = expectedInterlocutorPresentation) }

        setUiState(GeoChatUiState(messages = initMessages))

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateInterlocutorDomainResult)

            val interlocutorOperation = awaitItem()
            val messagesOperation = awaitItem()

            Assert.assertEquals(
                UpdateInterlocutorDetailsUiOperation::class, interlocutorOperation::class)
            Assert.assertEquals(UpdateGeoMessagesUiOperation::class, messagesOperation::class)

            messagesOperation as UpdateGeoMessagesUiOperation

            val gottenInterlocutorPresentation =
                (interlocutorOperation as UpdateInterlocutorDetailsUiOperation).interlocutor
            val gottenMessagePositions = messagesOperation.positions
            val gottenMessages = messagesOperation.messages
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedInterlocutorPresentation, gottenInterlocutorPresentation)
            AssertUtils.assertEqualContent(expectedMessagePositions, gottenMessagePositions)
            AssertUtils.assertEqualContent(expectedMessages, gottenMessages)

            AssertUtils.assertEqualContent(expectedMessages.toMutableList(), finalUiState.messages)
        }
    }

    @Test
    fun changeLastLocationTest() {
        val initLatitude = null
        val initLongitude = null
        val initRadius = 0

        val location = mockLocation(0.0, 0.0)

        val expectedLatitude = location.latitude.toFloat()
        val expectedLongitude = location.longitude.toFloat()

        setLatitude(initLatitude)
        setLongitude(initLongitude)
        setRadius(initRadius)

        mModel.changeLastLocation(location)

        val gottenLatitude = getLatitude()
        val gottenLongitude = getLongitude()

        Assert.assertEquals(expectedLatitude, gottenLatitude)
        Assert.assertEquals(expectedLongitude, gottenLongitude)
    }

    private fun mockLocation(latitude: Double, longitude: Double): Location {
        val locationMock = Mockito.mock(Location::class.java)

        Mockito.`when`(locationMock.latitude).thenAnswer { latitude }
        Mockito.`when`(locationMock.longitude).thenAnswer { longitude }

        return locationMock
    }

    private fun setRadius(radius: Int?) {
        GeoChatViewModelImpl::class.java
            .getDeclaredField("mRadius")
            .apply { isAccessible = true }
            .set(mModel, radius)
    }

    private fun setLatitude(latitude: Float?) {
        GeoChatViewModelImpl::class.java
            .getDeclaredField("mLatitude")
            .apply { isAccessible = true }
            .set(mModel, latitude)
    }

    private fun setLongitude(longitude: Float?) {
        GeoChatViewModelImpl::class.java
            .getDeclaredField("mLongitude")
            .apply { isAccessible = true }
            .set(mModel, longitude)
    }

    private fun getRadius(): Int? {
        return GeoChatViewModelImpl::class.java
            .getDeclaredField("mRadius")
            .apply { isAccessible = true }
            .get(mModel) as Int
    }

    private fun getLatitude(): Float? {
        return GeoChatViewModelImpl::class.java
            .getDeclaredField("mLatitude")
            .apply { isAccessible = true }
            .get(mModel) as Float
    }

    private fun getLongitude(): Float? {
        return GeoChatViewModelImpl::class.java
            .getDeclaredField("mLongitude")
            .apply { isAccessible = true }
            .get(mModel) as Float
    }

    override fun getChatViewModelViewModel(): GeoChatViewModelImpl {
        return mModel
    }

    override fun getChatViewModelResultFlow(): MutableSharedFlow<DomainResult> {
        return mResultFlow
    }
}