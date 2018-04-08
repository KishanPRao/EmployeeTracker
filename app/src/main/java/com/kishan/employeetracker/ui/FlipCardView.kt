package com.kishan.employeetracker.ui

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.PathInterpolator
import com.kishan.employeetracker.R


/**
 * Created by Kishan P Rao on 06/04/18.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class FlipCardView : View {
	companion object {
		private val TAG = FlipCardView::class.java.simpleName
	}
	
	private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
	private var mRotation = 1.0f
	
	constructor(context: Context) : this(context, null)
	
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		mTextPaint.textSize = context.resources.getDimension(R.dimen.font_size)
//		setLayerType(LAYER_TYPE_HARDWARE, mTextPaint)
	}
	
	private val mRect = RectF()
	private val mMatrix = Matrix()
	private val mCamera = Camera()
	private val mFactor = 3.5f
	
	private var mText = "1"
	private var mTextCount = 1
	private var mIncrement = 1.0f
	private var mMax = 100
	private var mIncrTimes = 30
	//	TODO
//	private val pathInterpolator = PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f)
//	private val pathInterpolator = PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f)
	private val pathInterpolator = PathInterpolator(0.2f, 1.0f, 0.4f, 0.0f)
	
	private fun updateIncrement(): Boolean {
//		val t = 0.0f
		/*val t = (mTextCount / mIncrTimes.toFloat())
		val value = pathInterpolator.getInterpolation(t)
		mIncrement = (value * mIncrTimes) * 10
		Log.d(TAG, "updateIncrement, $t -> $value, $mIncrement")*/
		val diff = mIncrTimes - mTextCount
		mIncrement = diff * mFactor * mFactor
		Log.d(TAG, "updateIncrement $mIncrement")
		return mIncrement != 0.0f
	}
	
	/*	*/
	override fun onDraw(canvas: Canvas) {
		val isLesserThan180 = mRotation < 180.0f
		mRotation = (mRotation + mIncrement) % 360.0f
		
		if (mRotation >= 180.0f && isLesserThan180) {
			mTextCount++
			mText = mTextCount.toString()
		}
		
		val cx = this.measuredWidth / 2.0f
		val cy = this.measuredHeight / 2.0f
		
		mRect.set(0.0f, 0.0f, width.toFloat(), height.toFloat())
		mCamera.save()
		mMatrix.reset()
		mCamera.rotateX(mRotation)
		mCamera.getMatrix(mMatrix)
		mCamera.restore()
		
		mMatrix.preTranslate(-cx, -cy)
		mMatrix.postTranslate(cx, cy)
		
		canvas.drawColor(Color.BLUE)
		val xPos = (canvas.width / 2.0f) - (mTextPaint.measureText(mText) / 2.0f)
		val yPos = (canvas.height / 2.0f - (mTextPaint.descent() + mTextPaint.ascent()) / 2.0f)
		
		canvas.save()
		canvas.matrix = mMatrix
		
		mTextPaint.color = Color.RED
		canvas.drawRoundRect(mRect, 0.0f, 0.0f, mTextPaint)
		
		if (mRotation < 90.0f || mRotation > 270.0f) {
			mTextPaint.color = Color.WHITE
			canvas.drawText(mText, xPos, yPos, mTextPaint)
		}
		
		canvas.restore()
//		if (updateIncrement() || (mTextCount == mIncrTimes && mRotation != 0.0f))
		if (updateIncrement())
//		updateIncrement()
			invalidate()
		
//		if (mTextCount == mIncrTimes && mRotation != 0.0f) {
//			mIncrement = 3.0f
//		}
	}
}