package cn.cxzheng.tracemanplugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class TraceManTransform(private val project: Project) : Transform() {

    override fun transform(transformInvocation: TransformInvocation) {
        println("[MethodTraceMan]: transform()")

        // 读取配置
        val traceConfig = initConfig()
        traceConfig.parseTraceConfigFile()

        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider
        outputProvider?.deleteAll()

        // 遍历
        inputs.forEach { input ->
            input.directoryInputs.forEach { directoryInput ->
                traceSrcFiles(directoryInput, outputProvider, traceConfig)
            }

            input.jarInputs.forEach { jarInput ->
                traceJarFiles(jarInput, outputProvider, traceConfig)
            }
        }
    }

    private fun initConfig(): Config {
        val configuration =
            project.extensions.getByName("traceMan") as? TraceManConfig ?: TraceManConfig()
        val config = Config()
        config.mTraceConfigFile = configuration.traceConfigFile
        config.mIsNeedLogTraceInfo = configuration.logTraceInfo
        return config
    }

    override fun getName(): String {
        return "traceManTransform"
    }

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    companion object {
        fun traceSrcFiles(
            directoryInput: DirectoryInput,
            outputProvider: TransformOutputProvider?,
            traceConfig: Config
        ) {
            if (directoryInput.file.isDirectory) {
                directoryInput.file.walk().forEach { file ->
                    val name = file.name
                    if (traceConfig.isNeedTraceClass(name)) {
                        val classReader = ClassReader(file.readBytes())
                        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        val cv = TraceClassVisitor(Opcodes.ASM7, classWriter, traceConfig)
                        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                        val code = classWriter.toByteArray()
                        FileOutputStream(file.parentFile.absolutePath + File.separator + name).use { fos ->
                            fos.write(code)
                        }
                    }
                }
            }

            // 处理完输出给下一任务作为输入
            val dest = outputProvider?.getContentLocation(
                directoryInput.name,
                directoryInput.contentTypes,
                directoryInput.scopes,
                Format.DIRECTORY
            )
            if (dest != null) {
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }

        fun traceJarFiles(
            jarInput: JarInput,
            outputProvider: TransformOutputProvider?,
            traceConfig: Config
        ) {
            if (jarInput.file.absolutePath.endsWith(".jar")) {
                // 重命名输出文件,因为可能同名,会覆盖
                var jarName = jarInput.name
                val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }
                val jarFile = JarFile(jarInput.file)
                val enumeration = jarFile.entries()

                val tmpFile = File(jarInput.file.parent + File.separator + "classes_temp.jar")
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }

                JarOutputStream(FileOutputStream(tmpFile)).use { jarOutputStream ->
                    // 循环jar包里的文件
                    while (enumeration.hasMoreElements()) {
                        val jarEntry = enumeration.nextElement() as JarEntry
                        val entryName = jarEntry.name
                        val zipEntry = ZipEntry(entryName)
                        jarFile.getInputStream(jarEntry).use { inputStream ->
                            if (traceConfig.isNeedTraceClass(entryName)) {
                                jarOutputStream.putNextEntry(zipEntry)
                                val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                                val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                                val cv = TraceClassVisitor(Opcodes.ASM7, classWriter, traceConfig)
                                classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                                val code = classWriter.toByteArray()
                                jarOutputStream.write(code)
                            } else {
                                jarOutputStream.putNextEntry(zipEntry)
                                jarOutputStream.write(IOUtils.toByteArray(inputStream))
                            }
                            jarOutputStream.closeEntry()
                        }
                    }
                }

                jarFile.close()

                // 处理完输出给下一任务作为输入
                val dest = outputProvider?.getContentLocation(
                    "$jarName$md5Name",
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                if (dest != null) {
                    FileUtils.copyFile(tmpFile, dest)
                }

                tmpFile.delete()
            }
        }
    }
}
