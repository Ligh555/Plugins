package plugins.demo

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class DemoPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("hello11")
        target.afterEvaluate {
            val byType = target.extensions.getByType(AppExtension::class.java)
            byType.applicationVariants.forEach { variant ->
                val variantName = variant.name.capitalize()
                val processResource = project.tasks.getByName("process${variantName}Resources")
                processResource.doLast {
                    println("hahahhaha")
                }
            }
        }
    }
}