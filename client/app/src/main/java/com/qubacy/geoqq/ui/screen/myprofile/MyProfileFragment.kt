package com.qubacy.geoqq.ui.screen.myprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.transition.Fade
import com.google.android.material.transition.MaterialFade
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMyProfileBinding
import com.qubacy.geoqq.ui.MainActivity
import com.qubacy.geoqq.ui.common.component.combobox.adapter.ComboBoxAdapter
import com.qubacy.geoqq.ui.common.component.combobox.view.ComboBoxView.Companion.POSITION_NOT_DEFINED
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState

class MyProfileFragment() : WaitingFragment() {
    companion object {
        const val TAG = "MyProfileFragment"

        const val PRIVACY_HIT_UP_POSITION_KEY = "privacyHitUpPosition"
        const val USER_AVATAR_URI_KEY = "userAvatarUri"
    }

    override val mModel: MyProfileViewModel by viewModels {
        MyProfileViewModelFactory()
    }

    private lateinit var mBinding: FragmentMyProfileBinding

    private lateinit var mPrivacyHitUpAdapter: ArrayAdapter<String>

    private var mPrivacyHitUpPosition: Int = POSITION_NOT_DEFINED
    private var mUserAvatarUri: Uri? = null

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
        mUserAvatarUri = savedInstanceState.getString(USER_AVATAR_URI_KEY)?.toUri()
        val privacyHitUpPosition = savedInstanceState.getInt(PRIVACY_HIT_UP_POSITION_KEY)

        mBinding.userAvatar.setImageURI(mUserAvatarUri)
        changePrivacyHitUpPosition(privacyHitUpPosition)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(USER_AVATAR_URI_KEY, mUserAvatarUri?.toString())
        outState.putInt(PRIVACY_HIT_UP_POSITION_KEY, mPrivacyHitUpPosition)

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

        if (savedInstanceState != null) {
            retrieveSavedInstanceState(savedInstanceState)
        }

        if (mPrivacyHitUpPosition == POSITION_NOT_DEFINED) {
            changePrivacyHitUpPosition(0)
        }

        mModel.myProfileUiState.observe(viewLifecycleOwner) {
            onUiStateChanged(it)
        }

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun initInputsWithUiState(uiState: MyProfileUiState) {
        mBinding.userAvatar.setImageURI(uiState.avatar!!)
        mBinding.usernameInput.input.setText(uiState.username!!)
        mBinding.aboutMeInput.input.setText(uiState.description!!)
        mBinding.passwordInput.input.setText(uiState.password!!)
        mBinding.passwordConfirmationInput.input.setText(uiState.password!!)
        changePrivacyHitUpPosition(uiState.hitUpOption!!.index)
    }

    private fun onUiStateChanged(uiState: MyProfileUiState) {
        if (checkUiStateForErrors(uiState)) return

        initInputsWithUiState(uiState)
    }

    private fun changePrivacyHitUpPosition(newPosition: Int) {
        mBinding.privacyHitUp.currentItemPosition = newPosition
        mPrivacyHitUpPosition = newPosition
    }

    private fun onPrivacyHitUpItemSelected(position: Int) {
        Log.d(TAG, "onPrivacyHitUpItemSelected(): position = $position")

        mPrivacyHitUpPosition = position
    }

    private fun onUploadAvatarButtonClicked() {
        (requireActivity() as MainActivity).pickImage {
            if (it == null) return@pickImage

            Log.d(TAG, "onUploadAvatarButtonClicked(): pickedImgUri: ${it.toString()}")

            mUserAvatarUri = it
            mBinding.userAvatar.setImageURI(it)
        }
    }

    private fun onConfirmButtonClicked() {
        val usernameText = mBinding.usernameInput.input.text.toString()
        val aboutMeText = mBinding.aboutMeInput.input.text.toString()
        val passwordText = mBinding.passwordInput.input.text.toString()
        val passwordConfirmationText = mBinding.passwordConfirmationInput.input.text.toString()
        val hitUpOption = mModel.getHitUpOptionByIndex(mPrivacyHitUpPosition)

        if (!mModel.isProfileDataCorrect(
                usernameText, aboutMeText, passwordText, passwordConfirmationText, hitUpOption
            )
        ) {
            showMessage(R.string.error_my_profile_data_incorrect)

            return
        }

        // todo: conveying data to the model..


    }

    override fun handleWaitingAbort() {
        super.handleWaitingAbort()

        mModel.interruptSavingProfileData()
    }
}