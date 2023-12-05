package com.qubacy.geoqq.ui.screen.geochat.chat

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.util.generator.MessageGeneratorUtility
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.geochat.chat.GeoChatUseCase
import com.qubacy.geoqq.domain.geochat.chat.state.GeoChatState
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.common.chat.ChatViewModelTest
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.chat.model.state.GeoChatUiState
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GeoChatViewModelTest : ChatViewModelTest<GeoChatState, GeoChatUiState>() {
    companion object {
        const val DEFAULT_RADIUS = 1000

        init {
            UriMockContext.mockUri()
        }
    }

    override fun generateChatState(
        messages: List<Message>,
        users: List<User>,
        operations: List<Operation>
    ): GeoChatState {
        return GeoChatState(messages, users, operations)
    }

    override fun initChatViewModel(newState: GeoChatState?) {
        val geoChatUseCastMock = Mockito.mock(GeoChatUseCase::class.java)

        Mockito.`when`(geoChatUseCastMock.getGeoChat(
            Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble())
        ).thenAnswer { setNewUiState(newState) }
        Mockito.`when`(geoChatUseCastMock.getUserDetails(Mockito.anyLong()))
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(geoChatUseCastMock.createMateRequest(Mockito.anyLong()))
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(geoChatUseCastMock.sendGeoMessage(
            Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString())
        ).thenAnswer { setNewUiState(newState) }

        mChatStateFlow = MutableStateFlow<GeoChatState?>(null)

        Mockito.`when`(geoChatUseCastMock.stateFlow).thenAnswer {
            mChatStateFlow
        }

        val mGeoChatUiStateFlowFieldReflection = GeoChatViewModel::class.java
            .getDeclaredField("mGeoChatUiStateFlow")
            .apply { isAccessible = true }

        mModel = GeoChatViewModel(DEFAULT_RADIUS, geoChatUseCastMock)

        mChatUiStateFlow = mGeoChatUiStateFlowFieldReflection.get(mModel) as Flow<GeoChatUiState?>
    }

    private fun initGeoChatViewModel(
        newState: GeoChatState?,
        initialLocationPoint: Point = Point()
    ) {
        initChatViewModel(newState)

        val mLastLocationPointFieldReflection = LocationViewModel::class.java
            .getDeclaredField("mLastLocationPoint")
            .apply { isAccessible = true }

        val mLastLocationPoint = mLastLocationPointFieldReflection.get(mModel) as MutableLiveData<Point?>

        mLastLocationPoint.value = initialLocationPoint
    }

    @Before
    override fun setup() {
        super.setup()

        initChatViewModel()
    }

    data class IsMessageCorrectTestCase(
        val messageText: String,
        val expectedResult: Boolean
    )

    @Test
    fun isMessageCorrectTest() {
        val testCases = listOf(
            IsMessageCorrectTestCase("", false),
            IsMessageCorrectTestCase(" ", true), // todo: it should be failed!
            IsMessageCorrectTestCase("f", true),
            IsMessageCorrectTestCase("fwefwef wef wfwe", true),
        )

        for (testCase in testCases) {
            val result = (mModel as GeoChatViewModel).isMessageCorrect(testCase.messageText)

            assertEquals(testCase.expectedResult, result)
        }
    }

    @Test
    fun getGeoChatTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = GeoChatState(
            MessageGeneratorUtility.generateMessages(2),
            UserGeneratorUtility.generateUsers(2),
            listOf(
                SetMessagesOperation()
            )
        )
        val locationPoint = Point(60.0, 60.0)

        initGeoChatViewModel(newState, locationPoint)

        mChatUiStateFlow.test {
            awaitItem()
            (mModel as GeoChatViewModel).getGeoChat()

            val gottenState = awaitItem()!!

            for (sourceMessage in newState.messages)
                Assert.assertNotNull(gottenState.messages.find { it == sourceMessage })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMessagesUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}