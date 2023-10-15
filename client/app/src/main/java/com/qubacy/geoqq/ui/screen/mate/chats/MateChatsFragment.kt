package com.qubacy.geoqq.ui.screen.mate.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
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
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.AddChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.ModifyChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.SetChatsUiOperation
import kotlinx.coroutines.launch

class MateChatsFragment() : WaitingFragment(), MateChatsAdapterCallback {
    override val mModel: MateChatsViewModel by viewModels {
        MateChatsViewModelFactory()
    }

    private lateinit var mBinding: FragmentMateChatsBinding

    private lateinit var mAdapter: MateChatsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        mAdapter = MateChatsAdapter(this)

        mBinding.matesRecyclerView.apply {
            layoutManager = AnimatedListLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
            itemAnimator = AnimatedListItemAnimator(mAdapter)
        }
        mBinding.friendRequestsCardButton.setOnClickListener {
            onFriendRequestsClicked()
        }

        lifecycleScope.launch {
            mModel.mateChatsUiOperationFlow.collect {
                if (it == null) return@collect

                onUiOperationGotten(it)
            }
        }

        initWithUiState(mModel.mateChatsUiState)
    }

    private fun initWithUiState(uiState: MateChatsUiState) {
        mAdapter.setItems(uiState.chats)
    }

    private fun onUiOperationGotten(uiOperation: UiOperation) {
        when (uiOperation::class) {
            SetChatsUiOperation::class -> {
                val setChatsUiOperation = uiOperation as SetChatsUiOperation

                mAdapter.setItems(setChatsUiOperation.chats)
            }
            AddChatUiOperation::class -> {
                val addChatUiOperation = uiOperation as AddChatUiOperation

                mAdapter.addItem(addChatUiOperation.chat)
            }
            ModifyChatUiOperation::class -> {
                val modifyChatUiOperation = uiOperation as ModifyChatUiOperation

                mAdapter.updateItem(modifyChatUiOperation.renewedChat)
            }
            ShowErrorUiOperation::class -> {
                val showErrorOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorOperation.error)
            }
        }
    }

    private fun onFriendRequestsClicked() {
        // todo: moving to the new mates requests screen..


    }

    override fun onChatClicked(chat: Chat) {
        // todo: moving to the chat..

    }
}