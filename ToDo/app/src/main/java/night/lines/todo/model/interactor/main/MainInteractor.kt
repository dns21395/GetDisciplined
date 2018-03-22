package night.lines.todo.model.interactor.main

import io.reactivex.Observable
import night.lines.todo.model.data.storage.Prefs
import night.lines.todo.model.repository.toolbarimages.ToolbarImages
import javax.inject.Inject

/**
 * Created by denisgabyshev on 21/03/2018.
 */
class MainInteractor @Inject constructor(private val toolbarImages: ToolbarImages,
                                         private val prefs: Prefs) {

    fun getBackground(): Observable<Int> = Observable.fromCallable {  toolbarImages.getBackground() }

    fun setFinishedTasksVisibility(status: Boolean) = prefs.setFinishedTasksVisibility(status)

    fun getFinishedTasksVisibility() = prefs.getFinishedTasksVisibility()
}