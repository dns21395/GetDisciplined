package night.lines.todo.ui.main.addtask

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import night.lines.todo.dagger.MainScope
import night.lines.todo.dagger.application.ForApplication
import javax.inject.Inject

/**
 * Created by denisgabyshev on 20/03/2018.
 */
class AddTaskFragmentRelay @Inject constructor() {

    private val addTaskFragmentStateRelay = PublishRelay.create<EnumAddTaskFragment>()

    val addTaskFragmentState: Observable<EnumAddTaskFragment> = addTaskFragmentStateRelay

    fun callAddTaskFragmentAction(action: EnumAddTaskFragment) {
        addTaskFragmentStateRelay.accept(action)
    }

    enum class EnumAddTaskFragment {
        SHOW,
        HIDE
    }



}