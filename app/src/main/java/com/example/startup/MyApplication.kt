package com.example.startup

import android.app.Application
import com.example.startup.launch.manage.StartupManager
import com.example.startup.launch.task.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StartupManager.Builder()
            .addStartup(Task2())
            .addStartup(Task1())
            .addStartup(Task5())
            .addStartup(Task4())
            .addStartup(Task3())
            .build(this)
            .start()
    }
}