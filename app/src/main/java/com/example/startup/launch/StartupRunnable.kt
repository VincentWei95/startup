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
        // 如果该任务被子任务依赖，阻塞等待，直到所有子任务执行完成才放行
        startup.toWait()
        // 执行任务内容
        val result = startup.create(context)
        StartupCacheManager.saveStartupResult(startup.javaClass, StartupResult(result))
        // 执行结束，通知执行完成
        // 如果该任务是子任务，则通知父任务锁存器-1
        startupFinish.notifyChild(startup)
    }
}