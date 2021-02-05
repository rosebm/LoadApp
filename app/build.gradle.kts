
plugins {
    id(Plugins.androidApp)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinAndroidExtensions)
}

android {
    compileSdkVersion(Config.compileSdk)

    defaultConfig {
        applicationId = "com.rosalynbm"
        minSdkVersion (Config.minSdk)
        targetSdkVersion (Config.targetSdk)
        versionCode = Config.versionCode
        versionName = Config.versionName
        testInstrumentationRunner = Config.testInstrumentationRunner
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

dependencies {
    implementation(Libs.androidxAppCompact)
    implementation(Libs.androidxCore)
    implementation(Libs.androidxConstraint)
    implementation(Libs.googleMaterial)
    implementation(Libs.kotlinJdk8)
    implementation(Libs.timber)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
