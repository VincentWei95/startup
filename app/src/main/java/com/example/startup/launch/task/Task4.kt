package com.example.startup.launch.task

import android.content.Context
import android.util.Log
import com.example.startup.launch.AndroidStartUp
import com.example.startup.launch.Startup

class Task4 : AndroidStartUp<Unit>() {
    override fun create(context: Context) {
        Log.i("===test===", "学习http协议")
        Thread.sleep(1000)
        Log.i("====test===", "掌握http协议")
    }

    override fun dependencies(): List<Class<out Startup<*>>> {
        val classes = mutableListOf<Class<out Startup<*>>>()
        classes.add(Task2::class.java)
        return classes
    }
}