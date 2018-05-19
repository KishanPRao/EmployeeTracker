package com.kishan.employeetracker.extension

/**
 * Created by Kishan P Rao on 19/05/18.
 */
class IntegerExtension {
	//	TODO: Make it across all modules.
	companion object {
		val Int.clampNegative: Int get() = if (this < 0) 0 else this
	}
}