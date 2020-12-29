package com.nristekk.apps.architecturelab

import android.app.Application
import com.nristekk.apps.architecturelab.database.LabDatabase
import com.nristekk.apps.architecturelab.database.task.UserRepos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class ApplicationLab : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { LabDatabase.getDatabase(this, applicationScope) }
    val userRepos by lazy { UserRepos(database.userDao()) }

}