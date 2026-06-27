package com.example.taskmanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.TaskType

/**
 * RecyclerView adapter for the task list screen.
 *
 * Extends [ListAdapter] with a [DiffUtil.ItemCallback] so that only changed
 * items are redrawn, improving scroll performance and enabling item animations.
 *
 * @param onItemClick   Called when the user taps a task row to open its details.
 * @param onDeleteClick Called when the user taps the delete button on a task row.
 */
class TaskAdapter(
    private val onItemClick: (TaskEntity) -> Unit,
    private val onDeleteClick: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder that binds a single [TaskEntity] to the item layout.
     *
     * The try-catch around [TaskType.valueOf] guards against stale string values
     * that may not match any current enum constant after a schema migration.
     */
    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.tvTitle.text = task.title
            binding.tvType.text = try {
                TaskType.valueOf(task.type).displayName
            } catch (e: IllegalArgumentException) {
                // Fall back to the raw string if the type is unrecognized.
                task.type
            }
            binding.tvPriority.text = "Priority: ${task.priority}"
            binding.cbDone.isChecked = task.isDone

            binding.root.setOnClickListener { onItemClick(task) }
            binding.btnDelete.setOnClickListener { onDeleteClick(task) }
        }
    }

    companion object {
        /**
         * DiffUtil callback used by [ListAdapter] to compute the minimal set of
         * changes between two list snapshots.
         *
         * [areItemsTheSame] compares stable IDs to detect moves and removals.
         * [areContentsTheSame] uses data class equality to detect field-level changes.
         */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskEntity>() {
            override fun areItemsTheSame(old: TaskEntity, new: TaskEntity) = old.id == new.id
            override fun areContentsTheSame(old: TaskEntity, new: TaskEntity) = old == new
        }
    }
}
