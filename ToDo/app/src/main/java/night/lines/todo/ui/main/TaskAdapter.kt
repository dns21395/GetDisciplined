package night.lines.todo.ui.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_task.view.*
import night.lines.todo.R
import night.lines.todo.model.data.database.model.Task
import night.lines.todo.presentation.main.task.TaskPresenter
import night.lines.todo.ui.main.task.TaskAdapterSwipeCallback
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource

/**
 * Created by denisgabyshev on 20/03/2018.
 */
class TaskAdapter(context: Context, recyclerView: RecyclerView) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    lateinit var presenter: TaskPresenter

    private val swipeCallback = TaskAdapterSwipeCallback(context, this)
    private val itemTouchHelper = ItemTouchHelper(swipeCallback)

    init {
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder
        = TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))

    override fun getItemCount(): Int = presenter.getTaskArrayItemCount()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(presenter.getTaskByPosition(position))
    }

    fun updateArray(array: ArrayList<Task>) {
        presenter.updateTaskArray(array)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        presenter.onItemSwiped(presenter.getTaskByPosition(position), {notifyItemRemoved(position)})
    }

    inner class TaskViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView?.setOnLongClickListener {
                itemTouchHelper.startSwipe(this)
                false
            }
        }

        fun bind(task: Task) = with(itemView){
            taskName.text = task.taskName

            when(task.isDone) {
                true -> statusButton.imageResource = R.drawable.checkbox_marked_circle
                false -> statusButton.imageResource = R.drawable.checkbox_blank_circle_outline
            }

            statusButton.setOnClickListener {
                task.isDone = !task.isDone
                presenter.onStatusButtonClick(task)
            }
        }

        private fun changeBackground(color: Int) = with(itemView)  {
            itemView.backgroundColor = ContextCompat.getColor(context, color)
        }

        fun onItemSelected() {
            changeBackground(R.color.colorPrimaryDark)
        }

        fun onItemClear() {
            changeBackground(android.R.color.white)
        }
    }
}