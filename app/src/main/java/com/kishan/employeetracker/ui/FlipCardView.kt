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
		
		private const val TEXT_COLOR = Color.WHITE
		private const val CARD_COLOR = Color.DKGRAY
		private const val BG_COLOR = Color.TRANSPARENT
//		private const val BG_COLOR = Color.BLUE
		
		private const val SHADOW_COLOR = Color.BLACK
		//		private const val SHADOW_COLOR = Color.YELLOW
		private const val SHADOW_RADIUS = 5.0f
		private const val SHADOW_X_OFFSET = 2.0f
		private const val SHADOW_Y_OFFSET = 2.0f
		
		private const val MAX_TIME_MS = 2 * 1000
		private const val SINGLE_CARD_AMOUNT = 180.0f
	}
	
	private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
	private var mPadding: Float
	private var mCornerSize: Float
	
	private val mRect = RectF()
	private val mMatrix = Matrix()
	private val mCamera = Camera()
	
	private var mText = "0"
	private var mTextSecond = "0"
	private var mTextCount = -1
	var text = 30
	
	//	private val SINGLE_CARD_AMOUNT = 90.0f
	private var mRotation = -1.0f
	private var mRotationFinished = -1.0f
	private var MAX_LIMIT = 80.0f
	
	private var mIncrement = 1.0f
	//	private val MAX_TIME_MS = 4 * 1000
	private var mStartTime = -1
	
	//	private val mInterpolator = PathInterpolator(0.2f, 0.0f, 0.2f, 1.0f)
//	private val mInterpolator = PathInterpolator(0.2f, 0.2f, 0.9f, 1.0f)
	private val mInterpolator = PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f)
	
	private var mInitialized = false
	
	constructor(context: Context) : this(context, null)
	
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
		
		mMatrix.preTranslate(-cx, -cy)
		mMatrix.postTranslate(cx, cy)
		
		canvas.save()
		canvas.matrix = mMatrix
		val oldShader = mTextPaint.shader
		mTextPaint.shader = mGradientShader

//		mTextPaint.color = CARD_COLOR
		canvas.drawRoundRect(mRect, mCornerSize, mCornerSize, mTextPaint)
		
		mTextPaint.shader = oldShader
		mTextPaint.color = TEXT_COLOR
//		mTextPaint.setShadowLayer(0.0f, 0.0f, 0.0f, SHADOW_COLOR)
		canvas.drawText(text, xPos, yPos, mTextPaint)
		
		canvas.restore()
	}
	
	private fun drawFirstCard(canvas: Canvas) {
		drawCard(mText, mRotation, xPos, yPos, canvas)
	}
	
	private fun drawSecondCard(canvas: Canvas) {
		drawCard(mTextSecond, mRotation + 180, xPosSecond, yPosSecond, canvas)
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
		val totalRotation = SINGLE_CARD_AMOUNT * (text + 1)    //Starts from -1
		val rotationForCurrentTime = amount * totalRotation
		val diff = rotationForCurrentTime - mRotationFinished
		mIncrement = diff
//		Log.d(TAG, "updateIncrement, $elapsed, $amount, $totalRotation")
//		Log.d(TAG, "updateIncrement, Rotation: $rotationForCurrentTime, $mRotationFinished, $diff")
	}
	
	private var cx = 0.0f
	private var cy = 0.0f
	private var xPos = 0.0f
	private var yPos = 0.0f
	private var xPosSecond = 0.0f
	private var yPosSecond = 0.0f
	private lateinit var mGradientShader: Shader
	
	private fun initValues() {
		cx = this.measuredWidth / 2.0f
		cy = this.measuredHeight / 2.0f
		mRect.set(mPadding, mPadding, width - mPadding, height - mPadding)
		val w = width.toFloat()
		val h = height.toFloat()
		val shaderColorStart = Color.LTGRAY
		val shaderColorEnd = Color.DKGRAY
		mGradientShader = LinearGradient(
				w, 0.0f, w, h,
				shaderColorStart, shaderColorEnd, Shader.TileMode.CLAMP)
	}
	
	override fun onDraw(canvas: Canvas) {
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
		mRotationFinished = mRotationFinished + mIncrement
		if (mRotation >= 180.0f) {
			mTextCount = (mTextCount + (mRotation / 180.0f).toInt())
			mText = mTextCount.toString()
			xPos = (canvas.width / 2.0f) - (mTextPaint.measureText(mText) / 2.0f)
			yPos = (canvas.height / 2.0f - (mTextPaint.descent() + mTextPaint.ascent()) / 2.0f)
			mTextSecond = (mTextCount + 1).toString()
			xPosSecond = (canvas.width / 2.0f) - (mTextPaint.measureText(mTextSecond) / 2.0f)
			yPosSecond = (canvas.height / 2.0f - (mTextPaint.descent() + mTextPaint.ascent()) / 2.0f)
		}
		mRotation %= 180
		
		canvas.drawColor(BG_COLOR)
		
		if (mRotation < 90) {
			drawFirstCard(canvas)
		} else {
			drawSecondCard(canvas)
		}
		if (mIncrement != 0.0f) {
			invalidate()
		}
	}
}