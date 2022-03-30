package com.example.startup.launch

import java.util.ArrayDeque

object TopologySort {

    fun sort(startupList: List<Startup<*>>): StartupSortStore {
        // 入度表
        val inDegreeMap = mutableMapOf<Class<out Startup<*>>, Int>()
        // 入度为0的任务队列
        val zeroDeque = ArrayDeque<Class<out Startup<*>>>()

        val startupMap = mutableMapOf<Class<out Startup<*>>, Startup<*>>()
        // 任务依赖表
        val startupChildMap = mutableMapOf<Class<out Startup<*>>, MutableList<Class<out Startup<*>>>>()

        // 找出图中入度为0的顶点
        startupList.forEach { startup ->
            startupMap[startup.javaClass] = startup

            // 构建入度表
            // 记录每个任务的入度数（依赖的任务数）
            val dependenciesCount = startup.getDependenciesCount()
            inDegreeMap[startup.javaClass] = dependenciesCount

            // 记录入度数（依赖的任务数）为0的任务
            if (dependenciesCount == 0) {
                zeroDeque.offer(startup.javaClass)
            } else {
                // 构建任务依赖表
                // 遍历本任务的依赖（父）任务列表
                startup.dependencies().forEach { parent ->
                    var child = startupChildMap[parent]
                    if (child == null) {
                        child = mutableListOf()
                        startupChildMap[parent] = child
                    }
                    child.add(startup.javaClass)
                }
            }
        }

        // 依次在图中删除顶点
        val result = mutableListOf<Startup<*>>()
        val mainStartupList = mutableListOf<Startup<*>>()
        val threadStartupList = mutableListOf<Startup<*>>()
        while (!zeroDeque.isEmpty()) {
            val parentCls = zeroDeque.poll()
            val startup = startupMap[parentCls]!!
            if (startup.callCreateOnMainThread()) {
                mainStartupList.add(startup)
            } else {
                threadStartupList.add(startup)
            }

            // 删除后再找出现在入度为0的顶点
            if (startupChildMap.containsKey(parentCls)) {
                val childClsList = startupChildMap[parentCls]
                childClsList?.forEach { childCls ->
                    val num = inDegreeMap[childCls]!!
                    inDegreeMap[childCls] = num - 1
                    if (num - 1 == 0) {
                        zeroDeque.offer(childCls)
                    }
                }
            }
        }

        result.apply {
            // 先添加子线程的任务，再添加主线程任务
            // 避免要运行在主线程的同步任务先执行导致阻塞下一个要被执行的异步任务启动
            addAll(threadStartupList)
            addAll(mainStartupList)
        }

        return StartupSortStore(result, startupMap, startupChildMap)
    }
}