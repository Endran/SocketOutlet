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

import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.dokka.gradle.DokkaTask

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

buildscript {

    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    }

    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.16-eap-3")
    }
}

plugins {
    kotlin("jvm")
}

apply {
    plugin("maven")
    plugin("org.jetbrains.dokka")
}

dependencies {
    compile("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:${ext["jacksonVersion"]}")
    compile("com.fasterxml.jackson.core:jackson-annotations:${ext["jacksonVersion"]}")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${ext["jacksonVersion"]}")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:${ext["jacksonVersion"]}")

    testCompile("org.jmockit:jmockit:${ext["jmockitVersion"]}")
    testCompile("org.assertj:assertj-core:${ext["assertjVersion"]}")
    testCompile("junit:junit:${ext["junitVersion"]}")
    testCompile("io.reactivex.rxjava2:rxkotlin:${ext["rxKotlinVersion"]}")
}

val dokkaJar = task<Jar>("dokkaJar") {
    dependsOn("dokka")
    classifier = "javadoc"
    from((tasks.getByName("dokka") as DokkaTask).outputDirectory)
}

val sourcesJar = task<Jar>("sourcesJar") {
    classifier = "sources"
    from(the<JavaPluginConvention>().sourceSets.getByName("main").allSource)
}

artifacts.add("archives", dokkaJar)
artifacts.add("archives", sourcesJar)
