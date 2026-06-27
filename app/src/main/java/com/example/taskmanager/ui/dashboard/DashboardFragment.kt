package com.example.taskmanager.ui.dashboard

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
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentDashboardBinding
import com.example.taskmanager.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch

/**
 * The home screen of the application.
 *
 * Displays a summary of all tasks: total count, how many are done, and how
 * many are still pending. Provides a button to navigate to the full task list.
 *
 * Reuses [TaskListViewModel] because it already exposes the same task stream
 * needed for the statistics shown here.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the task list and recompute stats on every emission.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    val total = tasks.size
                    val done = tasks.count { it.isDone }
                    val pending = total - done

                    binding.tvTotal.text = total.toString()
                    binding.tvDone.text = done.toString()
                    binding.tvPending.text = pending.toString()
                }
            }
        }

        // Navigate to the task list screen when the button is tapped.
        binding.btnGoToTasks.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_taskList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
