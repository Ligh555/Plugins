plugins {
    `kotlin-dsl`
    `maven-publish`
}

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
        setUrl("https://maven.aliyun.com/repository/gradle-plugin")
    }
    maven {
        setUrl("https://jitpack.io")
    }
    google()
    mavenCentral()

}
dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("commons-codec:commons-codec:1.15")
    implementation("commons-io:commons-io:2.11.0")
}

gradlePlugin {
    plugins {
        register("test") {
            id = "com.ligh.tracemethod"
            implementationClass = "cn.cxzheng.tracemanplugin.TraceManPlugin"
            description = "方法耗时分析插桩插件"
            displayName = "方法耗时插桩"
        }
    }
}

sourceSets {
    main {
        //traceMethod
        java.srcDirs("../tracemethod/plugin/src/main")
        resources.srcDirs("../tracemethod/plugin/src/main/resourcess")
    }
}


publishing {
    publications {
        register<MavenPublication>("publish") {
            repositories {
                maven {
                    name = "local"
                    url = uri("file://${project.rootDir}/tracemethod/repo/plugin")
                }
            }
            groupId = "com.ligh.tracemethod"
            artifactId = "plugin"
            version = "1.0.0"

            from(components["java"])
        }
    }
}
