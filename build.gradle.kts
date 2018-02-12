/*
 * Copyright (c) 2017 David Hardy.
 * Copyright (c) 2017 Craftsmenlabs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.JavaVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.daemon.client.KotlinCompilerClient.compile
import org.jetbrains.kotlin.load.kotlin.isContainedByCompiledPartOfOurModule
import java.net.URI
import org.gradle.plugins.ide.idea.model.IdeaModule

buildscript {

    val gradleVersionsVersion = "0.17.0"

    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.ben-manes:gradle-versions-plugin:$gradleVersionsVersion")
    }
}

apply {
    plugin("com.github.ben-manes.versions")
}

plugins {
    base
    idea
    kotlin("jvm") version "1.2.21" apply false
}

repositories {
    jcenter()
}

configure<IdeaModel> {
    project {
        languageLevel = IdeaLanguageLevel(JavaVersion.VERSION_1_8)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        inheritOutputDirs = false
        outputDir = file("$buildDir/classes/main/")
    }
}

subprojects {

    repositories {
        jcenter()
        maven { setUrl("https://jitpack.io") }
    }

    plugins {
        kotlin("jvm") version "1.2.21" apply false
    }

    group = "org.craftsmenlabs"
    version = "0.2.0-SNAPSHOT"
    ext["projectVersion"] = version

    ext["assertjVersion"] = "3.9.0"
    ext["gsonVersion"] = "2.8.2"
    ext["jacksonVersion"] = "2.8.7"
    ext["jmockitVersion"] = "1.38"
    ext["junitVersion"] = "4.12"
    ext["rxJavaVersion"] = "2.1.9"
    ext["rxKotlinVersion"] = "2.2.0"
    ext["socketOutletVersion"] = "aec78e4456"

    dependencies {
        // This I really want
    }
}

dependencies {
    subprojects.forEach {
        archives(it)
    }
}
