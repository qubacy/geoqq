package com.qubacy.geoqq.ui.screen.mate.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui.common.fragment.BaseFragment
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModelFactory

class MateChatFragment() : BaseFragment() {
    override val mModel: MateChatViewModel by viewModels {
        MateChatViewModelFactory()
    }

    private lateinit var mBinding: FragmentMateChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_mate_chat,
            container,
            false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.messageSendingSection.sendingButton.setOnClickListener {
            onSendingMessageButtonClicked()
        }
    }

    private fun onSendingMessageButtonClicked() {
        if (isCurrentMessageNull()) {
            showMessage(R.string.error_chat_message_empty, 400)

            return
        }

        val messageText = mBinding.messageSendingSection.sendingMessage.text.toString()

        mBinding.messageSendingSection.sendingMessage.text?.clear()

        // todo: providing the composed message to the model..


    }

    private fun isCurrentMessageNull(): Boolean {
        val messageText = mBinding.messageSendingSection.sendingMessage.text.toString()

        return (messageText.isEmpty())
    }

    override fun handleError(error: Error) {
        TODO("Not yet implemented")
    }
}