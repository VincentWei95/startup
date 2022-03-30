package com.example.startup.launch

interface StartUpFinish {

    /**
     * 通知子任务，父任务已经执行完成
     */
    fun notifyChild(startup: Startup<*>)
}