package com.example.startup.launch.task

import android.content.Context
import android.util.Log
import com.example.startup.launch.AndroidStartUp
import com.example.startup.launch.Startup

class Task2 : AndroidStartUp<Unit>() {
    override fun create(context: Context) {
        Log.i("===test===", "学习socket")
        Thread.sleep(1000)
        Log.i("====test===", "掌握socket")
    }

    override fun dependencies(): List<Class<out Startup<*>>> {
        val classes = mutableListOf<Class<out Startup<*>>>()
        classes.add(Task1::class.java)
        return classes
    }
}