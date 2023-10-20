package com.qubacy.geoqq.ui.screen.mate.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialElevationScale
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
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

        mModel.mateChatsUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onChatsUiStateGotten(it)
        }
        mModel.mateChatsUiStateFlow.value?.let {
            onMateRequestCountChanged(it.requestCount)
            mAdapter.setItems(it.chats)
        }
    }

    private fun onChatsUiStateGotten(chatsUiState: MateChatsUiState) {
        if (chatsUiState.newUiOperations.isEmpty()) return

        for (uiOperation in chatsUiState.newUiOperations) {
            processUiOperation(uiOperation)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation) {
        when (uiOperation::class) {
            AddChatUiOperation::class -> {
                val addChatUiOperation = uiOperation as AddChatUiOperation
                val chat = mModel.mateChatsUiStateFlow.value!!.chats.find {
                    it.chatId == addChatUiOperation.chatId
                }!!

                mAdapter.addItem(chat)
            }
            UpdateChatUiOperation::class -> {
                val updateChatUiOperation = uiOperation as UpdateChatUiOperation
                val chat = mModel.mateChatsUiStateFlow.value!!.chats.find {
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

    override fun onChatClicked(chat: Chat) {
        val directions = MateChatsFragmentDirections
            .actionMateChatsFragmentToMateChatFragment(chat.chatId)

        findNavController().navigate(directions)
    }
}