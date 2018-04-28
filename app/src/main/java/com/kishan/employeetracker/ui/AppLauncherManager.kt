package com.kishan.employeetracker.ui

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by Kishan P Rao on 25/04/18.
 */
class AppLauncherManager {
	companion object {
		const val DISABLE = false
		
		private fun applySettings(context: Context, className: String, newState: Int, flags: Int) {
//			if (DISABLE) return
			val componentName = ComponentName(context.packageName, className)
			context.packageManager.setComponentEnabledSetting(componentName, newState, flags)
		}
		
		fun disable(context: Context, className: String) {
//			if (DISABLE) return
			this.applySettings(context, context.packageName + className,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP)
		}
		
		fun enable(context: Context, className: String) {
//			if (DISABLE) return
			this.applySettings(context, context.packageName + className,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					0)
//					PackageManager.DONT_KILL_APP)
		}
	}
}