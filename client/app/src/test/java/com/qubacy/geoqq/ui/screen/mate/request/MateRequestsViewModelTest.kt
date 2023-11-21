package com.qubacy.geoqq.ui.screen.mate.request

import android.net.Uri
import app.cash.turbine.test
import com.qubacy.geoqq.common.UriMockContext
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestsOperation
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.MateRequestAnswerProcessedUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.SetMateRequestsUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MateRequestsViewModelTest : ViewModelTest() {
    companion object {
        init {
            UriMockContext.mockUri()
        }
    }

    private lateinit var mModel: MateRequestsViewModel
    private lateinit var mMateRequestsStateFlow: MutableStateFlow<MateRequestsState?>

    private lateinit var mMateRequestsUiStateFlow: Flow<MateRequestsUiState?>

    private fun setNewUiState(newState: MateRequestsState?) = runTest {
        if (newState == null) return@runTest

        mMateRequestsStateFlow.emit(newState)
    }

    private fun initMateRequestsViewModel(
        newState: MateRequestsState? = null
    ) {
        val mateRequestsUseCastMock = Mockito.mock(MateRequestsUseCase::class.java)

        Mockito.`when`(mateRequestsUseCastMock.getMateRequests(Mockito.anyInt()))
            .thenAnswer { setNewUiState(newState) }
        Mockito.`when`(mateRequestsUseCastMock.answerMateRequest(Mockito.anyLong(), Mockito.anyBoolean()))
            .thenAnswer { setNewUiState(newState) }

        mMateRequestsStateFlow = MutableStateFlow<MateRequestsState?>(null)

        Mockito.`when`(mateRequestsUseCastMock.stateFlow).thenAnswer {
            mMateRequestsStateFlow
        }

        val mMateRequestsUiStateFlowFieldReflection = MateRequestsViewModel::class.java
            .getDeclaredField("mMateRequestsUiStateFlow")
            .apply { isAccessible = true }

        mModel = MateRequestsViewModel(mateRequestsUseCastMock)
        mMateRequestsUiStateFlow = mMateRequestsUiStateFlowFieldReflection.get(mModel) as Flow<MateRequestsUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initMateRequestsViewModel()
    }

    @Test
    fun getMateRequestsTest() = runTest {
        val mockUri = Uri.parse(String())
        val newState = MateRequestsState(
            listOf(
                MateRequest(0, 1),
                MateRequest(1, 2),
            ),
            listOf(
                User(1, "test", "pox", mockUri, true),
                User(2, "test 2", "pox", mockUri, true)
            ),
            listOf(
                SetMateRequestsOperation()
            )
        )

        initMateRequestsViewModel(newState)

        mMateRequestsUiStateFlow.test {
            awaitItem()
            mModel.getMateRequests()

            val gottenState = awaitItem()!!

            for (sourceRequest in newState.mateRequests)
                Assert.assertNotNull(gottenState.mateRequests.find { it == sourceRequest })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMateRequestsUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val newState = MateRequestsState(
            listOf(),
            listOf(),
            listOf(
                MateRequestAnswerProcessedOperation()
            )
        )

        initMateRequestsViewModel(newState)

        mMateRequestsUiStateFlow.test {
            awaitItem()
            mModel.getMateRequests()

            val gottenState = awaitItem()!!

            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(
                MateRequestAnswerProcessedUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}