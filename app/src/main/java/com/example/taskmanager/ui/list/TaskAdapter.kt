package com.example.taskmanager.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.TaskType

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

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.tvTitle.text = task.title
            binding.tvType.text = try {
                TaskType.valueOf(task.type).displayName
            } catch (e: IllegalArgumentException) {
                task.type
            }
            binding.tvPriority.text = "Priority: ${task.priority}"
            binding.cbDone.isChecked = task.isDone

            binding.root.setOnClickListener { onItemClick(task) }
            binding.btnDelete.setOnClickListener { onDeleteClick(task) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskEntity>() {
            override fun areItemsTheSame(old: TaskEntity, new: TaskEntity) = old.id == new.id
            override fun areContentsTheSame(old: TaskEntity, new: TaskEntity) = old == new
        }
    }
}
