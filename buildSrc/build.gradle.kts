/*
import org.gradle.kotlin.dsl.`kotlin-dsl`
*/

plugins {
    /**
     * [Kotlin DSL Plugin]
     * (https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin)
     */
    `kotlin-dsl`
}

repositories {
    jcenter()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}