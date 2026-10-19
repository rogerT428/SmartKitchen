package com.example.smartkitchen

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class GasGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var showYAxis: Boolean = true
    var showThreshold: Boolean = true
    var isGasAlert: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEEEEEE.toInt()
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val thresholdPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFD32F2F.toInt()
        strokeWidth = 3f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(12f, 8f), 0f)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val pointInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF757575.toInt()
        textSize = 28f
    }

    private val thresholdTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFD32F2F.toInt()
        textSize = 26f
    }

    private val path = Path()
    private val fillPath = Path()

    // Default sample data points (ADC values)
    private val normalValues = listOf(310f, 320f, 340f, 420f, 560f, 480f, 350f)
    private val alertValues = listOf(350f, 420f, 780f, 1100f, 1250f, 1180f, 950f)
    private val timeLabels = listOf("14:18", "14:19", "14:20", "14:21", "14:22", "14:23", "14:24")

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = if (showYAxis) 90f else 40f
        val paddingRight = 40f
        val paddingTop = 40f
        val paddingBottom = 70f

        val graphWidth = width - paddingLeft - paddingRight
        val graphHeight = height - paddingTop - paddingBottom

        if (graphWidth <= 0 || graphHeight <= 0) return

        val maxVal = 2000f
        val thresholdVal = 800f
        val currentValues = if (isGasAlert) alertValues else normalValues

        // Draw Y Axis Labels and Horizontal Grid Lines
        if (showYAxis) {
            val yTicks = listOf(0, 500, 1000, 1500, 2000)
            for (tick in yTicks) {
                val y = paddingTop + graphHeight - (tick / maxVal) * graphHeight
                canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint)
                canvas.drawText(tick.toString(), 10f, y + 8f, textPaint)
            }
        } else {
            // Draw horizontal grid lines
            for (i in 0..3) {
                val y = paddingTop + graphHeight * i / 3f
                canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint)
            }
        }

        // Draw Threshold Line at 800
        if (showThreshold) {
            val thresholdY = paddingTop + graphHeight - (thresholdVal / maxVal) * graphHeight
            canvas.drawLine(paddingLeft, thresholdY, width - paddingRight, thresholdY, thresholdPaint)
            canvas.drawText("Ambang (800)", width - paddingRight - 160f, thresholdY - 10f, thresholdTextPaint)
        }

        // Calculate Data Point Positions
        val numPoints = currentValues.size
        val xStep = graphWidth / (numPoints - 1)

        path.reset()
        fillPath.reset()

        val xPoints = FloatArray(numPoints)
        val yPoints = FloatArray(numPoints)

        for (i in 0 until numPoints) {
            val valADC = currentValues[i]
            val x = paddingLeft + i * xStep
            val y = paddingTop + graphHeight - (valADC / maxVal) * graphHeight
            xPoints[i] = x
            yPoints[i] = y

            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, paddingTop + graphHeight)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            // X Axis time labels
            val timeText = timeLabels.getOrElse(i) { "" }
            val textWidth = textPaint.measureText(timeText)
            canvas.drawText(timeText, x - textWidth / 2f, height - 15f, textPaint)
        }

        fillPath.lineTo(xPoints.last(), paddingTop + graphHeight)
        fillPath.close()

        // Gradient color for line and fill
        val lineColor = if (isGasAlert) 0xFFD32F2F.toInt() else 0xFFE85D04.toInt()
        val fillColorStart = if (isGasAlert) 0x66D32F2F else 0x66E85D04
        val fillColorEnd = 0x00FFFFFF

        linePaint.color = lineColor
        pointPaint.color = lineColor

        fillPaint.shader = LinearGradient(
            0f, paddingTop, 0f, paddingTop + graphHeight,
            fillColorStart, fillColorEnd, Shader.TileMode.CLAMP
        )

        // Draw area fill & line path
        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(path, linePaint)

        // Draw data points
        for (i in 0 until numPoints) {
            canvas.drawCircle(xPoints[i], yPoints[i], 9f, pointPaint)
            canvas.drawCircle(xPoints[i], yPoints[i], 4f, pointInnerPaint)
        }
    }
}