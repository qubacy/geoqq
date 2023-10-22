package com.qubacy.geoqq.ui.screen.mate.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFade
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.mates.chats.entity.MateChatPreview
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.mate.chats.list.adapter.MateChatsAdapter
import com.qubacy.geoqq.ui.screen.mate.chats.list.adapter.MateChatsAdapterCallback
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModelFactory
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.AddChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.operation.UpdateRequestCountUiOperation

class MateChatsFragment() : WaitingFragment(), MateChatsAdapterCallback {
    override val mModel: MateChatsViewModel by viewModels {
        MateChatsViewModelFactory()
    }

    private lateinit var mBinding: FragmentMateChatsBinding

    private lateinit var mAdapter: MateChatsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionWindowBackgroundDrawableResId(R.drawable.mate_background)

        enterTransition = Fade().apply {
            mode = MaterialFade.MODE_IN
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        returnTransition = Fade().apply {
            mode = MaterialFade.MODE_OUT
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }

        exitTransition = MaterialElevationScale(false).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMateChatsBinding.inflate(layoutInflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.mateRequestsCard.visibility = View.GONE

        mAdapter = MateChatsAdapter(this)

        mBinding.chatsRecyclerView.apply {
            layoutManager = AnimatedListLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
            itemAnimator = AnimatedListItemAnimator(mAdapter)
        }
        mBinding.mateRequestsCardButton.setOnClickListener {
            onFriendRequestsClicked()
        }

        mModel.mateChatsUiStateFlow.value?.let {
            initChats(it)
        }
        mModel.mateChatsUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onChatsUiStateGotten(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun initChats(uiState: MateChatsUiState) {
        onMateRequestCountChanged(uiState.requestCount)
        mAdapter.setItems(uiState.chatPreviews)
    }

    private fun onChatsUiStateGotten(chatsUiState: MateChatsUiState) {
        val isListEmpty = mAdapter.itemCount <= 0

        if (isListEmpty) initChats(chatsUiState)
        if (chatsUiState.newUiOperations.isEmpty()) return

        for (uiOperation in chatsUiState.newUiOperations) {
            processUiOperation(uiOperation, isListEmpty)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation, isListEmpty: Boolean) {
        when (uiOperation::class) {
            AddChatUiOperation::class -> {
                if (isListEmpty) return

                val addChatUiOperation = uiOperation as AddChatUiOperation
                val chat = mModel.mateChatsUiStateFlow.value!!.chatPreviews.find {
                    it.chatId == addChatUiOperation.chatId
                }!!

                mAdapter.addItem(chat)
            }
            UpdateChatUiOperation::class -> {
                val updateChatUiOperation = uiOperation as UpdateChatUiOperation
                val chat = mModel.mateChatsUiStateFlow.value!!.chatPreviews.find {
                    it.chatId == updateChatUiOperation.chatId
                }!!

                mAdapter.updateItem(chat)
            }
            UpdateRequestCountUiOperation::class -> {
                val updateRequestCountUiOperation = uiOperation as UpdateRequestCountUiOperation
                val requestCount = mModel.mateChatsUiStateFlow.value!!.requestCount

                onMateRequestCountChanged(requestCount)
            }
            ShowErrorUiOperation::class -> {
                val showErrorOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorOperation.error)
            }
        }
    }

    private fun onMateRequestCountChanged(requestCount: Int) {
        val prevVisibility = mBinding.mateRequestsCard.visibility
        val curVisibility = if (requestCount <= 0) { View.GONE } else { View.VISIBLE }

        if (prevVisibility == curVisibility) return

        setMateRequestsCardAnimation(requestCount > 0)
    }

    private fun setMateRequestsCardAnimation(isFadingIn: Boolean) {
        mBinding.mateRequestsCard.apply {
            this.clearAnimation()

            var endAlpha = 0f
            var endTranslation = 0f

            if (isFadingIn) {
                visibility = View.VISIBLE

                measure(0, 0)

                alpha = 0f
                translationY = -(measuredHeight.toFloat())

                endAlpha = 1f
                endTranslation = 0f

            } else {
                endAlpha = 0f
                endTranslation = -(measuredHeight.toFloat())
            }

            animate()
                .alpha(endAlpha)
                .translationY(endTranslation)
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    if (!isFadingIn) visibility = View.GONE
                }
                .start()
        }
    }

    private fun onFriendRequestsClicked() {
        findNavController().navigate(R.id.action_mateChatsFragment_to_mateRequestsFragment)
    }

    override fun onChatClicked(chatPreview: MateChatPreview, chatView: View) {
        val transitionName = getString(R.string.transition_mate_chats_to_mate_chat)
        val extras = FragmentNavigatorExtras(chatView to transitionName)
        val directions = MateChatsFragmentDirections
            .actionMateChatsFragmentToMateChatFragment(chatPreview.chatId)

        findNavController().navigate(directions, extras)
    }
}