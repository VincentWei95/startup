package com.example.startup.launch.manage

import com.example.startup.launch.Startup
import com.example.startup.launch.StartupResult
import java.util.concurrent.ConcurrentHashMap

/**
 * 任务缓存管理类
 */
object StartupCacheManager {
    private val mStartupResultCache = ConcurrentHashMap<Class<out Startup<*>>, StartupResult<*>>()

    fun saveStartupResult(startupCls: Class<out Startup<*>>, result: StartupResult<*>) {
        mStartupResultCache[startupCls] = result
    }

    fun hasStartupResult(startupCls: Class<out Startup<*>>): Boolean {
        return mStartupResultCache.containsKey(startupCls)
    }

    fun <T> getStartupResult(startupCls: Class<out Startup<T>>): StartupResult<T>? {
        val startupResult = mStartupResultCache[startupCls]
        if (startupResult != null) {
            return startupResult as StartupResult<T>
        }
        return null
    }

    fun remove(startupCls: Class<out Startup<*>>) {
        mStartupResultCache.remove(startupCls)
    }

    fun clear() {
        mStartupResultCache.clear()
    }
}