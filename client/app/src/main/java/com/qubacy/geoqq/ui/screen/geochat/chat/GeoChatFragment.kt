package com.qubacy.geoqq.ui.screen.geochat.chat

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import com.google.android.material.transition.MaterialFade
import com.qubacy.geoqq.R
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.ui.common.visual.component.bottomsheet.userinfo.content.UserInfoBottomSheetContentCallback
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.location.LocationFragment
import com.qubacy.geoqq.ui.common.visual.fragment.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.common.visual.fragment.chat.component.list.adapter.ChatAdapterCallback
import com.qubacy.geoqq.ui.common.visual.component.animatedlist.animator.AnimatedListItemAnimator
import com.qubacy.geoqq.ui.common.visual.component.animatedlist.layoutmanager.AnimatedListLayoutManager
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.OpenUserDetailsUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.operation.MateRequestCreatedUiOperation
import com.yandex.mapkit.geometry.Point

class GeoChatFragment(

) : LocationFragment(),
    ChatAdapterCallback,
    UserInfoBottomSheetContentCallback
{
    companion object {
        const val TAG = "GEO_CHAT_FRAGMENT"
    }

    private val mArgs by navArgs<GeoChatFragmentArgs>()

    private lateinit var mBinding: FragmentGeoChatBinding

    private lateinit var mGeoChatAdapter: ChatAdapter

    private var mInitChatRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo: decide what to do with this abrupt animation:
//        enterTransition = Fade().apply {
//            mode = MaterialFade.MODE_IN
//            interpolator = AccelerateDecelerateInterpolator()
//            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
//        }
        returnTransition = Fade().apply {
            mode = MaterialFade.MODE_OUT
            interpolator = AccelerateDecelerateInterpolator()
            duration = resources.getInteger(R.integer.default_transition_duration).toLong()
        }
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

        mBinding.bottomSheet.bottomSheetContentCard.setCallback(this)
        mGeoChatAdapter = ChatAdapter(this)

        mBinding.chatRecyclerView.apply {
            layoutManager = AnimatedListLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mGeoChatAdapter
            itemAnimator = AnimatedListItemAnimator(mGeoChatAdapter)
        }

        mBinding.messageSendingSection.sendingButton.setOnClickListener {
            onSendingMessageButtonClicked()
        }

        (mModel as GeoChatViewModel).geoChatUiStateFlow.value?.let {
            initChat(it)
        }
        (mModel as GeoChatViewModel).geoChatUiStateFlow.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onChatUiStateGotten(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()

            if (mInitChatRequested) getInitChatMessages()
        }
    }

    private fun getInitChatMessages() {
        if ((mModel as GeoChatViewModel).geoChatInitialized) return

        (mModel as GeoChatViewModel).getGeoChat()
    }


    private fun initChat(chatUiState: ChatUiState) {
        mGeoChatAdapter.setItems(chatUiState.messages)
    }

    private fun onChatUiStateGotten(chatUiState: ChatUiState) {
        if (chatUiState.uiOperationCount() <= 0) return

        while (true) {
            val uiOperation = chatUiState.takeUiOperation() ?: break

            processUiOperation(uiOperation, chatUiState)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation, chatUiState: ChatUiState) {
        when (uiOperation::class) {
            SetMessagesUiOperation::class -> {
                initChat(chatUiState)
            }
            AddMessageUiOperation::class -> {
                val addMessageUiOperation = uiOperation as AddMessageUiOperation
                val message = (mModel as GeoChatViewModel).geoChatUiStateFlow.value!!.messages.find {
                    it.id == addMessageUiOperation.messageId
                }!!

                mGeoChatAdapter.addItem(message)
            }
            OpenUserDetailsUiOperation::class -> {
                val openUserDetailsUiOperation = uiOperation as OpenUserDetailsUiOperation
                val user = chatUiState.users.find { it.id == openUserDetailsUiOperation.userId }!!

                processOpenUserDetailsOperation(openUserDetailsUiOperation, user)
            }
            MateRequestCreatedUiOperation::class -> {
                val mateRequestCreatedUiOperation = uiOperation as MateRequestCreatedUiOperation

                showMessage(R.string.chat_mate_request_created_message)
            }
//            AddUserUiOperation::class -> { // todo: is it necessary?
//                val addUserUiOperation = uiOperation as AddUserUiOperation
//
//                // todo: mb some stuff to visualize a new user's entrance..
//
//
//            }
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    private fun processOpenUserDetailsOperation(
        openUserDetailsUiOperation: OpenUserDetailsUiOperation,
        user: User
    ) {
        closeSoftKeyboard()

        mBinding.bottomSheet.bottomSheetContentCard.setData(user)
        mBinding.bottomSheet.bottomSheetContentCard.showPreview()
    }

    private fun onSendingMessageButtonClicked() {
        val messageText = mBinding.messageSendingSection.sendingMessage.text.toString()

        if (!(mModel as GeoChatViewModel).isMessageCorrect(messageText)) {
            showMessage(R.string.error_chat_message_incorrect, 400)

            return
        }

        mBinding.messageSendingSection.sendingMessage.text?.clear()

        (mModel as GeoChatViewModel).sendMessage(messageText)
    }

    override fun onLocationPointChanged(newLocationPoint: Point) {
        // todo: mb it'd be nice to use this somehow in the UI??


    }

    override fun initFlowContainerIfNull() {
        val application = (requireActivity().application as Application)

        if (application.appContainer.geoChatContainer != null) return

        application.appContainer.initGeoChatContainer(
            mArgs.radius,
            application.appContainer.errorDataRepository,
            application.appContainer.tokenDataRepository,
            application.appContainer.geoMessageDataRepository,
            application.appContainer.imageDataRepository,
            application.appContainer.userDataRepository,
            application.appContainer.mateRequestDataRepository
        )

        mModel = application.appContainer.geoChatContainer!!
            .geoChatViewModelFactory
            .create(GeoChatViewModel::class.java)
    }

    override fun clearFlowContainer() {
        (requireActivity().application as Application).appContainer.clearGeoChatContainer()
    }

    override fun getUserById(userId: Long): User {
        return (mModel as GeoChatViewModel).geoChatUiStateFlow.value!!.users.find {
            it.id== userId
        }!!
    }

    override fun onMessageClicked(message: Message) {
        if ((mModel as GeoChatViewModel).isLocalUser(message.userId)) return

        closeSoftKeyboard()

        (mModel as GeoChatViewModel).getUserDetails(message.userId)
    }

    override fun addToMates(user: User) {
        (mModel as GeoChatViewModel).createMateRequest(user.id)
    }

    override fun getPermissionsToRequest(): Array<String>? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return (arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).plus(super.getPermissionsToRequest() ?: arrayOf()))

        return super.getPermissionsToRequest()
    }

    override fun onRequestedPermissionsGranted() {
        super.onRequestedPermissionsGranted()

        if (view == null) {
            mInitChatRequested = true

            return
        }

        getInitChatMessages()
    }

    override fun handleWaitingAbort() {
        if ((mModel as GeoChatViewModel).isGettingChat) return

        super.handleWaitingAbort()
    }
}