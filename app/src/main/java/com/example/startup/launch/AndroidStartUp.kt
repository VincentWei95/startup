package com.example.startup.launch

import android.os.Process
import com.example.startup.launch.manage.ExecutorManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

abstract class AndroidStartUp<T> : Startup<T> {
    private val mCountDownLatch = CountDownLatch(getDependenciesCount())

    override fun dependencies(): List<Class<out Startup<*>>> {
        return emptyList()
    }

    final override fun getDependenciesCount(): Int {
        return dependencies().size
    }

    override fun callCreateOnMainThread(): Boolean {
        return false
    }

    override fun waitOnMainThread(): Boolean {
        return false
    }

    override fun executor(): Executor {
        return ExecutorManager.ioExecutor
    }

    override fun toWait() {
        mCountDownLatch.await()
    }

    override fun toNotify() {
        mCountDownLatch.countDown()
    }

    override fun getThreadPriority(): Int {
        return Process.THREAD_PRIORITY_DEFAULT
    }
}