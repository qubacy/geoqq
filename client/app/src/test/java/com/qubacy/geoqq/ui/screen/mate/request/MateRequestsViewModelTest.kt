package com.qubacy.geoqq.ui.screen.mate.request

import android.net.Uri
import app.cash.turbine.test
import com.example.carousel3dlib.general.Carousel3DContext
import com.qubacy.geoqq.common.util.mock.UriMockContext
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.util.generator.MateRequestGeneratorUtility
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestCountOperation
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
        const val DEFAULT_REQUEST_COUNT = 1

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
        getMateRequestsStateHashMap: HashMap<Int, MateRequestsState>? = null,
        answerMateRequestState: MateRequestsState? = null,
        mateCount: Int = DEFAULT_REQUEST_COUNT
    ) {
        val countState = MateRequestsState(
            hashMapOf(), listOf(), listOf(SetMateRequestCountOperation(mateCount)))
        val mateRequestsUseCastMock = Mockito.mock(MateRequestsUseCase::class.java)

        Mockito.`when`(mateRequestsUseCastMock.getMateRequests(
            Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean())
        ).thenAnswer {
            val offset = it.arguments[1]!!

            setNewUiState(getMateRequestsStateHashMap!!.get(offset)!!)
        }
        Mockito.`when`(mateRequestsUseCastMock.answerMateRequest(
            Mockito.anyLong(), Mockito.anyBoolean())
        ).thenAnswer { setNewUiState(answerMateRequestState) }
        Mockito.`when`(mateRequestsUseCastMock.getMateRequestCount()).thenAnswer {
            setNewUiState(countState)
        }

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
            hashMapOf(
                0L to listOf(
                    MateRequest(0, 1),
                    MateRequest(1, 2)
                )
            ),
            listOf(
                User(1, "test", "pox", mockUri, true),
                User(2, "test 2", "pox", mockUri, true)
            ),
            listOf(
                SetMateRequestsOperation(false)
            )
        )

        initMateRequestsViewModel(hashMapOf(0 to newState))

        mMateRequestsUiStateFlow.test {
            awaitItem()
            mModel.getMateRequests()
            awaitItem() // skipping count state;

            val gottenState = awaitItem()!!

            for (gottenMateRequest in gottenState.mateRequests)
                Assert.assertNotNull(newState.mateRequestChunks.values.find { requests ->
                    requests.find { it.id == gottenMateRequest.id } != null
                })
            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMateRequestsUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun getMoreMateRequestOnEdgeReachedTest() = runTest {
        val edgePos = 20
        val count = 50

        val lastChunkSizeDiff = count % MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE
        val lastChunkSize = if (lastChunkSizeDiff == 0) MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE
        else lastChunkSizeDiff

        val newStateForTop = MateRequestsState(
            hashMapOf(
                0L to MateRequestGeneratorUtility.generateMateRequests(
                    MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE)
            ),
            UserGeneratorUtility.generateUsers(count + 1),
            listOf(SetMateRequestsOperation(true))
        )
        val newStateForBottom = MateRequestsState(
            hashMapOf(
                0L to newStateForTop.mateRequestChunks[0]!!,
                40L to MateRequestGeneratorUtility.generateMateRequests(
                    lastChunkSize,
                    (count - lastChunkSize).toLong())
            ),
            newStateForTop.users,
            listOf(SetMateRequestsOperation(true))
        )
        val newStateForHittingEdge = MateRequestsState(
            hashMapOf(
                0L to newStateForTop.mateRequestChunks[0]!!,
                20L to MateRequestGeneratorUtility.generateMateRequests(
                    MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE,
                    MateRequestsViewModel.DEFAULT_REQUEST_CHUNK_SIZE.toLong()),
                40L to newStateForBottom.mateRequestChunks[40]!!
            ),
            newStateForTop.users,
            listOf(SetMateRequestsOperation(false))
        )

        initMateRequestsViewModel(
            getMateRequestsStateHashMap = hashMapOf(
                0 to newStateForTop,
                40 to newStateForBottom,
                20 to newStateForHittingEdge
            ),
            mateCount = count
        )

        mMateRequestsUiStateFlow.test {
            awaitItem() // init. null state;
            mModel.getMateRequests()
            skipItems(3) // todo: DOESNT WORK!!! newStateForTop doesnt get stateToUiState() method;
            mModel.mateRequestsListRolled(edgePos, Carousel3DContext.RollingDirection.UP)

            val gottenState = awaitItem()!!

            for (gottenMateRequest in gottenState.mateRequests)
                Assert.assertNotNull(newStateForHittingEdge.mateRequestChunks.values.find { requests ->
                    requests.find { it.id == gottenMateRequest.id } != null
                })
            for (sourceUser in newStateForHittingEdge.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(SetMateRequestsUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val newState = MateRequestsState(
            hashMapOf(),
            listOf(),
            listOf(
                MateRequestAnswerProcessedOperation()
            )
        )

        initMateRequestsViewModel(answerMateRequestState = newState)

        mMateRequestsUiStateFlow.test {
            awaitItem()
            mModel.getMateRequests()
            awaitItem() // skipping count state;

            val gottenState = awaitItem()!!

            for (sourceUser in newState.users)
                Assert.assertNotNull(gottenState.users.find { it == sourceUser })

            Assert.assertEquals(
                MateRequestAnswerProcessedUiOperation::class, gottenState.takeUiOperation()!!::class)
        }
    }
}