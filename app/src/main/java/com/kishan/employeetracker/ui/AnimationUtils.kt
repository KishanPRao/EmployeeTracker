package com.kishan.employeetracker.ui

import android.support.v4.view.animation.PathInterpolatorCompat
import android.view.animation.Interpolator

/**
 * Created by Kishan P Rao on 10/04/18.
 */
class AnimationUtils {
	companion object {
		const val ANIM_TIME = 2 * 1000
		
		val INTERPOLATOR = createPathInterpolator(0.2f, 0.0f, 0.0f, 1.0f)
		
		private fun createPathInterpolator(controlX1: Float,
		                                   controlY1: Float,
		                                   controlX2: Float,
		                                   controlY2: Float): Interpolator {
			return PathInterpolatorCompat.create(controlX1, controlY1, controlX2, controlY2)
		}
	}
}