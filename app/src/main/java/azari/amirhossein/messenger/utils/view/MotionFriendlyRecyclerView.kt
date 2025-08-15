package azari.amirhossein.messenger.utils.view


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class MotionFriendlyRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    // Flag to track whether MotionLayout is consuming touch events
    private var isMotionLayoutConsumingEvents = false

    // Variables to store the initial touch coordinates
    private var startX = 0f
    private var startY = 0f

    // Handle touch events
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val targetView = findChildViewUnder(e.x, e.y)
        if (targetView is MotionLayout) {
            when (e.action) {
                // If the touch event is on the MotionLayout,
                MotionEvent.ACTION_DOWN -> {
                    startX = e.x
                    startY = e.y
                    isMotionLayoutConsumingEvents = false
                }
                // If the touch event is outside the MotionLayout,
                MotionEvent.ACTION_MOVE -> {
                    if (!isMotionLayoutConsumingEvents) {
                        val dx = abs(e.x - startX)
                        val dy = abs(e.y - startY)

                        if (dx > dy) {
                            isMotionLayoutConsumingEvents = true
                            requestDisallowInterceptTouchEvent(true)
                            return false
                        }
                    }
                }
                // If the touch event is up or canceled, reset the flag
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isMotionLayoutConsumingEvents = false
                    requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return if (isMotionLayoutConsumingEvents) {
            false
        } else {
            super.onInterceptTouchEvent(e)
        }
    }
}