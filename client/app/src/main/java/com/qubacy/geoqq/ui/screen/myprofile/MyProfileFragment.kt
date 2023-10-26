package com.qubacy.geoqq.ui.screen.myprofile

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.transition.Fade
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialFade
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext.CURRENT_PASSWORD_TEXT_KEY
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext.DESCRIPTION_TEXT_KEY
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext.NEW_PASSWORD_TEXT_KEY
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext.PRIVACY_HIT_UP_POSITION_KEY
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext.REPEAT_NEW_PASSWORD_TEXT_KEY
import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext.USER_AVATAR_URI_KEY
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.MainActivity
import com.qubacy.geoqq.ui.common.component.combobox.adapter.ComboBoxAdapter
import com.qubacy.geoqq.ui.common.component.combobox.view.ComboBoxView.Companion.POSITION_NOT_DEFINED
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory
import com.qubacy.geoqq.ui.screen.myprofile.model.operation.ProfileDataSavedUiOperation
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState

class MyProfileFragment() : WaitingFragment() {
    companion object {
        const val TAG = "MyProfileFragment"

    }

    override val mModel: MyProfileViewModel by viewModels {
        MyProfileViewModelFactory()
    }

    private lateinit var mBinding: FragmentMyProfileBinding

    private lateinit var mPrivacyHitUpAdapter: ArrayAdapter<String>

