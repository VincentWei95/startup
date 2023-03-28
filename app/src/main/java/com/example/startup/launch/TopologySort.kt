package com.example.startup.launch

import java.util.ArrayDeque

object TopologySort {

    /**
     * 有向无环图拓扑排序的目的：
     * 其实就是被依赖的任务（即入度为 0 的任务）排在执行队列前面先启动
     * 等这些被依赖的任务启动完了，就让依赖的任务入队启动，直到结束。
     *
     * 目的就是充足利用 CPU 资源尽可能有效的启动任务执行
     *
     * 举个例子：
     * 假设 task1 是入度为 0 的任务，被 task2 和 task3 依赖
     * task1 因为入度为 0 会先入队启动，等 task1 启动完后，找到 task2 和 task3 依次启动
     */
    fun sort(startupList: List<Startup<*>>): StartupSortStore {
        // 入度表
        // 入度表的作用：按列表顺序记录入度为0的任务
        val inDegreeMap = mutableMapOf<Class<out Startup<*>>, Int>()
        // 入度为0的任务队列，任务执行队列
        val zeroDeque = ArrayDeque<Class<out Startup<*>>>()

        val startupMap = mutableMapOf<Class<out Startup<*>>, Startup<*>>()
        // 任务依赖表
        // 任务依赖表的作用：用于任务查询
        // 假设 task1 是入度为 0 的任务，被 task2 和 task3 依赖
        // 入度为 0 的 task1 被启动后，可以通过任务依赖表查询到 task2 和 task3
        // 将它们的入度数-1，此时 task2 和 task3 的入度就为 0，则放入 zeroDeque 执行队列启动执行
        val startupChildMap = mutableMapOf<Class<out Startup<*>>, MutableList<Class<out Startup<*>>>>()

        // 找出图中入度为0的顶点
        startupList.forEach { startup ->
            // 记录所有的任务
            startupMap[startup.javaClass] = startup

            // 构建入度表
            // 记录每个任务的入度数（依赖的任务数）
            val dependenciesCount = startup.getDependenciesCount()
            inDegreeMap[startup.javaClass] = dependenciesCount

            // 记录入度数（依赖的任务数）为 0 的任务
            // 入度为 0 的任务先进入排在执行队列前面
            if (dependenciesCount == 0) {
                zeroDeque.offer(startup.javaClass)
            } else {
                // 构建任务依赖表
                // 遍历本任务的依赖（父）任务列表
                startup.dependencies().forEach { parentTask ->
                    var childTasks = startupChildMap[parentTask]
                    if (childTasks == null) {
                        childTasks = mutableListOf()
                        startupChildMap[parentTask] = childTasks
                    }
                    childTasks.add(startup.javaClass)
                }
            }
        }

        val result = mutableListOf<Startup<*>>()
        val mainStartupList = mutableListOf<Startup<*>>()
        val threadStartupList = mutableListOf<Startup<*>>()
        // 依次在图中删除顶点
        while (!zeroDeque.isEmpty()) {
            val parentTask = zeroDeque.poll()
            val startup = startupMap[parentTask]!!
            // 将入度为 0 的任务添加到列表
            if (startup.callCreateOnMainThread()) {
                mainStartupList.add(startup)
            } else {
                threadStartupList.add(startup)
            }

            // 如果启动执行的任务没有子任务，重新进入循环去执行队列拿下一个任务执行

            // 如果启动执行的任务有子任务
            if (startupChildMap.containsKey(parentTask)) {
                // 任务依赖表查找父任务是否有依赖的子任务
                val childTasks = startupChildMap[parentTask]
                childTasks?.forEach { childTask ->
                    val num = inDegreeMap[childTask]!!
                    inDegreeMap[childTask] = num - 1 // 将子任务的入度数-1
                    if (num - 1 == 0) {
                        zeroDeque.offer(childTask) // 子任务入度为 0，添加到执行队列
                    }
                }
            }
        }

        result.apply {
            // 先添加子线程的任务，再添加主线程任务
            // 避免要运行在主线程的同步任务有阻塞任务，任务先执行导致阻塞下一个要被执行的异步任务启动
            addAll(threadStartupList)
            addAll(mainStartupList)
        }

        return StartupSortStore(result, startupMap, startupChildMap)
    }
}