package com.kishan.employeetracker.ui

import android.content.Context
import android.graphics.*
import android.support.v4.view.animation.PathInterpolatorCompat
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import com.kishan.employeetracker.R


/**
 * Created by Kishan P Rao on 06/04/18.
 */
class FlipCardView : View {
	companion object {
		private val TAG = FlipCardView::class.java.simpleName
		
		private const val TEXT_COLOR = Color.GRAY
		private const val CARD_COLOR = Color.WHITE
		private const val BG_COLOR = Color.TRANSPARENT
		
//		private const val SHADOW_COLOR = Color.BLACK
		private const val SHADOW_COLOR = Color.TRANSPARENT
		private const val SHADOW_RADIUS = 5.0f
		private const val SHADOW_X_OFFSET = 2.0f
		private const val SHADOW_Y_OFFSET = 2.0f
		
		private const val MAX_TIME_MS = AnimationUtils.ANIM_TIME
		//		private const val MAX_TIME_MS = 10 * 1000
		private const val SINGLE_CARD_AMOUNT = 180.0f
	}
	
	private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
	private var mPadding: Float
	private var mCornerSize: Float
	
	private val mRect = RectF()
	private val mMatrix = Matrix()
	private val mCamera = Camera()
	
	private var mText = "DUMMY"
	private var mTextSecond = "DUMMY"
	private var mTextCount = 0
	var number = 1
		set(value) {
			if (!mInAnimation) {
				if (field != value) {
					updateTextValues(value)
					invalidate()
				}
			}
			field = value
		}
	
	private var mRotation = -1.0f
	private var mNumRotationFinished = -1.0f
//	private var MAX_LIMIT = 80.0f   //Per onDraw increment. Beyond this animations looks horrible.
	
	private var mIncrement = 1.0f
	private var mStartTime = -1
	
	//	private val mInterpolator = createPathInterpolator(0.2f, 0.0f, 0.2f, 1.0f)
//	private val mInterpolator = createPathInterpolator(0.2f, 0.2f, 0.9f, 1.0f)
//	private val mInterpolator = createPathInterpolator(0.2f, 0.0f, 0.0f, 1.0f)
	private val mInterpolator = AnimationUtils.INTERPOLATOR
	
	private var mInitialized = false
	private var mInAnimation = true
	
	private var mCx = 0.0f
	private var mCy = 0.0f
	private var mXPos = 0.0f
	private var mYPos = 0.0f
	private var mXPosSecond = 0.0f
	private var mYPosSecond = 0.0f
	private lateinit var mGradientShader: Shader
	
	/*private fun createPathInterpolator(controlX1: Float,
	                                   controlY1: Float,
	                                   controlX2: Float,
	                                   controlY2: Float): Interpolator {
		return PathInterpolatorCompat.create(controlX1, controlY1, controlX2, controlY2)
	}*/
	
	constructor(context: Context) : this(context, null)
	
	fun prepare() {
		mInAnimation = true
	}
	
