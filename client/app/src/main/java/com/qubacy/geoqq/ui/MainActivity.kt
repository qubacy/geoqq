package com.qubacy.geoqq.ui

import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import com.qubacy.geoqq.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)


    }

    fun changeStatusBarColor(@ColorRes colorResId: Int) {
        window.statusBarColor = resources.getColor(colorResId)
    }
}