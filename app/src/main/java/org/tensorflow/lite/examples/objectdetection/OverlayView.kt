package org.tensorflow.lite.examples.objectdetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.LinkedList
import kotlin.math.max
import java.util.HashMap
import kotlin.collections.Map;

import org.tensorflow.lite.task.vision.detector.Detection

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }
    fun String.replacelabels(vararg replacements: Pair<String,String>):String {
        var results=this
        replacements.forEach {(l,r)-> results=results.replace(l,r)}
        return results
    }
    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.bounding_box_color)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            val left = boundingBox.left * scaleFactor
            val right = boundingBox.right * scaleFactor

            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)
            val drawableText =
                when (result.categories[0].label){
                    "apple"->result.categories[0].label.replacelabels(result.categories[0].label to "Apple: Calorie->95")
                    "banana"->result.categories[0].label.replacelabels(result.categories[0].label to "Banana: Calorie->105")
                    "pineapple"->result.categories[0].label.replacelabels(result.categories[0].label to "Pineapple: Calorie->41")
                    "strawberry"->result.categories[0].label.replacelabels(result.categories[0].label to "Strawberry: Calorie->29")
                    "orange"->result.categories[0].label.replacelabels(result.categories[0].label to "Orange: Calorie->69")
                    "pomegranate"->result.categories[0].label.replacelabels(result.categories[0].label to "Pomegranate: Calorie->234")
                    "avocado"->result.categories[0].label.replacelabels(result.categories[0].label to "Avocado: Calorie->161")
                    "lychee"->result.categories[0].label.replacelabels(result.categories[0].label to "Lychee: Calorie->125")
                    else -> result.categories[0].label+" "+String.format("%.2f", result.categories[0].score)
                }
            // Draw rect behind display text
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()
            canvas.drawRect(
                left,
                top,
                left + textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                textBackgroundPaint
            )

            // Draw text for detected object
            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
        }
    }

    fun setResults(
      detectionResults: MutableList<Detection>,
      imageHeight: Int,
      imageWidth: Int,
    ) {
        results = detectionResults
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}
