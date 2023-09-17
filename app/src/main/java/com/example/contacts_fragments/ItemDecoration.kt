import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts_fragments.R

class ItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val dividerPaint: Paint = Paint()

    init {
        dividerPaint.color = ContextCompat.getColor(context, R.color.pink_light)
        dividerPaint.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.dividerHeight).toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerPaint.strokeWidth
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, dividerPaint)
        }
    }
}
