package com.example.startup.launch

import android.content.Context

interface Startup<T> : Dispatcher {

    /**
     * 任务处理逻辑
     */
    fun create(context: Context): T

    /**
     * 该任务依赖的其他任务Class
     */
    fun dependencies(): List<Class<out Startup<*>>>

    /**
     * 该任务依赖其他任务的数量
     */
    fun getDependenciesCount(): Int
}