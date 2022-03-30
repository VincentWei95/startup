package com.example.startup.launch

import android.content.Context
import android.os.Process
import com.example.startup.launch.manage.StartupCacheManager

class StartupRunnable(
    private val context: Context,
    private val startup: Startup<*>,
    private val startupFinish: StartUpFinish
) : Runnable {
    override fun run() {
        Process.setThreadPriority(startup.getThreadPriority())
        startup.toWait()
        val result = startup.create(context)
        StartupCacheManager.saveStartupResult(startup.javaClass, StartupResult(result))
        startupFinish.notifyChild(startup)
    }
}