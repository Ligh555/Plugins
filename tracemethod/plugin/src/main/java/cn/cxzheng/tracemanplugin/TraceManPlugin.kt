package cn.cxzheng.tracemanplugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TraceManPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("*****************MethodTraceMan Plugin apply*********************")
        target.extensions.create("traceMan", TraceManConfig::class.java)
        val android = target.extensions.getByType(AppExtension::class.java)
        android.registerTransform(TraceManTransform(target))

    }
}