	fun reset() {
		mStartTime = -1
		mIncrement = 1.0f
		mNumRotationFinished = -1.0f
		mRotation = -1.0f
		mTextCount = 0
		mInitialized = false
		prepare()
		
		invalidate()
	}
	
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		mTextPaint.textSize = context.resources.getDimension(R.dimen.font_size)
		mPadding = context.resources.getDimension(R.dimen.card_padding)
		mCornerSize = mPadding / 5.0f
		mTextPaint.setShadowLayer(SHADOW_RADIUS, SHADOW_X_OFFSET, SHADOW_Y_OFFSET, SHADOW_COLOR)
	}
	
	private fun drawCard(text: String, angle: Float, xPos: Float, yPos: Float, canvas: Canvas) {
		mCamera.save()
		mMatrix.reset()
		mCamera.rotateX(angle)
		mCamera.getMatrix(mMatrix)
		mCamera.restore()
		
		mMatrix.preTranslate(-mCx, -mCy)
		mMatrix.postTranslate(mCx, mCy)
		
		canvas.save()
		canvas.matrix = mMatrix
		val oldShader = mTextPaint.shader
//		mTextPaint.shader = mGradientShader

		mTextPaint.color = CARD_COLOR
		canvas.drawRoundRect(mRect, mCornerSize, mCornerSize, mTextPaint)
		
		mTextPaint.shader = oldShader
		mTextPaint.color = TEXT_COLOR
		canvas.drawText(text, xPos, yPos, mTextPaint)
		
		canvas.restore()
	}
	
	private fun drawFirstCard(canvas: Canvas) {
		drawCard(mText, mRotation, mXPos, mYPos, canvas)
	}
	
	private fun drawSecondCard(canvas: Canvas) {
		drawCard(mTextSecond, mRotation + 180, mXPosSecond, mYPosSecond, canvas)
	}
	
	private fun updateIncrement() {
		if (mStartTime == -1) {
			mStartTime = System.currentTimeMillis().toInt()
		}
		val currentTime = System.currentTimeMillis().toInt()
		val elapsed = (currentTime - mStartTime)
		var amount = elapsed / MAX_TIME_MS.toFloat()
		if (amount > 1.0f) {
			amount = 1.0f
		}
		val amountModified = mInterpolator.getInterpolation(amount)
//		Log.d(TAG, "updateIncrement, $amount -> $amountModified")
		amount = amountModified
		val totalRotation = SINGLE_CARD_AMOUNT * number
		val rotationForCurrentTime = amount * totalRotation
		val diff = rotationForCurrentTime - mNumRotationFinished
		mIncrement = diff
//		Log.d(TAG, "updateIncrement, $elapsed, $amount, $totalRotation")
//		Log.d(TAG, "updateIncrement, Rotation: $rotationForCurrentTime, $mNumRotationFinished, $diff")
	}
	
	private fun updateTextValues(value: Int) {
		mText = value.toString()
		mXPos = (width / 2.0f) - (mTextPaint.measureText(mText) / 2.0f)
		mYPos = (height / 2.0f - (mTextPaint.descent() + mTextPaint.ascent()) / 2.0f)
		mTextSecond = (mTextCount + 1).toString()
		mXPosSecond = (width / 2.0f) - (mTextPaint.measureText(mTextSecond) / 2.0f)
		mYPosSecond = (height / 2.0f - (mTextPaint.descent() + mTextPaint.ascent()) / 2.0f)
	}
	
	private fun initValues() {
		mCx = this.measuredWidth / 2.0f
		mCy = this.measuredHeight / 2.0f
		mRect.set(mPadding, mPadding, width - mPadding, height - mPadding)
		val w = width.toFloat()
		val h = height.toFloat()
		val shaderColorStart = Color.LTGRAY
		val shaderColorEnd = Color.DKGRAY
		mGradientShader = LinearGradient(
				w, 0.0f, w, h,
				shaderColorStart, shaderColorEnd, Shader.TileMode.CLAMP)
		updateTextValues(mTextCount)
	}
	
	override fun onDraw(canvas: Canvas) {
		if (mInAnimation) {
			if (!mInitialized) {
				if (this.width <= 0 || this.measuredWidth <= 0) {
					Log.w(TAG, "onDraw, bad width")
					return
				}
				initValues()
				mInitialized = true
			}
			updateIncrement()
			mRotation = mRotation + mIncrement
			mNumRotationFinished = mNumRotationFinished + mIncrement
			if (mRotation >= 180.0f) {
				mTextCount = (mTextCount + (mRotation / 180.0f).toInt())
				updateTextValues(mTextCount)
			}
			mRotation %= 180
			
			canvas.drawColor(BG_COLOR)
			
			if (mRotation < 90) {
				drawFirstCard(canvas)
			} else {
				drawSecondCard(canvas)
			}
			if (mIncrement != 0.0f) {
				mInAnimation = true
				invalidate()
			} else {
				mInAnimation = false
			}
		} else {
			drawCard(mText, mRotation, mXPos, mYPos, canvas)
		}
	}
}