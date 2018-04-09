package com.kishan.employeetracker.ui

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


/**
 * Created by Kishan P Rao on 09/04/18.
 */
class CustomViewPager : ViewPager {
	
	private var isPagingEnabled = false
	
	constructor(context: Context) : super(context)
	
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	
	override fun onTouchEvent(event: MotionEvent): Boolean {
		return this.isPagingEnabled && super.onTouchEvent(event)
	}
	
	override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
		return this.isPagingEnabled && super.onInterceptTouchEvent(event)
	}
	
	/*fun setPagingEnabled(b: Boolean) {
		this.isPagingEnabled = b
	}*/
}
