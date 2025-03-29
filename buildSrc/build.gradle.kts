// buildSrc/build.gradle.kts
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("com.android.tools.build:gradle:$androidGradlePluginVersion")
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
}

gradlePlugin {
    plugins {
        register("test") {
            id = "com.ligh.test"
            implementationClass = "com.ligh.plugin.TestPlugin"
            description = "A plugin for test"
            displayName = "Test Plugin"
        }
    }
}