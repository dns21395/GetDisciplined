package night.lines.todo.presentation.main

import com.arellomobile.mvp.InjectViewState
import io.reactivex.Observable
import night.lines.todo.R
import night.lines.todo.domain.repository.PreferencesRepository
import night.lines.todo.util.SchedulerProvider
import night.lines.todo.presentation.base.BasePresenter
import night.lines.todo.ui.main.AddTaskActionsRelay
import night.lines.todo.ui.main.MainNavigationRelay
import night.lines.todo.ui.main.ToolbarImages
import javax.inject.Inject

/**
 * Created by denisgabyshev on 18/03/2018.
 */
@InjectViewState
class MainPresenter @Inject constructor(private val schedulerProvider: SchedulerProvider,
                                        private val mainNavigationRelay: MainNavigationRelay,
                                        private val addTaskActionsRelay: AddTaskActionsRelay) : BasePresenter<MainView>() {

    private val TAG = "MainPresenter"

    @Inject lateinit var preferencesRepository: PreferencesRepository

    var bottomFrameLayoutId: Int = 0

    fun onViewPrepared() {
        setToolbarBackground()

        compositeDisposable.add(
                mainNavigationRelay.addTaskFragmentState
                        .compose(schedulerProvider.ioToMainObservableScheduler())
                        .subscribe {
                            handleAddTaskFragmentState(it)
                            checkFinishedItemsVisibility()
                        }
        )
    }

    private fun handleAddTaskFragmentState(enum: MainNavigationRelay.EnumAddTaskFragment) {
        when(enum) {
            MainNavigationRelay.EnumAddTaskFragment.SHOW -> viewState.showAddTaskFragment()
            else -> viewState.hideAddTaskFragment()
        }
    }

    private fun setToolbarBackground() {
        compositeDisposable.add(
                Observable.fromCallable { ToolbarImages.getBackground() }
                        .compose(schedulerProvider.ioToMainObservableScheduler())
                        .subscribe { viewState.setToolbarBackground(it) }
        )
    }

    private fun checkFinishedItemsVisibility() {
        compositeDisposable.add(
               preferencesRepository.getFinishedTasksVisibility().compose(schedulerProvider.ioToMainObservableScheduler())
                        .subscribe { updateIconCheckFinishedItemsVisibility(it) }
        )
    }

    private fun updateIconCheckFinishedItemsVisibility(visible: Boolean) {
        when(visible) {
            true -> viewState.updateIconCheckFinishedItemsVisibility(R.drawable.check_show)
            false -> viewState.updateIconCheckFinishedItemsVisibility(R.drawable.check_hide)
        }
    }

    fun setFinishedTasksVisibility() {
        compositeDisposable.add(
                preferencesRepository.getFinishedTasksVisibility()
                        .map {
                            preferencesRepository.setFinishedTasksVisibility(!it)
                        !it
                        }
                        .compose(schedulerProvider.ioToMainObservableScheduler())
                        .subscribe {
                            updateIconCheckFinishedItemsVisibility(it)
                            addTaskActionsRelay.callTaskFragmentAction(AddTaskActionsRelay.EnumTaskFragment.FINISHED_ITEMS_VISIBILITY_UPDATED)
                        }
        )
    }

    fun onFabButtonClicked() {
        mainNavigationRelay.callAddTaskFragmentAction(MainNavigationRelay.EnumAddTaskFragment.SHOW)
    }

    fun enumAddTaskFragmentHide() {
        mainNavigationRelay.callAddTaskFragmentAction(MainNavigationRelay.EnumAddTaskFragment.HIDE)
    }



}