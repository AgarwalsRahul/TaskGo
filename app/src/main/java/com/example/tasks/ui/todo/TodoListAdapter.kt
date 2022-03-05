package com.example.tasks.ui.todo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.R
import com.example.tasks.domain.models.Todo

class TodosAdapter(
    private val interaction: Interaction? = null,
    private val context: Context,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Todo>() {

        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.task_item_layout,
                parent,
                false
            ),
            interaction,
            context
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Todo>) {
        val commitCallback = Runnable {
            // if process died must restore list position
            // very annoying
            interaction?.restoreListPosition()
        }
        differ.submitList(list, commitCallback)
    }

    fun getTodo(index: Int): Todo? {
        return try {
            differ.currentList[index]
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            null
        }
    }

    class TodoViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?,
        private val context: Context
    ) : RecyclerView.ViewHolder(itemView),CompoundButton.OnCheckedChangeListener {


        private lateinit var todo: Todo

        fun bind(item: Todo) = with(itemView) {
            setOnClickListener {
                interaction?.onItemSelected(adapterPosition, todo)

            }



            todo = item
            findViewById<CheckBox>(R.id.todoCheckBox).apply {
                setOnCheckedChangeListener(null)
                text = item.task
                isChecked = item.status == 1
                setOnCheckedChangeListener(this@TodoViewHolder)

            }


        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1){
                    interaction?.updateTaskStatus(todo.copy(status = 1))
                }else{
                    interaction?.updateTaskStatus(todo.copy(status = 0))
                }

        }

    }


    interface Interaction {

        fun onItemSelected(position: Int, item: Todo)

        fun restoreListPosition()

        fun deleteTask(position: Int)

        fun editTask(position: Int)

        fun updateTaskStatus(todo: Todo)
    }


}