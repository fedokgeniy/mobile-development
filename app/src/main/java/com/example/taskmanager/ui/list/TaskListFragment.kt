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
 * Screen 1 — Task List
 * Edge-to-Edge is enabled here.
 * Supports both portrait and landscape (ViewModel holds state).
 */
class TaskListFragment : Fragment() {

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

        // ── Edge-to-Edge insets handling ──────────────────────────────────────
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ── RecyclerView ──────────────────────────────────────────────────────
        adapter = TaskAdapter(
            onItemClick = { task ->
                val action = TaskListFragmentDirections
                    .actionTaskListFragmentToTaskDetailFragment(task.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { task -> viewModel.deleteTask(task) }
        )
        binding.recyclerView.adapter = adapter

        // ── FAB ───────────────────────────────────────────────────────────────
        binding.fabAdd.setOnClickListener {
            val action = TaskListFragmentDirections
                .actionTaskListFragmentToTaskFormFragment(-1L)
            findNavController().navigate(action)
        }

        // ── Observe tasks ─────────────────────────────────────────────────────
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    adapter.submitList(tasks)
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
