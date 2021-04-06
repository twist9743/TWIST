package geva.oren.rotaryknobdemo

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.rotary_knob_view.view.*
import kotlin.math.atan2


class RotaryKnobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {
    private val gestureDetector: GestureDetectorCompat
    private var maxValue = 99
    private var minValue = 0
    var listener: RotaryKnobListener? = null
    var value = 130
    private var knobDrawable: Drawable? = null
    private var divider = 300f / (maxValue - minValue)

    interface RotaryKnobListener {
        fun onRotate(value: Int)
    }

    init {
        this.maxValue = maxValue + 1

        LayoutInflater.from(context)
            .inflate(R.layout.rotary_knob_view, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RotaryKnobView,
            0,
            0
        ).apply {
            try {
                minValue = getInt(R.styleable.RotaryKnobView_minValue, 0)
                maxValue = getInt(R.styleable.RotaryKnobView_maxValue, 100) + 1
                divider = 300f / (maxValue - minValue)
                value = getInt(R.styleable.RotaryKnobView_initialValue, 50)
                knobDrawable = getDrawable(R.styleable.RotaryKnobView_knobDrawable)
                knobImageView.setImageDrawable(knobDrawable)
            } finally {
                recycle()
            }
        }
        gestureDetector = GestureDetectorCompat(context, this)
    }

    /**
     * We're only interested in e2 - the coordinates of the end movement.
     * We calculate the polar angle (Theta) from these coordinates and use these to animate the
     * knob movement and calculate the value
     */
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float)
            : Boolean {

        val rotationDegrees = calculateAngle(e2.x, e2.y)
        // use only -150 to 150 range (knob min/max points
        if (rotationDegrees >= -150 && rotationDegrees <= 150) {
            setKnobPosition(rotationDegrees)

            // Calculate rotary value
            // The range is the 300 degrees between -150 and 150, so we'll add 150 to adjust the
            // range to 0 - 300
            val valueRangeDegrees = rotationDegrees + 150
                value = ((valueRangeDegrees / divider) + minValue).toInt()
                if (listener != null) listener!!.onRotate(value)
        }
        return true
    }

    /**
     * Calculate the angle from x,y coordinates of the touch event
     * The 0,0 coordinates in android are the top left corner of the view.
     * Dividing x and y by height and width we normalize them to the range of 0 - 1 values:
     * (0,0) top left, (1,1) bottom right.
     * While x's direction is correct - going up from left to right, y's isn't - it's
     * lowest value is at the top. W
     * So we reverse it by subtracting y from 1.
     * Now x is going from 0 (most left) to 1 (most right),
     * and Y is going from 0 (most downwards) to 1 (most upwards).
     * We now need to bring 0,0 to the middle - so subtract 0.5 from both x and y.
     * now 0,0 is in the middle, 0, 0.5 is at 12 o'clock and 0.5, 0 is at 3 o'clock.
     * Now that we have the coordinates in proper cartesian coordinate system - and we can calculate
     * "theta" - the angle between the x axis and the point by calculating atan2(y,x).
     * However, theta is the angle between the x axis and the point, and it rises as we turn
     * counter-clockwise. In addition, atan2 returns (in radians) angles in the range of -180
     * through 180 degrees (-PI through PI). And we want 0 to be at 12 o'clock.
     * So we reverse the direction of the angle by prefixing it with a minus sign,
     * and add 90 to move the "zero degrees" point north (taking care to handling the range between
     * 180 and 270 degrees, bringing them to their proper values of -180 .. -90 by adding 360 to the
     * value.
     *
     * @param x - x coordinate of the touch event
     * @param y - y coordinate of the touch event
     * @return
     */
    private fun calculateAngle(x: Float, y: Float): Float {
        val px = (x / width.toFloat()) - 0.5
        val py = ( 1 - y / height.toFloat()) - 0.5
        var angle = -(Math.toDegrees(atan2(py, px)))
            .toFloat() + 90
        if (angle > 180) angle -= 360
        return angle
    }

    private fun setKnobPosition(deg: Float) {
        val matrix = Matrix()
        knobImageView.scaleType = ScaleType.MATRIX
        matrix.postRotate(deg, width.toFloat() / 2, height.toFloat() / 2)
        knobImageView.imageMatrix = matrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event))
            true
        else
            super.onTouchEvent(event)
    }

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onFling(arg0: MotionEvent, arg1: MotionEvent, arg2: Float, arg3: Float)
            : Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onShowPress(e: MotionEvent) {}
}
