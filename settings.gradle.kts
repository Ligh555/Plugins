pluginManagement {
    includeBuild("build-logic")
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
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") } // 阿里云 Gradle 插件镜像
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin") } // 腾讯云
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") } // 华为云
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") } // 阿里云 Gradle 插件镜像
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin") } // 腾讯云
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") } // 华为云
    }
}

rootProject.name = "Plugins"
include(":app")
