package com.example.startup.launch

import java.util.concurrent.Executor

interface Dispatcher {

    /**
     * 是否在主线程运行
     */
    fun callCreateOnMainThread(): Boolean

    /**
     * 主线程是否等待该任务执行完成
     */
    fun waitOnMainThread(): Boolean

    /**
     * 等待
     */
    fun toWait()

    /**
     * 父任务执行完毕，计数器-1
     */
    fun toNotify()

    /**
     * 线程池
     */
    fun executor(): Executor

    /**
     * 线程优先级
     */
    fun getThreadPriority(): Int
}