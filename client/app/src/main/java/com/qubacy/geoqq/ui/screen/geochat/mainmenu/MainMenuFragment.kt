package com.qubacy.geoqq.ui.screen.geochat.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.qubacy.geoqq.databinding.FragmentMainMenuBinding
import com.qubacy.geoqq.ui.common.fragment.BaseFragment

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
        // todo: calling an appropriate method of the model..


    }

    private fun onMatesMenuOptionClicked() {
        // todo: calling an appropriate method of the model..


    }

    private fun onMyProfileMenuOptionClicked() {
        // todo: calling an appropriate method of the model..


    }
}