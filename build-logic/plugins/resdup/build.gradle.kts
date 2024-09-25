plugins {
    `kotlin-dsl`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("res") {
            id = "com.ligh.res"
            implementationClass = "com.ligh.plugin.MyPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:8.1.1")
    implementation("pink.madis.apk.arsc:android-chunk-utils:0.0.7")
}

publishing {
    publications {
        register<MavenPublication>("publish") {
            groupId = "com.ligh.res"
            artifactId = "dup"
            version = "3.0.0-SNAPSHOT"
            from(components["java"])
        }
    }
}


