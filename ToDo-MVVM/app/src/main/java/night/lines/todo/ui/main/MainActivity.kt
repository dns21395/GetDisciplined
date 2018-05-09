package night.lines.todo.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import night.lines.todo.BR
import night.lines.todo.R
import night.lines.todo.databinding.ActivityMainBinding
import night.lines.todo.presentation.main.MainActivityViewModel
import night.lines.todo.presentation.main.MainNavigator
import night.lines.todo.toothpick.DI
import night.lines.todo.toothpick.main.Main
import night.lines.todo.toothpick.main.MainModule
import night.lines.todo.ui.base.BaseActivity
import night.lines.todo.ui.main.addtask.AddTaskFragment
import night.lines.todo.ui.main.task.TaskFragment
import toothpick.Toothpick
import javax.inject.Inject

/**
 * Created by denisgabyshev on 18/03/2018.
 */

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>(), MainNavigator {
    override var layoutRes = R.layout.activity_main
    override var bindingVariable: Int = BR.viewModel

    @Inject lateinit var mainActivityViewModel: MainActivityViewModel

    override fun performDependencyInjection() {
        Toothpick.openScopes(DI.APP_SCOPE, Main::class.java).apply {
            Toothpick.inject(this@MainActivity, this)
            installModules(MainModule())
        }
    }

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel.navigator = this

        setMenu()

        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, TaskFragment())
                .commitAllowingStateLoss()

//        if(mainActivityViewModel.bottomFrameLayoutId != 0) createFrameLayout()

//        fab.setOnClickListener {
//            mainActivityViewModel.onFabButtonClicked()
//        }

        mainActivityViewModel.onViewPrepared()
    }

    override fun getViewModel(): MainActivityViewModel = mainActivityViewModel

    private fun createFrameLayout() {
        fab.hide()

        val bottomFrameLayout = FrameLayout(applicationContext)

        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        bottomFrameLayout.layoutParams = params

        if(mainActivityViewModel.bottomFrameLayoutId == 0) mainActivityViewModel.bottomFrameLayoutId = View.generateViewId()

        bottomFrameLayout.id = mainActivityViewModel.bottomFrameLayoutId

        parentConstraint.addView(bottomFrameLayout)

        val constraintSet = ConstraintSet()

        constraintSet.clone(parentConstraint)

        constraintSet.connect(mainActivityViewModel.bottomFrameLayoutId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM )

        constraintSet.connect(mainActivityViewModel.bottomFrameLayoutId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

        constraintSet.connect(mainActivityViewModel.bottomFrameLayoutId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.clear(coordinator.id, ConstraintSet.BOTTOM)

        constraintSet.connect(coordinator.id, ConstraintSet.BOTTOM, mainActivityViewModel.bottomFrameLayoutId, ConstraintSet.TOP)

        constraintSet.applyTo(parentConstraint)
    }

    override fun showAddTaskFragment() {
        createFrameLayout()

        supportFragmentManager.beginTransaction().replace(mainActivityViewModel.bottomFrameLayoutId, AddTaskFragment(), AddTaskFragment.TAG).commit()

        app_bar.setExpanded(false)
    }

    override fun hideAddTaskFragment() {
        val bottomFragment = supportFragmentManager.findFragmentByTag(AddTaskFragment.TAG)

        if(bottomFragment != null) supportFragmentManager.beginTransaction().remove(bottomFragment).commit()

        parentConstraint.removeView(findViewById(mainActivityViewModel.bottomFrameLayoutId))

        val constraintSet = ConstraintSet()
        constraintSet.clone(parentConstraint)

        constraintSet.clear(coordinator.id, ConstraintSet.BOTTOM)

        constraintSet.connect(coordinator.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constraintSet.applyTo(parentConstraint)

        mainActivityViewModel.bottomFrameLayoutId = 0

        fab.show()
    }

    override fun setToolbarBackground(background: Int) {
        toolbarBackground.setImageResource(background)
    }

    override fun onBackPressed() {
        if(mainActivityViewModel.bottomFrameLayoutId != 0) {
            hideAddTaskFragment()
            mainActivityViewModel.enumAddTaskFragmentHide()
        }
        else super.onBackPressed()
    }

    private fun setMenu() {
        toolbar.inflateMenu(R.menu.main)

        toolbar.setOnMenuItemClickListener {
            if(it.itemId == R.id.check) mainActivityViewModel.setFinishedTasksVisibility()
            false
        }
    }

    override fun updateIconCheckFinishedItemsVisibility(drawable: Int) {
        toolbar.menu.getItem(0).icon = resources.getDrawable(drawable, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isFinishing) Toothpick.closeScope(Main::class.java)
    }
}