package com.ligh.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.internal.tasks.OptimizeResourcesTask
import org.gradle.api.Project
import pink.madis.apk.arsc.ResourceFile
import pink.madis.apk.arsc.ResourceTableChunk
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object ResDupTask {

    private const val RESOURCE_NAME = "resources.arsc"
    private const val CONFIG_NAME = "Config"
    private const val REPEAT_RES_CONFIG_NAME = "RepeatResConfig"

    private const val REPEAT_RES_MAPPING = "RepeatResMapping.txt"


    fun doAction(project: Project) {

        // 重复资源配置
        project.extensions.create(REPEAT_RES_CONFIG_NAME, RepeatResConfig::class.java)

        val hasAppPlugin = project.plugins.hasPlugin(AppPlugin::class.java)
        if (hasAppPlugin) {
            project.afterEvaluate {
                kotlin.runCatching {
                    FileUtil.setRootDir(project.rootDir.path)
                    print("PluginTest Config " + project.extensions.findByName(CONFIG_NAME))
                    val repeatResConfig =
                        project.extensions.findByName(REPEAT_RES_CONFIG_NAME) as? RepeatResConfig


                    val byType = project.extensions.getByType(AppExtension::class.java)

                    byType.applicationVariants.forEach { variant ->
                        val variantName = variant.name.capitalize()
                        var isShirk = false
                        val shirkTask =
                            project.tasks.findByName("optimize${variantName}Resources") as? OptimizeResourcesTask
                        shirkTask?.let { task ->
                            isShirk = true
                            task.doLast {
                                this as OptimizeResourcesTask
                                this.optimizedProcessedRes.asFile.get().listFiles()
                                    ?.filter { file ->
                                        file.name.endsWith(".ap_")
                                    }?.forEach { apFile ->
                                        reMapping(project, apFile, repeatResConfig)
                                    }
                            }
                        }
                        if (!isShirk) {
                            val processResource =
                                project.tasks.getByName("process${variantName}Resources")
                            // 获取资源打包输出的文件夹，
                            val resourcesTask =
                                processResource as LinkApplicationAndroidResourcesTask
                            processResource.doLast {
                                val files = resourcesTask.resPackageOutputFolder.asFileTree.files
                                files.filter { file ->
                                    file.name.endsWith(".ap_")
                                }.forEach { apFile ->
                                    reMapping(project, apFile, repeatResConfig)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun reMapping(project: Project, apFile: File, repeatResConfig: RepeatResConfig?) {
        val mapping =
            "${project.buildDir}${File.separator}ResDeduplication${File.separator}mapping${File.separator}"
        File(mapping).takeIf { fileMapping ->
            !fileMapping.exists()
        }?.apply {
            mkdirs()
        }

        val originalLength = apFile.length()
        val resCompressFile = File(mapping, REPEAT_RES_MAPPING)
        val unZipPath = "${apFile.parent}${File.separator}resCompress"
        ZipFile(apFile).unZipFile(unZipPath)

        // 删除重复图片
        deleteRepeatRes(
            unZipPath,
            resCompressFile,
            apFile,
            repeatResConfig?.whiteListName
        )
        apFile.delete()


        println("---f_tag, file name = ${apFile.name}")
        ZipOutputStream(apFile.outputStream()).use { output ->
            output.zip(unZipPath, File(unZipPath))
        }

        val lastLength = apFile.length()
        print("优化结束缩减：${lastLength - originalLength}")
        deleteDir(File(unZipPath))
    }

    private fun deleteDir(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return false
        }
        if (file.isFile) {
            file.delete()
        } else if (file.isDirectory) {
            val files = file.listFiles()
            for (i in files.indices) {
                deleteDir(files[i])
            }
        }
        file.delete()
        return true
    }

    private fun deleteRepeatRes(
        unZipPath: String,
        mappingFile: File,
        apFile: File,
        ignoreName: MutableList<String>?
    ) {

        val fileWriter = FileWriter(mappingFile)
        val groupsResources = ZipFile(apFile).groupsResources()

        val arscFile = File(unZipPath, RESOURCE_NAME)
        val newResource = FileInputStream(arscFile).use { input ->
            val fromInputStream = ResourceFile.fromInputStream(input)
            groupsResources.asSequence().filter {
                it.value.size > 1
            }.filter { entry ->
                val name = File(entry.value[0].name).name
                ignoreName?.contains(name)?.let {
                    !it
                } ?: true
            }.forEach { zipMap ->
                val zips = zipMap.value

                val coreResources = zips[0]

                for (index in 1 until zips.size) {

                    val repeatZipFile = zips[index]
                    fileWriter.synchronizedWriteString("${repeatZipFile.name} => ${coreResources.name}")

                    File(unZipPath, repeatZipFile.name).delete()

                    fromInputStream
                        .chunks
                        .asSequence()
                        .filter {
                            it is ResourceTableChunk
                        }
                        .map {
                            it as ResourceTableChunk
                        }
                        .forEach { chunk ->
                            val stringPoolChunk = chunk.stringPool
                            val index = stringPoolChunk.indexOf(repeatZipFile.name)
                            if (index != -1) {
                                stringPoolChunk.setString(index, coreResources.name)
                            }
                        }
                }

            }


            fileWriter.close()
            fromInputStream
        }

        arscFile.delete()

        FileOutputStream(arscFile).use {
            it.write(newResource.toByteArray())
        }

    }

}