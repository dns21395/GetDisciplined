package com.example.denisgabyshev.getdisciplined.data

import android.content.Context
import com.example.denisgabyshev.getdisciplined.data.db.AppDatabase
import com.example.denisgabyshev.getdisciplined.data.db.model.Date
import com.example.denisgabyshev.getdisciplined.data.db.model.Task
import com.example.denisgabyshev.getdisciplined.di.ApplicationContext
import com.example.denisgabyshev.getdisciplined.utils.rx.SchedulerProvider
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import java.util.Date.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by denisgabyshev on 11/09/2017.
 */
@Singleton
class AppDataManager @Inject constructor(@ApplicationContext val context: Context,
                                         private val database: AppDatabase) : DataManager {
    override fun addDate(date: Long) {
        val _date = Date(0, date)

        Single.fromCallable {
            database.dateDao().insert(_date)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    override fun addTask(dateId: Long, task: String) {
        doAsync {
            val count = database.taskDao().getTaskCount(dateId)

            val _task = Task(0, dateId, task, count)

            Single.fromCallable {
                database.taskDao().insert(_task)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe()

        }
    }

     override fun getDateId(date: Long): Flowable<List<Date>> =
                database.dateDao().getDateId(date)


    override fun getTasksByDayId(date: Long): Flowable<List<Task>> =
            database.taskDao().getTasksByDayId(date)



}