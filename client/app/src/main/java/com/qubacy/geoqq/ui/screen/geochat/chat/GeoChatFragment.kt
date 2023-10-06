package com.qubacy.geoqq.ui.screen.geochat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.ui.common.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModelFactory
import com.yandex.mapkit.geometry.Point

class GeoChatFragment() : LocationFragment() {
    override val mModel: GeoChatViewModel by viewModels {
        GeoChatViewModelFactory()
    }

    private lateinit var mBinding: FragmentGeoChatBinding

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
            R.layout.fragment_geo_chat,
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
            showMessage(R.string.error_geo_chat_message_empty, 400)

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

    override fun onLocationPointChanged(newLocationPoint: Point) {
        // todo: mb it'd be nice to use this somehow in the UI??


    }

    override fun handleError(error: Error) {
        // todo: handling the error..


    }
}