package com.data.repository

import android.util.Log
import kotlinx.coroutines.Job

open class JobManager(
    private val className: String
) {
    private val jobs: HashMap<String, Job> = HashMap()

    fun addJob(methodName: String, job: Job) {
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String) {
        getJob(methodName)?.cancel()
    }

    fun getJob(methosName: String): Job? {
        if (jobs
                .containsKey(methosName)
        ) {
            jobs[methosName]?.let { return it }
        }
        return null
    }

    fun cancelAllActiveJobs() {
        for ((methodName, job) in jobs) {
            if (job.isActive) {
                Log.e(
                    "AppDebug",
                    "cancelAllActtiveJobs: $className: cancelling job in $methodName"
                )
                job.cancel()
            }
        }
    }
}