package me.tino.lifecyclekt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.experimental.delay
import java.util.*

/**
 * mailTo:guocheng@xuxu.in
 * Created by tino on 2018 March 26, 16:30.
 */
class CustomView : View {
    private val paint: Paint by lazy {
        Paint().also {
            it.color = Color.BLUE
            it.style = Paint.Style.STROKE
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    fun loadTask() {
        load {
            delay(3_000)
            val random = Random()
            Triple(random.nextInt(255), random.nextInt(255), random.nextInt(255))
        } then {
            tag = it
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (tag != null) {
            val (r, g, b) = tag as Triple<Int, Int, Int>
            canvas.drawARGB(255, r, g, b)
        }
    }
}