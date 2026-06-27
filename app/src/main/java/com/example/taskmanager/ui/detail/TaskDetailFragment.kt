package com.example.taskmanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.databinding.FragmentTaskDetailBinding
import com.example.taskmanager.model.TaskType
import com.example.taskmanager.viewmodel.TaskDetailViewModel
import kotlinx.coroutines.launch

/**
 * Screen 2 — Task Details
 * Back navigation provided by MainActivity's setupActionBarWithNavController.
 */
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.task.collect { task ->
                    task ?: return@collect
                    binding.tvTitle.text = task.title
                    binding.tvDescription.text = task.description
                    binding.tvType.text = try {
                        TaskType.valueOf(task.type).displayName
                    } catch (e: IllegalArgumentException) { task.type }
                    binding.tvPriority.text = "Priority: ${task.priority}"
                    binding.tvDeadline.text = task.deadline ?: "No deadline"
                    binding.tvStatus.text = if (task.isDone) "Done" else "In progress"
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            val taskId = viewModel.task.value?.id ?: return@setOnClickListener
            val action = TaskDetailFragmentDirections
                .actionTaskDetailFragmentToTaskFormFragment(taskId)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteTask {
                requireActivity().runOnUiThread {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
