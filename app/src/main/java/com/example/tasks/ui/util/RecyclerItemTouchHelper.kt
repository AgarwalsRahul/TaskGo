package com.example.tasks.ui.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.R
import com.example.tasks.ui.todo.TodosAdapter


class RecyclerItemTouchHelper(
    val interaction: TodosAdapter.Interaction,
    val context: Context,
    val dialogDismissHelper: DialogDismissHelper
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Task")
            builder.setMessage("Are you sure you want to delete this Task?")
            builder.setPositiveButton("Confirm",
                DialogInterface.OnClickListener { dialog, which -> interaction.deleteTask(viewHolder.adapterPosition) })
            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

            val dialog: AlertDialog = builder.create()

            dialog.setOnDismissListener {
                dialogDismissHelper.notifyItemInRecyclerView(viewHolder.adapterPosition)
            }
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            }
            dialog.show()
        } else {
            interaction.editTask(viewHolder.adapterPosition)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val icon: Drawable?
        val background: ColorDrawable
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset = 20
        if (dX > 0) {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit)
            background = ColorDrawable(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimaryDark
                )
            )
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete)
            background = ColorDrawable(Color.RED)
        }
        assert(icon != null)
        val iconMargin: Int = (itemView.getHeight() - icon!!.intrinsicHeight) / 2
        val iconTop: Int = itemView.getTop() + (itemView.getHeight() - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        if (dX > 0) { // Swiping to the right
            val iconLeft: Int = itemView.getLeft() + iconMargin
            val iconRight: Int = itemView.getLeft() + iconMargin + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.getLeft(), itemView.getTop(),
                itemView.getLeft() + dX.toInt() + backgroundCornerOffset, itemView.getBottom()
            )
        } else if (dX < 0) { // Swiping to the left
            val iconLeft: Int = itemView.getRight() - iconMargin - icon.intrinsicWidth
            val iconRight: Int = itemView.getRight() - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.getRight() + dX.toInt() - backgroundCornerOffset,
                itemView.getTop(), itemView.getRight(), itemView.getBottom()
            )
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
        icon.draw(c)
    }


}