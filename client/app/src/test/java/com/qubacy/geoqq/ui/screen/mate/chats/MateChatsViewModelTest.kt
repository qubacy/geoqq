package com.qubacy.geoqq.ui.screen.mate.chats

import android.net.Uri
import app.cash.turbine.test
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.SetMateChatsUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MateChatsViewModelTest() : ViewModelTest() {
    companion object {
        init {
            UriMockContext.mockUri()
        }
    }

    private lateinit var mModel: MateChatsViewModel
    private lateinit var mMateChatsStateFlow: MutableStateFlow<MateChatsState?>

    private lateinit var mMateChatsUiStateFlow: Flow<MateChatsUiState?>

    private fun setNewUiState(newState: MateChatsState?) = runTest {
        if (newState == null) return@runTest

        mMateChatsStateFlow.emit(newState)
    }

    private fun initMateChatsViewModel(
        newState: MateChatsState? = null
    ) {
        val mateChatsUseCastMock = Mockito.mock(MateChatsUseCase::class.java)

        Mockito.`when`(mateChatsUseCastMock.getMateChats(Mockito.anyInt()))
            .thenAnswer { setNewUiState(newState) }

        mMateChatsStateFlow = MutableStateFlow<MateChatsState?>(null)

        Mockito.`when`(mateChatsUseCastMock.stateFlow).thenAnswer {
            mMateChatsStateFlow
        }

        val mMateChatsUiStateFlowFieldReflection = MateChatsViewModel::class.java
            .getDeclaredField("mMateChatsUiStateFlow")
            .apply { isAccessible = true }

        mModel = MateChatsViewModel(mateChatsUseCastMock)
        mMateChatsUiStateFlow = mMateChatsUiStateFlowFieldReflection.get(mModel) as Flow<MateChatsUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initMateChatsViewModel()
    }

    @Test
    fun getChatsTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MateChatsState(
            listOf(
                MateChat(0, 1, mockUri, null),
                MateChat(1, 2, mockUri, null)
            ),
            listOf(
                User(0, "me", "pox", mockUri, true),
                User(1, "test", "pox", mockUri, true),
                User(2, "test 2", "pox", mockUri, true)
            ),
            0,
            listOf(
                SetMateChatsOperation()
            )
        )

        initMateChatsViewModel(newState)

        mMateChatsUiStateFlow.test {
            awaitItem()
            mModel.getMateChats()

            val gottenState = awaitItem()!!

            for (sourceChat in newState.chats)
                Assert.assertNotNull(gottenState.chats.find { it == sourceChat })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMateChatsUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}