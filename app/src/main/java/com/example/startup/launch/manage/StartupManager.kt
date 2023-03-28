package com.example.startup.launch.manage

import android.content.Context
import android.os.Looper
import com.example.startup.launch.*
import java.lang.RuntimeException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * 异步启动器
 */
class StartupManager private constructor(
    private val context: Context,
    private val startupList: List<AndroidStartUp<*>>,
    // 需要主线程等待异步任务执行完成
    // 实现等待任务完成后才进入首页的需求
    private val countDownLatch: CountDownLatch
) : StartUpFinish {
    private lateinit var startupSortStore: StartupSortStore

    fun start() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("please start in main thread")
        }

        // 有向无环图拓扑排序
        startupSortStore = TopologySort.sort(startupList)
        // 按排序完后的任务启动
        startupSortStore.result.forEach { startup ->
            val runnable = StartupRunnable(context, startup, this)
            if (startup.callCreateOnMainThread()) {
                runnable.run()
            } else {
                startup.executor().execute(runnable)
            }
        }
    }

    override fun notifyChild(startup: Startup<*>) {
        if (!startup.callCreateOnMainThread() && startup.waitOnMainThread()) {
            countDownLatch.countDown()
        }

        if (startupSortStore.startupChildMap.containsKey(startup.javaClass)) {
            val childStartupCls = startupSortStore.startupChildMap[startup.javaClass]
            childStartupCls?.forEach { childCls ->
                // 通知子任务父任务已完成
                startupSortStore.startupMap[childCls]?.toNotify()
            }
        }
    }

    fun await() {
        countDownLatch.await()
    }

    class Builder {
        private val startupList: MutableList<AndroidStartUp<*>> = mutableListOf()

        fun addStartup(startup: AndroidStartUp<*>): Builder {
            startupList.add(startup)
            return this
        }

        fun addStartupList(list: List<AndroidStartUp<*>>): Builder {
            startupList.addAll(list)
            return this
        }

        fun build(context: Context): StartupManager {
            val needAwaitCount = AtomicInteger()
            startupList.forEach { startup ->
                // 记录需要主线程等待完成的异步任务
                // 实现等待任务完成后才进入首页的需求
                if (!startup.callCreateOnMainThread() && startup.waitOnMainThread()) {
                    needAwaitCount.incrementAndGet()
                }
            }
            val countDownLatch = CountDownLatch(needAwaitCount.get())
            return StartupManager(context, startupList, countDownLatch)
        }
    }
}