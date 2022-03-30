package com.example.startup.launch.manage

import android.os.Handler
import java.util.concurrent.*

/**
 * 线程池管理类
 */
object ExecutorManager {
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOl_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))
    private val MAX_POOL_SIZE = CORE_POOl_SIZE
    private const val KEEP_ALIVE_TIME = 5L

    // CPU 密集型使用线程池
    val cpuExecutor: ThreadPoolExecutor
    // IO 密集型使用线程池
    val ioExecutor: ExecutorService
    val mainExecutor: Executor
    private val mainHandler = Handler()

    init {
        cpuExecutor = ThreadPoolExecutor(
            CORE_POOl_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            LinkedBlockingDeque(),
            Executors.defaultThreadFactory()
        ).apply {
            allowCoreThreadTimeOut(true)
        }

        ioExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory())
        mainExecutor = Executor {
            mainHandler.post {
                it.run()
            }
        }
    }
}