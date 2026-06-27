package com.example.taskmanager.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.databinding.FragmentTaskListBinding
import com.example.taskmanager.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch

/**
 * Screen 1 - Task List.
 *
 * Displays all tasks in a RecyclerView and provides a FAB to create new tasks.
 * This screen is Edge-to-Edge: it draws behind the system bars and applies
 * window insets as padding so content is never obscured.
 *
 * Orientation changes are handled automatically because all state lives in
 * [TaskListViewModel], which survives configuration changes.
 */
class TaskListFragment : Fragment() {

    // _binding is nulled in onDestroyView to avoid holding a reference to
    // the destroyed view hierarchy and prevent memory leaks.
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply system bar insets so the content respects the status bar and
        // navigation bar heights. This is required for Edge-to-Edge layouts.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the adapter with click callbacks delegated to the ViewModel.
        adapter = TaskAdapter(
            onItemClick = { task ->
                val action = TaskListFragmentDirections
                    .actionTaskListFragmentToTaskDetailFragment(task.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { task -> viewModel.deleteTask(task) }
        )
        binding.recyclerView.adapter = adapter

        // FAB navigates to the form in create mode (taskId = -1 signals new task).
        binding.fabAdd.setOnClickListener {
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskFormFragment(-1L)
            findNavController().navigate(action)
        }

        // Collect the task list only while the fragment is at least STARTED.
        // repeatOnLifecycle cancels the inner block when the lifecycle drops
        // below STARTED and re-launches it when it returns, preventing updates
        // to a fragment that is in the back stack.
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.submitList(tasks)
                    // Show the empty state view only when there are no tasks.
                    binding.emptyState.visibility =
                        if (tasks.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
