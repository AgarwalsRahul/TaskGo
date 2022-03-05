package com.example.tasks.ui.todo

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gitbrowser.presentation.common.gone
import com.example.gitbrowser.presentation.common.visible
import com.example.tasks.R
import com.example.tasks.databinding.ActivityMainBinding
import com.example.tasks.domain.data.DataState
import com.example.tasks.domain.models.Todo
import com.example.tasks.interactors.todo.UpdateTodo
import com.example.tasks.ui.auth.LoginActivity
import com.example.tasks.ui.util.DialogDismissHelper
import com.example.tasks.ui.util.RecyclerItemTouchHelper
import com.example.tasks.util.Constants
import com.example.tasks.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TodosAdapter.Interaction, BottomSheetDismissListener,
    DialogDismissHelper {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var todosAdapter: TodosAdapter

    @Inject
    lateinit var sessionManager: SessionManager

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeObserver()
        setupRecyclerView()

        binding.fab.setOnClickListener {
            CreateTaskBottomSheet().show(supportFragmentManager, "Bottom Sheet")
        }

        binding.logOutButton.setOnClickListener {
            sessionManager.logout()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreInstanceState(savedInstanceState)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            todosAdapter = TodosAdapter(this@MainActivity, this@MainActivity)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == todosAdapter.itemCount.minus(1)) {
                        viewModel.nextPage()
                    }
                }
            })
            adapter = todosAdapter
            val itemTouchHelper = ItemTouchHelper(
                RecyclerItemTouchHelper(
                    this@MainActivity,
                    this@MainActivity,
                    this@MainActivity
                )
            )
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun subscribeObserver() {
        viewModel.dataState.observe(this) { dataState ->
            when (dataState) {
                is DataState.Success<List<Todo>?> -> {
                    displayProgressBar(false)

                    if (dataState.data != null) {
                        if ((dataState.data.size < viewModel.getPage() * Constants.PAGE_SIZE)
                            && !viewModel.isQueryExhausted()
                        ) {
                            viewModel.setQueryExhausted(true)
                        }

                        todosAdapter.submitList(dataState.data.reversed())
                        todosAdapter.notifyDataSetChanged()



                        if (dataState.data.isEmpty()) {
                            binding.recyclerView.gone()
                            binding.emptyTask.visible()
                        } else {
                            binding.emptyTask.gone()
                            binding.recyclerView.visible()
                        }
                    }

                    if (dataState.response != null) {
                        displaySnackBar(dataState.response)
                    }

                }
                is DataState.Error -> {
                    displayProgressBar(false)
                    displaySnackBar(dataState.message)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        }
        sessionManager.cachedUser.observe(this) {
            if (it == null) {
                navToAuthActivity()
            }
        }
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { inState ->
            (inState["page"] as Int?)?.let { page ->
                viewModel.setPage(page)
            }
            (inState["isQueryExhausted"] as Boolean?)?.let { isQueryExhausted ->
                viewModel.setQueryExhausted(isQueryExhausted)
            }
            (inState["layoutManagerState"] as Parcelable?)?.let { lmState ->
                viewModel.layoutManagerState = lmState
            }
        }

    }

    private fun navToAuthActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun displayProgressBar(isDisplayed: Boolean) {
        binding.progressBar.visibility = if (isDisplayed) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadFirstPage()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshQuery()
    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState()
    }

    private fun saveLayoutManagerState() {
        binding.recyclerView.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.layoutManagerState = lmState
        }
    }

    private fun displaySnackBar(message: String) {
        val snackbar = Snackbar.make(
            findViewById(R.id.mainActivity),
            message,
            Snackbar.LENGTH_LONG
        )

        snackbar.show()
    }

    override fun onItemSelected(position: Int, item: Todo) {

    }

    override fun restoreListPosition() {
        viewModel.layoutManagerState.let { lmState ->
            binding.recyclerView.layoutManager?.onRestoreInstanceState(
                lmState
            )
        }
    }

    override fun deleteTask(position: Int) {
        todosAdapter.getTodo(position)?.let {
            viewModel.setStateEvent(MainStateEvent.DeleteTodoEvent(it.id))
        }
        todosAdapter.notifyItemChanged(position)
    }

    override fun editTask(position: Int) {
        todosAdapter.notifyItemChanged(position)
        val item = todosAdapter.getTodo(position)
        val bundle = Bundle()
        bundle.putParcelable("todo", item)
        val fragment = CreateTaskBottomSheet()
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "Bottom Sheet")
    }

    override fun updateTaskStatus(todo: Todo) {
        viewModel.setStateEvent(MainStateEvent.UpdateTodoEvent(todo.id, todo.status, todo.task))
    }

    override fun onDismiss() {

    }

    override fun createTask(task: String) {

        viewModel.setStateEvent(MainStateEvent.CreateTodoEvent(0, task))

    }

    override fun updateTask(todo: Todo) {
        viewModel.setStateEvent(MainStateEvent.UpdateTodoEvent(todo.id, todo.status, todo.task))
    }

    override fun notifyItemInRecyclerView(position: Int) {
        todosAdapter.notifyItemChanged(position)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("page", viewModel.getPage())
        outState.putParcelable("layoutManagerState", viewModel.layoutManagerState)
        outState.putBoolean("isQueryExhausted", viewModel.isQueryExhausted())
        super.onSaveInstanceState(outState)
    }


}