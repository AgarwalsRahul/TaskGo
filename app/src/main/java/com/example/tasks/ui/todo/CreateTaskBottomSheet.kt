package com.example.tasks.ui.todo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.tasks.R
import com.example.tasks.databinding.BottomSheetLayoutBinding
import com.example.tasks.domain.models.Todo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CreateTaskBottomSheet : BottomSheetDialogFragment(), TextWatcher {
    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private var isUpdate: Boolean = false

    private var todoItem: Todo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLayoutBinding.inflate(layoutInflater, container, false)
//        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE || WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        bundle?.let {
            isUpdate = true
            todoItem = it.getParcelable("todo")
            todoItem?.let {
                binding.editTextTask.setText(it.task)
                if (it.task.isNotEmpty()) binding.saveButton.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.colorPrimaryDark
                    )
                )
            }


        }
        binding.editTextTask.addTextChangedListener(this)

        binding.saveButton.setOnClickListener {
            if (isUpdate) {
                (activity as BottomSheetDismissListener).updateTask(todoItem!!.copy(task = binding.editTextTask.text.toString()))
            } else {
                (activity as BottomSheetDismissListener).createTask(binding.editTextTask.text.toString())
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as BottomSheetDismissListener).onDismiss()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0.toString().isEmpty()) {
            binding.saveButton.isEnabled = false
            binding.saveButton.setTextColor(Color.GRAY)
        } else {
            binding.saveButton.isEnabled = true
            binding.saveButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimaryDark
                )
            )
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}


interface BottomSheetDismissListener {
    fun onDismiss()
    fun createTask(task: String)

    fun updateTask(todo: Todo)
}