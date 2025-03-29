plugins {
    `kotlin-dsl`
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
            id = "com.ligh.test"
            implementationClass = "cn.cxzheng.tracemanplugin.TraceManPlugin"
            description = "A plugin for test"
            displayName = "Test Plugin"
        }
    }
}

sourceSets {
    main {
        //traceMethod
        java.srcDirs("../tracemethod/plugin/src/main")
        groovy.srcDirs("../tracemethod/plugin/src/main")
        resources.srcDirs("../tracemethod/plugin/src/main/resourcess")
    }
}
