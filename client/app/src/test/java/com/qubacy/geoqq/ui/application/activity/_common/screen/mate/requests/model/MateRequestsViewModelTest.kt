package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.UriMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.requests.projection.MateRequestChunk
import com.qubacy.geoqq.domain.mate.requests.usecase.impl.MateRequestsUseCaseImpl
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.toMateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.chunk.insert.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.request.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.request.UpdateRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.reflect.Field

class MateRequestsViewModelTest(

) : BusinessViewModelTest<MateRequestsUiState, MateRequestsUseCaseImpl, MateRequestsViewModel>(
    MateRequestsUseCaseImpl::class.java
) {
    companion object {
        val DEFAULT_IMAGE = Image(0L, UriMockUtil.getMockedUri())
        val DEFAULT_USER = User(
            0L, "test", "test", DEFAULT_IMAGE, false, false)
        val DEFAULT_MATE_REQUEST = MateRequest(0L, DEFAULT_USER)

        val DEFAULT_MATE_REQUEST_PRESENTATION = DEFAULT_MATE_REQUEST.toMateRequestPresentation()
    }

    private lateinit var mIsGettingNextChatChunkFieldReflection: Field

    private var mGetRequestChunkCallFlag = false
    private var mAnswerRequestCallFlag = false
    private var mGetInterlocutorCallFlag = false

    override fun clear() {
        super.clear()

        mGetRequestChunkCallFlag = false
        mAnswerRequestCallFlag = false
        mGetInterlocutorCallFlag = false
    }

    override fun preInit() {
        super.preInit()

        mIsGettingNextChatChunkFieldReflection = MateRequestsViewModel::class.java
            .getDeclaredField("mIsGettingNextRequestChunk")
            .apply { isAccessible = true }
    }

    override fun initUseCase(): MateRequestsUseCaseImpl {
        val mateRequestsUseCaseMock = super.initUseCase()

        Mockito.`when`(mateRequestsUseCaseMock.getRequestChunk(Mockito.anyInt())).thenAnswer {
            mGetRequestChunkCallFlag = true

            Unit
        }
        Mockito.`when`(mateRequestsUseCaseMock.answerRequest(
            Mockito.anyLong(), Mockito.anyBoolean()
        )).thenAnswer {
            mAnswerRequestCallFlag = true

            Unit
        }
        Mockito.`when`(mateRequestsUseCaseMock.getInterlocutor(Mockito.anyLong())).thenAnswer {
            mGetInterlocutorCallFlag = true

            Unit
        }

        return mateRequestsUseCaseMock
    }

    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDatabaseDataSourceImpl
    ): MateRequestsViewModel {
        return MateRequestsViewModel(savedStateHandle, errorDataSource, mUseCase)
    }

    @Test
    fun getUserProfileWithMateRequestIdTest() {
        val request = DEFAULT_MATE_REQUEST_PRESENTATION
        val initRequests = mutableListOf(request)
        val initUiState = MateRequestsUiState(requests = initRequests)

        val expectedUserPresentation = request.user

        setUiState(initUiState)

        val gottenUserPresentation = mModel.getUserProfileWithMateRequestId(request.id)

        Assert.assertEquals(expectedUserPresentation, gottenUserPresentation)
    }

    @Test
    fun getNextRequestChunkTest() = runTest {
        val initIsGettingNextRequestChunk = false
        val initLoadingState = false
        val initRequests = mutableListOf<MateRequestPresentation>()
        val initUiState = MateRequestsUiState(
            requests = initRequests, isLoading = initLoadingState)

        val expectedIsGettingNextRequestChunk = true
        val expectedLoadingState = true

        mIsGettingNextChatChunkFieldReflection.set(mModel, initIsGettingNextRequestChunk)
        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.getNextRequestChunk()

            val operation = awaitItem()

            Assert.assertTrue(mGetRequestChunkCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenIsGettingNextRequestChunk = mIsGettingNextChatChunkFieldReflection.get(mModel)
            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedIsGettingNextRequestChunk, gottenIsGettingNextRequestChunk)
            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun answerRequestTest() = runTest {
        val request = DEFAULT_MATE_REQUEST_PRESENTATION
        val initLoadingState = false
        val initRequests = mutableListOf(request)
        val initUiState = MateRequestsUiState(
            requests = initRequests, isLoading = initLoadingState)

        val position = initRequests.size - 1
        val isAccepted = true

        val expectedLoadingState = true

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.answerRequest(position, isAccepted)

            val operation = awaitItem()

            Assert.assertTrue(mAnswerRequestCallFlag)
            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            val gottenLoadingState = (operation as SetLoadingStateUiOperation).isLoading
            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun isNextRequestChunkGettingAllowedTest() {
        data class TestCase(
            val requests: MutableList<MateRequestPresentation>,
            val newRequestCount: Int,
            val answeredRequestCount: Int,
            val isGettingNextRequestChunk: Boolean,
            val expectedIsNextRequestChunkGettingAllowed: Boolean
        )

        val testCases = listOf(
            TestCase(
                mutableListOf(),
                0,
                0,
                false,
                true
            ),
            TestCase(
                mutableListOf(),
                0,
                0,
                true,
                false
            ),
            TestCase(
                mutableListOf(),
                3,
                0,
                false,
                true
            ),
            TestCase(
                mutableListOf(),
                0,
                MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE - 1,
                false,
                false
            ),
            TestCase(
                mutableListOf(),
                0,
                MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE,
                false,
                true
            ),
            TestCase(
                mutableListOf<MateRequestPresentation>().apply {
                    repeat(MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE - 1) {
                        add(DEFAULT_MATE_REQUEST_PRESENTATION)
                    }
                },
                0,
                0,
                false,
                false
            ),
            TestCase(
                mutableListOf<MateRequestPresentation>().apply {
                   repeat(MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE) {
                       add(DEFAULT_MATE_REQUEST_PRESENTATION)
                   }
                },
                0,
                0,
                false,
                true
            ),
            TestCase(
                mutableListOf<MateRequestPresentation>().apply {
                    repeat(MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE - 1) {
                        add(DEFAULT_MATE_REQUEST_PRESENTATION)
                    }
                },
                0,
                1,
                false,
                true
            ),
            TestCase(
                mutableListOf<MateRequestPresentation>().apply {
                    repeat(MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE + 1) {
                        add(DEFAULT_MATE_REQUEST_PRESENTATION)
                    }
                },
                1,
                0,
                false,
                true
            ),
            TestCase(
                mutableListOf<MateRequestPresentation>().apply {
                    repeat(MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE - 2 + 1) {
                        add(DEFAULT_MATE_REQUEST_PRESENTATION)
                    }
                },
                1,
                2,
                false,
                true
            ),
            TestCase(
                mutableListOf<MateRequestPresentation>().apply {
                    repeat(MateRequestsUseCaseImpl.DEFAULT_REQUEST_CHUNK_SIZE - 2 + 1) {
                        add(DEFAULT_MATE_REQUEST_PRESENTATION)
                    }
                },
                2,
                2,
                false,
                false
            ),
        )

        for (testCase in testCases) {
            println(testCase.toString())

            val uiState = MateRequestsUiState(
                requests = testCase.requests,
                newRequestCount = testCase.newRequestCount,
                answeredRequestCount = testCase.answeredRequestCount
            )

            mIsGettingNextChatChunkFieldReflection.set(mModel, testCase.isGettingNextRequestChunk)
            setUiState(uiState)

            val gottenIsGettingNextRequestChunkAllowed = mModel.isNextRequestChunkGettingAllowed()

            Assert.assertEquals(
                testCase.expectedIsNextRequestChunkGettingAllowed,
                gottenIsGettingNextRequestChunkAllowed
            )
        }
    }

    @Test
    fun resetRequestsTest() {
        val initRequests = mutableListOf(
            DEFAULT_MATE_REQUEST_PRESENTATION
        )
        val initNewRequestCount = 1
        val initAnsweredRequestCount = 1
        val initUiState = MateRequestsUiState(
            requests = initRequests,
            newRequestCount = initNewRequestCount,
            answeredRequestCount = initAnsweredRequestCount
        )

        val expectedRequests = mutableListOf<MateRequestPresentation>()
        val expectedNewRequestCount = 0
        val expectedAnsweredRequestCount = 0

        setUiState(initUiState)

        mModel.resetRequests()

        val finalUiState = mModel.uiState

        AssertUtils.assertEqualContent(expectedRequests, finalUiState.requests)
        Assert.assertEquals(expectedNewRequestCount, finalUiState.newRequestCount)
        Assert.assertEquals(expectedAnsweredRequestCount, finalUiState.answeredRequestCount)
    }

    @Test
    fun processGetInterlocutorDomainResultTest() = runTest {
        val initRequest = DEFAULT_MATE_REQUEST_PRESENTATION
        val initRequests = mutableListOf(initRequest)
        val initUiState = MateRequestsUiState(
            requests = initRequests
        )

        val user = DEFAULT_USER.copy(username = "result user")
        val getInterlocutorDomainResult = GetInterlocutorDomainResult(interlocutor = user)

        val expectedUserPresentation = user.toUserPresentation()
        val expectedRequests = initRequests.map {
            if (it.user.id == user.id) it.copy(user = expectedUserPresentation) else it
        }

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getInterlocutorDomainResult)

            val operation = awaitItem()

            Assert.assertEquals(ShowInterlocutorDetailsUiOperation::class, operation::class)

            val gottenUserPresentation = (operation as ShowInterlocutorDetailsUiOperation)
                .interlocutor
            val gottenRequests = mModel.uiState.requests

            Assert.assertEquals(expectedUserPresentation, gottenUserPresentation)
            AssertUtils.assertEqualContent(expectedRequests, gottenRequests)
        }
    }

    @Test
    fun processUpdateInterlocutorDomainResultTest() = runTest {
        val initRequest = DEFAULT_MATE_REQUEST_PRESENTATION
        val initRequests = mutableListOf(initRequest)
        val initUiState = MateRequestsUiState(
            requests = initRequests
        )

        val user = DEFAULT_USER.copy(username = "result user")
        val updateInterlocutorDomainResult = UpdateInterlocutorDomainResult(interlocutor = user)

        val expectedUserPresentation = user.toUserPresentation()
        val expectedUpdatedRequest = initRequest.copy(user = expectedUserPresentation)
        val expectedRequests = initRequests.map {
            if (it.user.id == user.id) it.copy(user = expectedUserPresentation) else it
        }

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(updateInterlocutorDomainResult)

            val interlocutorOperation = awaitItem()

            Assert.assertEquals(UpdateInterlocutorDetailsUiOperation::class,
                interlocutorOperation::class)

            val gottenUserPresentation =
                (interlocutorOperation as UpdateInterlocutorDetailsUiOperation).interlocutor
            Assert.assertEquals(expectedUserPresentation, gottenUserPresentation)

            val requestOperation = awaitItem()

            Assert.assertEquals(UpdateRequestUiOperation::class, requestOperation::class)

            val gottenUpdatedRequest = (requestOperation as UpdateRequestUiOperation).request
            val gottenRequests = mModel.uiState.requests

            Assert.assertEquals(expectedUpdatedRequest, gottenUpdatedRequest)

            AssertUtils.assertEqualContent(expectedRequests, gottenRequests)
        }
    }

    @Test
    fun processGetRequestChunkDomainResultTest() = runTest {
        val initLoadingState = true
        val initIsGettingNextRequestChunk = true
        val initRequests = mutableListOf(DEFAULT_MATE_REQUEST_PRESENTATION)
        val initUiState = MateRequestsUiState(
            isLoading = initLoadingState,
            requests = initRequests,
        )

        val offset = 0
        val requestChunk = MateRequestChunk(offset, mutableListOf(DEFAULT_MATE_REQUEST))
        val getRequestChunkDomainResult = GetRequestChunkDomainResult(chunk = requestChunk)

        val expectedLoadingState = false
        val expectedIsGettingNextRequestChunk = false
        val expectedChunkPosition = initRequests.size
        val expectedRequestsForInsertion = requestChunk.requests.map { it.toMateRequestPresentation() }
        val expectedRequests = initRequests.plus(expectedRequestsForInsertion)

        mIsGettingNextChatChunkFieldReflection.set(mModel, initIsGettingNextRequestChunk)
        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(getRequestChunkDomainResult)

            val insertingRequestsOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(InsertRequestsUiOperation::class, insertingRequestsOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            insertingRequestsOperation as InsertRequestsUiOperation

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenInGettingNextRequestChunk = mIsGettingNextChatChunkFieldReflection.get(mModel)
            val gottenChunkPosition = insertingRequestsOperation.position
            val gottenRequestsForInsertion = insertingRequestsOperation.requests
            val gottenRequests = mModel.uiState.requests

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedIsGettingNextRequestChunk, gottenInGettingNextRequestChunk)
            Assert.assertEquals(expectedChunkPosition, gottenChunkPosition)
            AssertUtils.assertEqualContent(expectedRequestsForInsertion, gottenRequestsForInsertion)
            AssertUtils.assertEqualContent(expectedRequests, gottenRequests)
        }
    }

    @Test
    fun processAnswerMateRequestDomainResultTest() = runTest {
        val initLoadingState = true
        val initRequest = DEFAULT_MATE_REQUEST_PRESENTATION
        val initRequests = mutableListOf(initRequest)
        val initUiState = MateRequestsUiState(
            isLoading = initLoadingState,
            requests = initRequests,
        )

        val requestToRemove = initRequest
        val requestId = requestToRemove.id
        val answerMateRequestDomainResult = AnswerMateRequestDomainResult(requestId = requestId)

        val expectedLoadingState = false
        val expectedRequestPositionForRemoval = 0
        val expectedRequests = initRequests.minus(requestToRemove)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mResultFlow.emit(answerMateRequestDomainResult)

            val removingRequestOperation = awaitItem()
            val loadingOperation = awaitItem()

            Assert.assertEquals(RemoveRequestUiOperation::class, removingRequestOperation::class)
            Assert.assertEquals(SetLoadingStateUiOperation::class, loadingOperation::class)

            val gottenLoadingState = (loadingOperation as SetLoadingStateUiOperation).isLoading
            val gottenRequestPositionForRemoval =
                (removingRequestOperation as RemoveRequestUiOperation).position
            val gottenRequests = mModel.uiState.requests

            Assert.assertEquals(expectedLoadingState, gottenLoadingState)
            Assert.assertEquals(expectedRequestPositionForRemoval, gottenRequestPositionForRemoval)
            AssertUtils.assertEqualContent(expectedRequests, gottenRequests)
        }
    }
}