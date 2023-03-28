package com.example.startup.launch.manage

import java.util.concurrent.*

/**
 * 线程池管理类
 */
object ExecutorManager {
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4))
    private val MAX_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val KEEP_ALIVE_TIME = 30L

    // CPU 密集型使用线程池
    val cpuExecutor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.SECONDS,
        LinkedBlockingDeque(10), // 要设置最大线程池数
        Executors.defaultThreadFactory()
    ).apply {
        allowCoreThreadTimeOut(true)
    }

    // IO 密集型使用线程池
    val ioExecutor: ExecutorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory())
}