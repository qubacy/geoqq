package com.qubacy.geoqq.ui.screen.mate.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModelFactory

class MateChatsFragment() : WaitingFragment() {
    override val mModel: MateChatsViewModel by viewModels {
        MateChatsViewModelFactory()
    }

    private lateinit var mBinding: FragmentMateChatsBinding

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

        mBinding.friendRequestsCardButton.setOnClickListener {
            onFriendRequestsClicked()
        }
    }

    private fun onFriendRequestsClicked() {
        // todo: moving to the new mates requests screen..


    }

    override fun handleError(error: Error) {
        TODO("Not yet implemented")
    }

}