package com.qubacy.geoqq.ui.screen.geochat.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMainMenuBinding

class MainMenuFragment() : Fragment() {
    private lateinit var mBinding: FragmentMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMainMenuBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.goMenuOption.menuOptionButton.setOnClickListener {
            onGoMenuOptionClicked()
        }
        mBinding.matesMenuOption.menuOptionButton.setOnClickListener {
            onMatesMenuOptionClicked()
        }
        mBinding.profileMenuOption.menuOptionButton.setOnClickListener {
            onMyProfileMenuOptionClicked()
        }
    }

    private fun onGoMenuOptionClicked() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_geoChatSettingsFragment)
    }

    private fun onMatesMenuOptionClicked() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_mateChatsFragment)
    }

    private fun onMyProfileMenuOptionClicked() {
        findNavController().navigate(R.id.action_mainMenuFragment_to_myProfileFragment)
    }
}