package com.example.startup.launch

data class StartupSortStore(
    val result: List<Startup<*>>,
    val startupMap: Map<Class<out Startup<*>>, Startup<*>>,
    val startupChildMap: Map<Class<out Startup<*>>, MutableList<Class<out Startup<*>>>>
)