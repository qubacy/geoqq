package com.qubacy.geoqq.ui.screen.mate.request.list.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.carousel3dlib.adapter.Carousel3DViewHolder
import com.example.carousel3dlib.view.Carousel3DOpenableView
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.ComponentMateRequestBinding
import com.qubacy.geoqq.databinding.ComponentMateRequestOpenableBinding
import com.qubacy.geoqq.domain.common.model.user.User

class MateRequestViewHolder(val binding: ComponentMateRequestBinding)
    : Carousel3DViewHolder(binding.root)
{
    private var lastProgressValue = 0f

    fun bind(item: User) {
        binding.avatar.setImageURI(item.avatarUri)
        binding.username.text = item.username
        binding.description.text = item.description
    }

    override fun getOpenableView(root: ViewGroup, topMarginPx: Int): Carousel3DOpenableView {
        val layoutInflater = LayoutInflater.from(binding.root.context)
        val openableViewBinding = ComponentMateRequestOpenableBinding.inflate(layoutInflater, root, true)

        recycleOpenableView(openableViewBinding.root, topMarginPx)

        openableViewBinding.root.apply {
            isClickable = true

            setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {}

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
                    if (progress in 0.70..0.80) {
                        if (progress > lastProgressValue
                            && openableViewBinding.mateRequestOpenableContent.description
                                .ellipsize == TextUtils.TruncateAt.END
                        ) {

                            openableViewBinding.mateRequestOpenableContent.description.ellipsize =
                                null
                            openableViewBinding.mateRequestOpenableContent.description.maxLines =
                                Int.MAX_VALUE

                        } else if (progress < lastProgressValue
                            && openableViewBinding.mateRequestOpenableContent.description
                                .ellipsize == null
                        ) {
                            openableViewBinding.mateRequestOpenableContent.description.ellipsize =
                                TextUtils.TruncateAt.END
                            openableViewBinding.mateRequestOpenableContent.description.maxLines =
                                1
                        }
                    }

                    lastProgressValue = progress
                }

                override fun onTransitionCompleted(
                    motionLayout: MotionLayout?,
                    currentId: Int
                ) { }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) { }
            })
        }

        return openableViewBinding.root
    }

    override fun recycleOpenableView(openableView: Carousel3DOpenableView, topMarginPx: Int) {
        val openableViewBinding = ComponentMateRequestOpenableBinding.bind(openableView)

        openableViewBinding.mateRequestOpenableContent.apply {
            avatar.setImageDrawable(binding.avatar.drawable)
            username.text = binding.username.text
            description.text = binding.description.text
        }

        setStartTopMarginForOpenableViewScene(openableViewBinding, topMarginPx)
    }

    private fun setStartTopMarginForOpenableViewScene(
        openableViewBinding: ComponentMateRequestOpenableBinding,
        topMarginPx: Int
    ) {
        openableViewBinding.root.apply {
            scene.getConstraintSet(
                context,
                context.resources.getResourceName(R.id.mates_request_card_container_closed_state)
            )
                .getConstraint(R.id.mate_request_openable_content).layout.topMargin =
                topMarginPx
        }
    }
}
