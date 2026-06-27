package com.example.taskmanager.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.databinding.FragmentTaskFormBinding
import com.example.taskmanager.model.TaskType
import com.example.taskmanager.viewmodel.TaskFormViewModel
import kotlinx.coroutines.launch

/**
 * Screen 3 — Create / Edit Task
 * In edit mode the type spinner is disabled (type is fixed after creation).
 */
class TaskFormFragment : Fragment() {

    private var _binding: FragmentTaskFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskFormViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Type spinner
        val types = TaskType.values().map { it.displayName }
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )
        binding.spinnerType.adapter = spinnerAdapter

        // In edit mode — pre-fill fields and lock the type spinner
        if (viewModel.isEditMode) {
            binding.spinnerType.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.existingTask.collect { task ->
                        task ?: return@collect
                        binding.etTitle.setText(task.title)
                        binding.etDescription.setText(task.description)
                        val typeIndex = TaskType.values().indexOfFirst { it.name == task.type }
                        if (typeIndex >= 0) binding.spinnerType.setSelection(typeIndex)
                        binding.seekBarPriority.progress = task.priority - 1
                        binding.etDeadline.setText(task.deadline ?: "")
                        binding.cbDone.isChecked = task.isDone
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            if (title.isEmpty()) {
                binding.etTitle.error = "Title is required"
                return@setOnClickListener
            }

            val selectedType = TaskType.values()[binding.spinnerType.selectedItemPosition].name
            val priority = binding.seekBarPriority.progress + 1
            val deadline = binding.etDeadline.text.toString().trim().ifEmpty { null }

            viewModel.save(
                title = title,
                description = binding.etDescription.text.toString().trim(),
                type = selectedType,
                priority = priority,
                deadline = deadline,
                isDone = binding.cbDone.isChecked,
                onSuccess = {
                    requireActivity().runOnUiThread {
                        findNavController().popBackStack()
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
