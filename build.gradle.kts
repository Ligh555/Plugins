buildscript {
    repositories {
        maven {
            url = uri("./dupres/repo")
        }
        maven {
            url = uri("./tracemethod/repo/plugin")
        }
        maven {
            url = uri("./tracemethod/repo/ui")
        }
        mavenLocal()
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/jcenter")
        }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") } // 阿里云 Gradle 插件镜像
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin")
        } // 腾讯云
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") } // 华为云
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.0")
        classpath("com.google.code.gson:gson:2.8.0")
        classpath("com.ligh.res:dupres:1.0.0")
        classpath("com.ligh.tracemethod:plugin:1.0.0")
        classpath("com.bytedance.tools.lancet:lancet-plugin-asm9:1.0.1")
    }
}

allprojects {
    repositories {
        mavenLocal()
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/jcenter")
        }
        maven {
            url = uri("./tracemethod/repo/ui")
        }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") } // 阿里云 Gradle 插件镜像
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin")
        } // 腾讯云
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") } // 华为云
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
