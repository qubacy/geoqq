package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.message.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.runPermissionCheck
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension.setupNavigationUI
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.adapter.MateMessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.component.list.item.data.MateMessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.MateChatsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MateChatFragment : BaseFragment<FragmentMateChatBinding>(), PermissionRunnerCallback {
    private lateinit var mAdapter: MateMessageListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runPermissionCheck<MateChatsFragment>()
        setupNavigationUI(mBinding.fragmentMateChatTopBar)

        initMessageListView()
    }

    override fun onStart() {
        super.onStart()

        // todo: delete:
        mAdapter.setMateMessages(listOf(
            MateMessageItemData(0, SenderSide.ME, "hi", "5:00 AM"),
            MateMessageItemData(1, SenderSide.OTHER, "qq", "5:01 AM"),
            MateMessageItemData(2, SenderSide.ME, "lets start", "5:02 AM"),
        ))
    }

    private fun initMessageListView() {
        mAdapter = MateMessageListAdapter()

        mBinding.fragmentMateChatList.apply {
            adapter = mAdapter
        }
    }

    override fun viewInsetsToCatch(): Int {
        return super.viewInsetsToCatch() or WindowInsetsCompat.Type.ime()
    }

    override fun adjustViewToInsets(insets: Insets) {
        super.adjustViewToInsets(insets)

        mBinding.fragmentMateChatTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentMateInputMessageWrapper.apply {
            updatePadding(bottom = insets.bottom)
        }
    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMateChatBinding {
        return FragmentMateChatBinding.inflate(inflater)
    }

    override fun getPermissionsToRequest(): Array<String> {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}