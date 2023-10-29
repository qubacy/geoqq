package com.qubacy.geoqq.ui.screen.mate.request

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.transition.Slide
import com.example.carousel3dlib.general.Carousel3DContext
import com.example.carousel3dlib.layoutmanager.Carousel3DLayoutManager
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.mates.request.entity.MateRequest
import com.qubacy.geoqq.databinding.FragmentMateRequestsBinding
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.mate.request.list.adapter.MateRequestsAdapter
import com.qubacy.geoqq.ui.screen.mate.request.list.adapter.MateRequestsAdapterCallback
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModelFactory
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

        mModel = MateRequestsViewModelFactory().create(MateRequestsViewModel::class.java)
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
            mAdapter.setItems(it.mateRequests)
        }
            (mModel as MateRequestsViewModel).mateRequestFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onUiStateGotten(it)
        }
    }

    private fun onUiStateGotten(uiState: MateRequestsUiState) {
        mAdapter.setItems(uiState.mateRequests)

        while (true) {
            val uiOperation = uiState.takeUiOperation() ?: break

            processUiOperation(uiOperation)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation) {
        // todo: do i need to process any other UI operations here?

        when (uiOperation::class) {
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    override fun getUserById(userId: Long): User {
        return (mModel as MateRequestsViewModel).mateRequestFlow.value!!
            .users.find { it.userId == userId }!!
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