    private var mChangedInputHash = HashMap<String, Any>()
    private var mIsInitInputs = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionWindowBackgroundDrawableResId(R.drawable.my_profile_background)

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
    }

    private fun retrieveSavedInstanceState(savedInstanceState: Bundle) {
        mIsInitInputs = true

        savedInstanceState.getString(USER_AVATAR_URI_KEY)?.let {
            mChangedInputHash[USER_AVATAR_URI_KEY] = it.toUri()

            setUserAvatarWithUri(it.toUri())
        }
        savedInstanceState.getString(DESCRIPTION_TEXT_KEY)?.let {
            mChangedInputHash[DESCRIPTION_TEXT_KEY] = it

            highlightChangedTextInputLayout(mBinding.aboutMeInput.inputLayout)
            mBinding.aboutMeInput.input.setText(it)
        }

        savedInstanceState.getString(CURRENT_PASSWORD_TEXT_KEY)?.let {
            mChangedInputHash[CURRENT_PASSWORD_TEXT_KEY] = it

            highlightChangedTextInputLayout(mBinding.currentPasswordInput.inputLayout)
            mBinding.currentPasswordInput.input.setText(it)
        }
        savedInstanceState.getString(NEW_PASSWORD_TEXT_KEY)?.let {
            mChangedInputHash[NEW_PASSWORD_TEXT_KEY] = it

            highlightChangedTextInputLayout(mBinding.newPasswordInput.inputLayout)
            mBinding.newPasswordInput.input.setText(it)
        }
        savedInstanceState.getString(REPEAT_NEW_PASSWORD_TEXT_KEY)?.let {
            mChangedInputHash[REPEAT_NEW_PASSWORD_TEXT_KEY] = it

            highlightChangedTextInputLayout(mBinding.newPasswordConfirmationInput.inputLayout)
            mBinding.newPasswordConfirmationInput.input.setText(it)
        }

        val hitUpOption = savedInstanceState.getInt(
            PRIVACY_HIT_UP_POSITION_KEY, POSITION_NOT_DEFINED)

        if (hitUpOption != POSITION_NOT_DEFINED) {
            mChangedInputHash[PRIVACY_HIT_UP_POSITION_KEY] = hitUpOption

            highlightChangedTextInputLayout(mBinding.privacyHitUpLayout)
            changePrivacyHitUpPosition(hitUpOption)
        }

        mIsInitInputs = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mChangedInputHash[USER_AVATAR_URI_KEY]?.let {
            outState.putString(USER_AVATAR_URI_KEY, (it as Uri).toString())
        }
        mChangedInputHash[DESCRIPTION_TEXT_KEY]?.let {
            outState.putString(DESCRIPTION_TEXT_KEY, it as String)
        }

        mChangedInputHash[CURRENT_PASSWORD_TEXT_KEY]?.let {
            outState.putString(CURRENT_PASSWORD_TEXT_KEY, it as String)
        }
        mChangedInputHash[NEW_PASSWORD_TEXT_KEY]?.let {
            outState.putString(NEW_PASSWORD_TEXT_KEY, it as String)
        }
        mChangedInputHash[REPEAT_NEW_PASSWORD_TEXT_KEY]?.let {
            outState.putString(REPEAT_NEW_PASSWORD_TEXT_KEY, it as String)
        }

        mChangedInputHash[PRIVACY_HIT_UP_POSITION_KEY]?.let {
            outState.putInt(PRIVACY_HIT_UP_POSITION_KEY, it as Int)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_my_profile,
            container,
            false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.uploadAvatarButton.setOnClickListener {
            onUploadAvatarButtonClicked()
        }

        mBinding.usernameInput.input.apply {
            inputType = InputType.TYPE_NULL

            setTextIsSelectable(true)
        }
        mBinding.aboutMeInput.input.apply {
            isSaveEnabled = false
            isSaveFromParentEnabled = false

            addTextChangedListener {
                if (mIsInitInputs) return@addTextChangedListener

                onDescriptionChanged(it)
            }
        }
        mBinding.currentPasswordInput.input.apply {
            isSaveEnabled = false
            isSaveFromParentEnabled = false

            addTextChangedListener {
                if (mIsInitInputs) return@addTextChangedListener

                onCurrentPasswordChanged(it)
            }
        }
        mBinding.newPasswordInput.input.apply {
            isSaveEnabled = false
            isSaveFromParentEnabled = false

            addTextChangedListener {
                if (mIsInitInputs) return@addTextChangedListener

                onNewPasswordChanged(it)
            }
        }
        mBinding.newPasswordConfirmationInput.input.apply {
            isSaveEnabled = false
            isSaveFromParentEnabled = false

            addTextChangedListener {
                if (mIsInitInputs) return@addTextChangedListener

                onRepeatNewPasswordChanged(it)
            }
        }
        mBinding.confirmButton.setOnClickListener {
            onConfirmButtonClicked()
        }

        mPrivacyHitUpAdapter = ComboBoxAdapter(
            requireContext(),
            R.layout.component_large_autofill_input_dropdown_option,
            resources.getStringArray(R.array.hit_up_variants))

        mBinding.privacyHitUp.apply {
            setDropDownBackgroundResource(R.drawable.component_autofill_input_dropdown_background)
            onItemClickListener =
                OnItemClickListener {
                        parent, view, position, id -> onPrivacyHitUpItemSelected(position)
                }

            setAdapter(mPrivacyHitUpAdapter)
        }

        mModel.myProfileUiState.value?.let {
            if (!it.isFull()) return@let

            initInputsWithUiState(it)
        }
        mModel.myProfileUiState.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onUiStateChanged(it)
        }

        if (savedInstanceState != null) {
            retrieveSavedInstanceState(savedInstanceState)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun initInputsWithUiState(uiState: MyProfileUiState) {
        mIsInitInputs = true

        uiState.avatar?.apply { setUserAvatarWithUri(this@apply) }

        mBinding.usernameInput.input.setText(uiState.username!!)

        if (mChangedInputHash[DESCRIPTION_TEXT_KEY] == null) {
            mBinding.aboutMeInput.input.setText(uiState.description!!)
        }

        if (mChangedInputHash[PRIVACY_HIT_UP_POSITION_KEY] == null) {
            uiState.hitUpOption?.let {
                changePrivacyHitUpPosition(it.index)
            }
        }

        mIsInitInputs = false
    }

    private fun onUiStateChanged(uiState: MyProfileUiState) {
        if (uiState.isFull()) initInputsWithUiState(uiState)
        if (uiState.newUiOperations.isEmpty()) return

        for (uiOperation in uiState.newUiOperations) {
            processUiOperation(uiOperation)
        }
    }

    private fun processUiOperation(uiOperation: UiOperation) {
        when (uiOperation::class) {
            ProfileDataSavedUiOperation::class -> {
                val profileDataSavedUiOperation = uiOperation as ProfileDataSavedUiOperation

                onProfileSaved(profileDataSavedUiOperation)
            }
            ShowErrorUiOperation::class -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    private fun onDescriptionChanged(description: Editable?) {
        onTextInputContentChanged(
            description,
            DESCRIPTION_TEXT_KEY,
            mModel.myProfileUiState.value!!.description!!,
            mBinding.aboutMeInput.inputLayout
        )
    }

    private fun onCurrentPasswordChanged(password: Editable?) {
        onTextInputContentChanged(
            password,
            CURRENT_PASSWORD_TEXT_KEY,
            String(),
            mBinding.currentPasswordInput.inputLayout
        )
    }

    private fun onNewPasswordChanged(newPassword: Editable?) {
        onTextInputContentChanged(
            newPassword,
            NEW_PASSWORD_TEXT_KEY,
            String(),
            mBinding.newPasswordInput.inputLayout
        )
    }

    private fun onRepeatNewPasswordChanged(repeatNewPassword: Editable?) {
        onTextInputContentChanged(
            repeatNewPassword,
            REPEAT_NEW_PASSWORD_TEXT_KEY,
            String(),
            mBinding.newPasswordConfirmationInput.inputLayout
        )
    }

    private fun onTextInputContentChanged(
        newValue: Editable?,
        hashKey: String,
        prevValue: String?,
        textInputLayout: TextInputLayout
    ) {
        val curValue = newValue?.toString() ?: String()

        Log.d(TAG, "onTextInputContentChanged(): key: $hashKey; curValue: $curValue; prevValue: $prevValue")

        if (prevValue == curValue) {
            mChangedInputHash.remove(hashKey)

            hideTextInputLayoutOutline(textInputLayout)
        }
        else {
            mChangedInputHash[hashKey] = curValue

            highlightChangedTextInputLayout(textInputLayout)
        }
    }

    private fun highlightChangedTextInputLayout(textInputLayout: TextInputLayout) {
        setTextInputLayoutOutline(textInputLayout, true)
    }

    private fun hideTextInputLayoutOutline(textInputLayout: TextInputLayout) {
        setTextInputLayoutOutline(textInputLayout, false)
    }

    private fun setTextInputLayoutOutline(
        textInputLayout: TextInputLayout,
        isHighlighted: Boolean
    ) {
        if (!isHighlighted) {
            textInputLayout.apply {
                boxStrokeWidth = 0

                requestLayout()
            }

            return
        }

        textInputLayout.apply {
            boxStrokeWidth =
                resources.getDimension(R.dimen.text_input_layout_changed_stroke_width).toInt()

            requestLayout()
        }
    }

    private fun onProfileSaved(profileDataSavedUiOperation: ProfileDataSavedUiOperation) {
        showMessage(R.string.profile_data_saved)
    }

    private fun changePrivacyHitUpPosition(newPosition: Int) {
        mBinding.privacyHitUp.currentItemPosition = newPosition
    }

    private fun onPrivacyHitUpItemSelected(position: Int) {
        Log.d(TAG, "onPrivacyHitUpItemSelected(): position = $position")

        val curOption = mModel.getHitUpOptionByIndex(position)
        val prevOption = mModel.myProfileUiState.value!!.hitUpOption!!

        if (curOption == prevOption) {
            mChangedInputHash.remove(PRIVACY_HIT_UP_POSITION_KEY)

            hideTextInputLayoutOutline(mBinding.privacyHitUpLayout)
        } else {
            mChangedInputHash[PRIVACY_HIT_UP_POSITION_KEY] = position

            highlightChangedTextInputLayout(mBinding.privacyHitUpLayout)
        }
    }

    private fun onUploadAvatarButtonClicked() {
        (requireActivity() as MainActivity).pickImage {
            if (it == null) return@pickImage

            Log.d(TAG, "onUploadAvatarButtonClicked(): pickedImgUri: ${it.toString()}")

            setUserAvatarWithUri(it)
        }
    }

    private fun setUserAvatarWithUri(avatarUri: Uri) {
        mChangedInputHash[USER_AVATAR_URI_KEY] = avatarUri

        val avatarInputStream = requireContext().contentResolver.openInputStream(avatarUri)
        val avatarDrawable = Drawable.createFromStream(avatarInputStream, String())

        mBinding.userAvatar.setImageDrawable(avatarDrawable)
    }

    private fun onConfirmButtonClicked() {
        if (mChangedInputHash.isEmpty()) return

        if (!mModel.isChangedProfileDataCorrect(mChangedInputHash)) {
            showMessage(R.string.error_my_profile_data_incorrect)

            return
        }

        mModel.saveProfileData(mChangedInputHash)
    }

    override fun handleWaitingAbort() {
        super.handleWaitingAbort()

        mModel.interruptSavingProfileData()
    }
}