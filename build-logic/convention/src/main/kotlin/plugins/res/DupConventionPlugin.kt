package plugins.res

import com.ligh.plugin.ResDupTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class DupConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        ResDupTask.doAction(target)
    }
}