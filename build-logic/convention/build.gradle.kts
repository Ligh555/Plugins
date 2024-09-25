/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.google.samples.apps.nowinandroid.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(project(":plugins:resdup"))
}

gradlePlugin {
    plugins {
        val configPath = "config."
        register("androidApplication") {
            id = "config.android.application"
            implementationClass = configPath + "AndroidApplicationConventionPlugin"
        }
        register("androidFlavors") {
            id = "config.android.application.flavors"
            implementationClass = configPath + "AndroidApplicationFlavorsConventionPlugin"
        }
        val customPath = "plugins."
        register("demo") {
            id = "plugins.demo"
            implementationClass = customPath + "demo.DemoPlugin"
        }
        register("resdup") {
            id = "plugins.res.dup"
            implementationClass = customPath + "res.DupConventionPlugin"
        }
    }
}
