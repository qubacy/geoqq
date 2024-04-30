package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat

import android.graphics.Shader
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentGeoChatBinding
import com.qubacy.geoqq.ui._common.tile.TileDrawable
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.animator.MessageItemAnimator
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.error.type.UiChatErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.model.operation.MateRequestSentToInterlocutorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.model.operation.MessageSentUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.util.listener.LocationListener
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.util.listener.LocationListenerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.MateableFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo._common.error.type.UiGeoErrorType
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.GeoMessageListAdapter
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.adapter.GeoMessageListAdapterCallback
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.GeoChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.operation.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.state.GeoChatUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation.toGeoMessageItemData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeoChatFragment(

) : BusinessFragment<FragmentGeoChatBinding, GeoChatUiState, GeoChatViewModel>(),
    PermissionRunnerCallback,
    LocationListenerCallback,
    GeoMessageListAdapterCallback,
    UserBottomSheetViewContainerCallback,
    MateableFragment
{
    companion object {
        const val TAG = "GeoChatFragment"
    }

    private val mArgs: GeoChatFragmentArgs by navArgs()

    @Inject
    @GeoChatViewModelFactoryQualifier
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val mModel: GeoChatViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var mAdapter: GeoMessageListAdapter

    private lateinit var mPermissionRunner: PermissionRunner<GeoChatFragment>
    private lateinit var mLocationListener: LocationListener

    private var mInterlocutorDetailsSheet: UserBottomSheetViewContainer? = null
    private var mLastWindowInsets: WindowInsetsCompat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLocationContext() // it's important to run BEFORE super.onStart() & onPermissionGranted();
        initLocationListener()
        initPermissionRunner()

        mSnackbarAnchorView = mBinding.fragmentGeoChatInputMessageWrapper

        initMessageListView()
        initUiControls()
    }

    override fun onStart() {
        super.onStart()

        if (!mPermissionRunner.isRequestingPermissions || mPermissionRunner.arePermissionsGranted)
            mLocationListener.startLocationListening(requireActivity())
    }

    override fun onStop() {
        super.onStop()

        mInterlocutorDetailsSheet?.close()
    }

    private fun initLocationListener() {
        mLocationListener = LocationListener(requireContext(), this)
    }

    private fun initPermissionRunner() {
        mPermissionRunner = PermissionRunner(this).also {
            it.requestPermissions(true)
        }
    }

    override fun retrieveToolbar(): MaterialToolbar {
        return mBinding.fragmentGeoChatTopBar
    }

    override fun getFragmentDestinationId(): Int {
        return R.id.geoChatFragment
    }

    override fun getFragmentTitle(): String {
        return getString(R.string.fragment_geo_chat_top_bar_title_text)
    }

    override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            AddGeoMessagesUiOperation::class ->
                processAddGeoMessagesUiOperation(uiOperation as AddGeoMessagesUiOperation)
            ShowInterlocutorDetailsUiOperation::class ->
                processShowInterlocutorDetailsUiOperation(
                    uiOperation as ShowInterlocutorDetailsUiOperation)
            UpdateInterlocutorDetailsUiOperation::class ->
                processUpdateInterlocutorDetailsUiOperation(
                    uiOperation as UpdateInterlocutorDetailsUiOperation)
            MateRequestSentToInterlocutorUiOperation::class ->
                processMateRequestSentToInterlocutorUiOperation(
                    uiOperation as MateRequestSentToInterlocutorUiOperation)
            MessageSentUiOperation::class ->
                processMessageSentUiOperation(uiOperation as MessageSentUiOperation)
            else -> return false
        }

        return true
    }

    private fun processAddGeoMessagesUiOperation(
        addGeoMessagesUiOperation: AddGeoMessagesUiOperation
    ) {
        val localUserId = mModel.getLocalUserId()
        val geoMessageItems = addGeoMessagesUiOperation.messages
            .map { it.toGeoMessageItemData(localUserId) }

        mAdapter.addMessages(geoMessageItems)
    }

    private fun processShowInterlocutorDetailsUiOperation(
        showInterlocutorDetailsUiOperation: ShowInterlocutorDetailsUiOperation
    ) {
        openInterlocutorDetailsSheet(showInterlocutorDetailsUiOperation.interlocutor)
    }

    private fun processUpdateInterlocutorDetailsUiOperation(
        updateInterlocutorDetailsUiOperation: UpdateInterlocutorDetailsUiOperation
    ) {
        adjustUiWithInterlocutor(updateInterlocutorDetailsUiOperation.interlocutor)
    }

    private fun processMateRequestSentToInterlocutorUiOperation(
        mateRequestSentToInterlocutorUiOperation: MateRequestSentToInterlocutorUiOperation
    ) {
        onPopupMessageOccurred(R.string.fragment_mate_chat_snackbar_message_mate_request_sent)
        mInterlocutorDetailsSheet!!.setMateButtonEnabled(false)
    }

    private fun processMessageSentUiOperation(messageSentUiOperation: MessageSentUiOperation) {
        //onPopupMessageOccurred(R.string.fragment_mate_chat_snackbar_message_message_sent) // not a nice thing actually;
    }

    override fun runInitWithUiState(uiState: GeoChatUiState) {
        super.runInitWithUiState(uiState)

        initUiWithUiState(uiState)
    }

    private fun initLocationContext() {
        if (!mModel.isLocationContextSet())
            mModel.setLocationContext(mArgs.radius, mArgs.longitude, mArgs.latitude)
    }

    private fun initUiWithUiState(uiState: GeoChatUiState) {

    }

    private fun initMessageListView() {
        mAdapter = GeoMessageListAdapter(geoCallback = this)

        mBinding.fragmentGeoChatList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, true
            )
            adapter = mAdapter
            itemAnimator = MessageItemAnimator()
        }

        setupMessageListBackground()
    }

    private fun setupMessageListBackground() {
        val background = ContextCompat.getDrawable(
            requireContext(), R.drawable.chat_background_pattern)!!
        val tileBackground = TileDrawable(background, Shader.TileMode.REPEAT)

        mBinding.fragmentGeoChatList.background = tileBackground
    }

    private fun initUiControls() {
        // todo: implement..
        mBinding.fragmentGeoChatInputMessage.setOnKeyListener { _, keyCode, event ->
            onMessageInputKeyPressed(keyCode, event)
        }
    }

    private fun onMessageInputKeyPressed(keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
            launchSendingMessage()

            return true
        }

        return false
    }

    override fun viewInsetsToCatch(): Int {
        return super.viewInsetsToCatch() or WindowInsetsCompat.Type.ime()
    }

    override fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) {
        super.adjustViewToInsets(insets, insetsRes)

        mLastWindowInsets = insetsRes

        mBinding.fragmentGeoChatTopBarWrapper.apply {
            updatePadding(top = insets.top)
        }
        mBinding.fragmentGeoChatInputMessageWrapper.apply {
            updatePadding(bottom = insets.bottom)
        }
        mBinding.fragmentGeoChatList.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                this@updateLayoutParams.bottomMargin =
                    mBinding.fragmentGeoChatInputMessageWrapper.measuredHeight
            }
        }

        mInterlocutorDetailsSheet?.apply {
            adjustToInsets(insetsRes)
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGeoChatBinding {
        return FragmentGeoChatBinding.inflate(inflater)
    }

    override fun getPermissionsToRequest(): Array<String> {
        return arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestedPermissionsGranted() {
        mLocationListener.startLocationListening(requireActivity())

        initGeoChat()
    }

    private fun initGeoChat() {
        if (mModel.areMessagesLoaded()) resetMessages()

        mModel.getMessages()
    }

    private fun resetMessages() {
        mAdapter.resetItems()
        mModel.resetMessages()
    }

    private fun adjustUiWithInterlocutor(interlocutor: UserPresentation) {
        setupInterlocutorDetailsSheet(interlocutor)
    }

    override fun onMateButtonClicked(userId: Long) {
        launchAddInterlocutorAsMate(userId)
        mInterlocutorDetailsSheet!!.close()
    }

    private fun launchAddInterlocutorAsMate(userId: Long) {
        mModel.addInterlocutorAsMate(userId)
    }

    private fun launchSendingMessage() {
        val messageText = mBinding.fragmentGeoChatInputMessage.text.toString()

        if (!mModel.isMessageTextValid(messageText))
            return mModel.retrieveError(UiChatErrorType.INVALID_MESSAGE)

        mBinding.fragmentGeoChatInputMessage.text!!.clear()

        mModel.sendMessage(messageText)
    }

    override fun getInterlocutorDetailsSheet(): UserBottomSheetViewContainer? {
        return mInterlocutorDetailsSheet
    }

    override fun isInterlocutorDetailsMateButtonEnabled(
        interlocutor: UserPresentation
    ): Boolean {
        return !interlocutor.isMate
    }

    override fun isInterlocutorDetailsMateButtonVisible(interlocutor: UserPresentation): Boolean {
        return !interlocutor.isMate
    }

    override fun initInterlocutorDetailsSheet() {
        mInterlocutorDetailsSheet = createInterlocutorDetailsSheet()
    }

    override fun getInterlocutorDetailsSheetExpandedHeight(): Int {
        val topPosition = mBinding.fragmentGeoChatTopBarWrapper.bottom
        val bottomPosition = mBinding.root.bottom

        return bottomPosition - topPosition
    }

    override fun getInterlocutorDetailsSheetParent(): CoordinatorLayout {
        return mBinding.root
    }

    override fun getInterlocutorDetailsSheetCallback(): UserBottomSheetViewContainerCallback {
        return this
    }

    override fun getInterlocutorDetailsSheetInsets(): WindowInsetsCompat? {
        return mLastWindowInsets
    }

    override fun adjustUiWithLoadingState(isLoading: Boolean) {
        changeLoadingIndicatorState(isLoading)
    }

    private fun changeLoadingIndicatorState(isVisible: Boolean) {
        mBinding.fragmentGeoChatProgressBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onGeoMessageClicked(position: Int) {
        mModel.getUserProfileByMessagePosition(position)
    }

    override fun onNewLocationGotten(location: Location?) {
        if (location == null) return

        Log.d(TAG, "onNewLocationGotten(): location = ${location.latitude}:${location.longitude};")

        mModel.changeLastLocation(location)
    }

    override fun onLocationServicesNotEnabled() {
        mModel.retrieveError(UiGeoErrorType.LOCATION_SERVICES_UNAVAILABLE)
    }

    override fun onRequestingLocationUpdatesFailed(exception: Exception) {
        mModel.retrieveError(UiGeoErrorType.LOCATION_REQUEST_FAILED)
    }
}