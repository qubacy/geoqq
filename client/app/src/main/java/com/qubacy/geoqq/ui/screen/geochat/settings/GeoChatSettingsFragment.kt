package com.qubacy.geoqq.ui.screen.geochat.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentRadiusSettingOptionBinding
import com.qubacy.geoqq.databinding.FragmentGeoChatSettingsBinding
import com.qubacy.geoqq.ui.common.fragment.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModelFactory
import com.yandex.mapkit.MapKitFactory

class GeoChatSettingsFragment : WaitingFragment() {
    override val mModel: GeoChatSettingsViewModel by viewModels {
        GeoChatSettingsViewModelFactory()
    }

    private lateinit var mBinding: FragmentGeoChatSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(requireContext())

        // todo: WORK WITH RUNTIME PERMISSIONS TO OBTAIN A LOCATION!
    }

    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()
        mBinding.map.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        mBinding.map.onStop()

        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentGeoChatSettingsBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.radiusSetting250m.radioButton.setOnClickListener {
            changeRadiusChoice(R.id.radius_setting_250m)
        }
        mBinding.radiusSetting500m.radioButton.setOnClickListener {
            changeRadiusChoice(R.id.radius_setting_500m)
        }
        mBinding.radiusSetting1km.radioButton.setOnClickListener {
            changeRadiusChoice(R.id.radius_setting_1km)
        }
        mBinding.radiusSetting3km.radioButton.setOnClickListener {
            changeRadiusChoice(R.id.radius_setting_3km)
        }
        mBinding.radiusSetting10km.radioButton.setOnClickListener {
            changeRadiusChoice(R.id.radius_setting_10km)
        }
        mBinding.goButton.setOnClickListener { onGoClicked() }
    }

    private fun changeRadiusChoice(@IdRes radiusChoiceId: Int) {
        for (radiusVariantView in mBinding.radiusSettings.children) {
            val curRadiusVariantBinding =
                DataBindingUtil.getBinding<ComponentRadiusSettingOptionBinding>(radiusVariantView)

            if (curRadiusVariantBinding == null) return

            if (curRadiusVariantBinding.radiusSettingOptionIsChecked == true) {
                if (radiusChoiceId == radiusVariantView.id) return
                else curRadiusVariantBinding.radiusSettingOptionIsChecked = false

            } else if (radiusChoiceId == radiusVariantView.id) {
                curRadiusVariantBinding.radiusSettingOptionIsChecked =  true
            }
        }
    }

    private fun onGoClicked() {
//        handleWaitingStart() // there's no reason to do it manually. the model should change isWaiting
                               // value that has to lead to calling the method;

        // todo: conveying a signal to the model..


    }
}