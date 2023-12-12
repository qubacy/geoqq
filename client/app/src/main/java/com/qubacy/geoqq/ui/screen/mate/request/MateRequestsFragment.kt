package com.qubacy.geoqq.ui.screen.mate.request

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.navigation.Navigation
import androidx.transition.Slide
import com.example.carousel3dlib.general.Carousel3DContext
import com.example.carousel3dlib.layoutmanager.Carousel3DLayoutManager
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.mate.request.list.adapter.MateRequestsAdapter
import com.qubacy.geoqq.ui.screen.mate.request.list.adapter.MateRequestsAdapterCallback
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.MateRequestAnswerProcessedUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.SetMateRequestsUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState

class MateRequestsFragment() : WaitingFragment(), MateRequestsAdapterCallback {
    private lateinit var mBinding: FragmentMateRequestsBinding
    private lateinit var mAdapter: MateRequestsAdapter

    private var mInitRequestsRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionWindowBackgroundDrawableResId(R.drawable.mate_background)

        enterTransition = Slide(Gravity.TOP).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        returnTransition = Slide(Gravity.TOP).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
    }

    override fun initFlowContainerIfNull() {
        val application = (requireActivity().application as Application)

        if (application.appContainer.mateRequestsContainer != null) return

        application.appContainer.initMateRequestsContainer(
            application.appContainer.errorDataRepository,
            application.appContainer.tokenDataRepository,
            application.appContainer.mateRequestDataRepository,
            application.appContainer.imageDataRepository,
            application.appContainer.userDataRepository
        )

        mModel = application.appContainer.mateRequestsContainer!!.mateRequestsViewModelFactory
            .create(MateRequestsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMateRequestsBinding.inflate(layoutInflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = MateRequestsAdapter(this)

        mBinding.requestsRecyclerView.apply {
            layoutManager = Carousel3DLayoutManager()
            adapter = mAdapter
        }

        (mModel as MateRequestsViewModel).mateRequestFlow.value?.let {
            initScreenWithState(it)
        }
        (mModel as MateRequestsViewModel).mateRequestFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onUiStateGotten(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()

            if (mInitRequestsRequested) getInitRequests()
        }
    }

    private fun getInitRequests() {
        (mModel as MateRequestsViewModel).getMateRequests()
    }

    private fun initScreenWithState(state: MateRequestsUiState) {
        mAdapter.setItems(state.mateRequests)
    }

    override fun clearFlowContainer() {
        (requireActivity().application as Application).appContainer.clearMateRequestsContainer()
    }

    private fun onUiStateGotten(uiState: MateRequestsUiState) {
        while (true) {
            val uiOperation = uiState.takeUiOperation() ?: break

            processUiOperation(uiOperation, uiState)
        }

        //if (uiState.mateRequests.isEmpty()) navigateBack() // todo: resolve this sh*t
    }

    private fun navigateBack() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    private fun processUiOperation(uiOperation: UiOperation, state: MateRequestsUiState) {
        when (uiOperation::class) {
            SetMateRequestsUiOperation::class -> {
                val setMateRequestsUiOperation = uiOperation as SetMateRequestsUiOperation

                initScreenWithState(state)
            }
            MateRequestAnswerProcessedUiOperation::class -> {
                val mateRequestAnswerProcessedUiOperation =
                    uiOperation as MateRequestAnswerProcessedUiOperation

                initScreenWithState(state)
            }
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    override fun getUserById(userId: Long): User {
        val user = (mModel as MateRequestsViewModel).mateRequestFlow.value!!
            .users.find { it.id == userId }!!

        return user
    }

    override fun onMateRequestSwiped(
        position: Int,
        mateRequest: MateRequest,
        direction: Carousel3DContext.SwipeDirection
    ) {
        if (direction == Carousel3DContext.SwipeDirection.RIGHT) {
            (mModel as MateRequestsViewModel).acceptMateRequest(position, mateRequest)

        } else {
            (mModel as MateRequestsViewModel).declineMateRequest(position, mateRequest)
        }
    }

    override fun onRequestListVerticalRoll(
        edgePosition: Int,
        direction: Carousel3DContext.RollingDirection
    ) {
        (mModel as MateRequestsViewModel).mateRequestsListRolled(edgePosition, direction)
    }

    override fun handleWaitingAbort() {
        if ((mModel as MateRequestsViewModel).isGettingRequests) return

        super.handleWaitingAbort()
    }

    override fun getPermissionsToRequest(): Array<String>? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        requestInitMateRequests()

        return null
    }

    override fun onRequestedPermissionsGranted() {
        requestInitMateRequests()
    }

    private fun requestInitMateRequests() {
        if (view == null) {
            mInitRequestsRequested = true

            return
        }

        getInitRequests()
    }
}