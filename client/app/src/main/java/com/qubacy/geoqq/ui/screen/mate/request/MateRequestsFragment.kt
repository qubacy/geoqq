package com.qubacy.geoqq.ui.screen.mate.request

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.transition.Slide
import com.example.carousel3dlib.general.Carousel3DContext
import com.example.carousel3dlib.layoutmanager.Carousel3DLayoutManager
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.Application
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.mate.request.list.adapter.MateRequestsAdapter
import com.qubacy.geoqq.ui.screen.mate.request.list.adapter.MateRequestsAdapterCallback
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.SetMateRequestsUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState

class MateRequestsFragment() : WaitingFragment(), MateRequestsAdapterCallback {
    private lateinit var mBinding: FragmentMateRequestsBinding
    private lateinit var mAdapter: MateRequestsAdapter

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

        if (application.appContainer.mateChatContainer != null) return

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
            (mModel as MateRequestsViewModel).getMateRequests()
        }
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
    }

    private fun processUiOperation(uiOperation: UiOperation, state: MateRequestsUiState) {
        when (uiOperation::class) {
            SetMateRequestsUiOperation::class -> {
                val setMateRequestsUiOperation = uiOperation as SetMateRequestsUiOperation

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
        mateRequest: MateRequest,
        direction: Carousel3DContext.SwipeDirection
    ) {
        if (direction == Carousel3DContext.SwipeDirection.RIGHT) {
            (mModel as MateRequestsViewModel).acceptMateRequest(mateRequest)

        } else {
            (mModel as MateRequestsViewModel).declineMateRequest(mateRequest)
        }
    }
}