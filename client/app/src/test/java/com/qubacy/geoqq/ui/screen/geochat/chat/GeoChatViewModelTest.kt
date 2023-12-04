package com.qubacy.geoqq.ui.screen.geochat.chat

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation
import com.qubacy.geoqq.domain.geochat.chat.GeoChatUseCase
import com.qubacy.geoqq.domain.geochat.chat.state.GeoChatState
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
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

class GeoChatViewModelTest : ViewModelTest() {
    companion object {
        const val DEFAULT_RADIUS = 1000

        init {
            UriMockContext.mockUri()
        }
    }

    private lateinit var mModel: GeoChatViewModel
    private lateinit var mGeoChatStateFlow: MutableStateFlow<GeoChatState?>

    private lateinit var mGeoChatUiStateFlow: Flow<GeoChatUiState?>



    private fun setNewUiState(newState: GeoChatState?) = runTest {
        if (newState == null) return@runTest

        mGeoChatStateFlow.emit(newState)
    }

    private fun initGeoChatViewModel(
        newState: GeoChatState? = null,
        initialLocationPoint: Point? = null
    ) {
        val geoChatUseCastMock = Mockito.mock(GeoChatUseCase::class.java)

        Mockito.`when`(geoChatUseCastMock.getGeoChat(
            Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble())
        ).thenAnswer { setNewUiState(newState) }

        mGeoChatStateFlow = MutableStateFlow<GeoChatState?>(null)

        Mockito.`when`(geoChatUseCastMock.stateFlow).thenAnswer {
            mGeoChatStateFlow
        }

        val mGeoChatUiStateFlowFieldReflection = GeoChatViewModel::class.java
            .getDeclaredField("mGeoChatUiStateFlow")
            .apply { isAccessible = true }
        val mLastLocationPointFieldReflection = LocationViewModel::class.java
            .getDeclaredField("mLastLocationPoint")
            .apply { isAccessible = true }

        mModel = GeoChatViewModel(DEFAULT_RADIUS, geoChatUseCastMock)

        val mLastLocationPoint = mLastLocationPointFieldReflection.get(mModel) as MutableLiveData<Point?>

        mLastLocationPoint.value = initialLocationPoint

        mGeoChatUiStateFlow = mGeoChatUiStateFlowFieldReflection.get(mModel) as Flow<GeoChatUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initGeoChatViewModel()
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
            val result = mModel.isMessageCorrect(testCase.messageText)

            assertEquals(testCase.expectedResult, result)
        }
    }

    @Test
    fun getGeoChatTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = GeoChatState(
            listOf(
                Message(0, 0, "test 1", 100L),
                Message(1, 1, "test 2", 100L)
            ),
            listOf(
                User(0, "me", "pox", mockUri, true),
                User(1, "test", "pox", mockUri, true)
            ),
            listOf(
                SetMessagesOperation()
            )
        )
        val locationPoint = Point(60.0, 60.0)

        initGeoChatViewModel(newState, locationPoint)

        mGeoChatUiStateFlow.test {
            awaitItem()
            mModel.getGeoChat()

            val gottenState = awaitItem()!!

            for (sourceMessage in newState.messages)
                Assert.assertNotNull(gottenState.messages.find { it == sourceMessage })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMessagesUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}