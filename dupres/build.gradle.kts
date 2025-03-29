// buildSrc/build.gradle.kts
plugins {
//    //Plugin用Java语言编写时需添加
//    `java-gradle-plugin` //等同于 id("java-gradle-plugin") apply true
//
//    //Plugin用Groovy语言编写时需添加
//    `groovy` //等同于 id("groovy") apply true

    //Plugin用Kotlin语言编写时需添加
    `kotlin-dsl` //等同于 id("org.gradle.kotlin.kotlin-dsl") version "4.1.2"
    // 也可以用 id("org.jetbrains.kotlin.jvm") version "1.9.10" apply true (不建议，这个只有Kotlin的语法，而没有Kotlin DSL的语法)

    `maven-publish`

}

val kotlinVersion = "1.7.20"
val androidGradlePluginVersion = "7.4.2"

repositories {
    mavenLocal()
    // 使用阿里云镜像加速
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    // 备用镜像
    maven("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin")
    maven("https://repo.huaweicloud.com/repository/maven")
    // 官方仓库作为后备
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // 添加源码依赖 (Gradle API)
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("com.android.tools.build:gradle:$androidGradlePluginVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // AGP 源码
    compileOnly("com.android.tools.build:gradle:$androidGradlePluginVersion")
    compileOnly("com.android.tools.build:builder:$androidGradlePluginVersion")
    compileOnly("com.android.tools.build:gradle-api:$androidGradlePluginVersion")

    // 包含源码的版本 (使用 sources classifier)
    implementation("com.android.tools.build:gradle:$androidGradlePluginVersion:sources")


    //dupres
    implementation("pink.madis.apk.arsc:android-chunk-utils:0.0.7")

    //traceMethod
    implementation("org.javassist:javassist:3.29.2-GA")
}

gradlePlugin {
    plugins {
        register("test") {
            id = "com.ligh.dupres"
            implementationClass = "com.ligh.plugin.ResPlugin"
            description = "重复资源删减插件"
            displayName = "重复资源删减"
        }
    }
}

sourceSets {
    main {
        //dupres
        java.srcDirs("../dupres/src/main/java")
        resources.srcDirs("tracemethod/plugin/src/main/resourcess")
    }
}

publishing {
    publications {
        register<MavenPublication>("publish") {
            repositories {
                maven {
                    name = "local"
                    url = uri("file://${project.rootDir}/dupres/repo")
                }
            }
            groupId = "com.ligh.res"
            artifactId = "dupres"
            version = "1.0.0"

            from(components["java"])
        }
    }
}

