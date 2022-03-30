package com.example.startup.launch.task

import android.content.Context
import android.util.Log
import com.example.startup.launch.AndroidStartUp
import com.example.startup.launch.Startup

class Task5 : AndroidStartUp<Unit>() {
    override fun create(context: Context) {
        Log.i("===test===", "学习okkttp")
        Thread.sleep(1000)
        Log.i("====test===", "掌握okhttp")
    }

    override fun dependencies(): List<Class<out Startup<*>>> {
        val classes = mutableListOf<Class<out Startup<*>>>()
        classes.add(Task3::class.java)
        classes.add(Task4::class.java)
        return classes
    }
}