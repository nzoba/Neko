import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Configs {
    const val applicationId = "tachiyomi.mangadex"
    const val compileSdkVersion = 31
    const val minSdkVersion = 24
    const val targetSdkVersion = 30
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val versionCode = 141
    const val versionName = "2.8.0.2"
}

fun getBuildTime() = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now(ZoneOffset.UTC))
fun getCommitCount() = runCommand("git rev-list --count HEAD")
fun getGitSha() = runCommand("git rev-parse --short HEAD")
fun runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = command.split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services") apply false
}

if (gradle.startParameter.taskRequests.toString().contains("Standard")) {
    apply(mapOf("plugin" to "com.google.gms.google-services"))
}


android {
    compileSdk = Configs.compileSdkVersion

    defaultConfig {
        minSdk = Configs.minSdkVersion
        targetSdk = Configs.targetSdkVersion
        applicationId = Configs.applicationId
        versionCode = Configs.versionCode
        versionName = Configs.versionName
        testInstrumentationRunner = Configs.testInstrumentationRunner
        multiDexEnabled = true
        setProperty("archivesBaseName", "Neko")
        buildConfigField("String", "COMMIT_COUNT", "\"${getCommitCount()}\"")
        buildConfigField("String", "COMMIT_SHA", "\"${getGitSha()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")
        buildConfigField("Boolean", "INCLUDE_UPDATER", "false")

        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86")
        }

    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            /* isShrinkResources = true
             isMinifyEnabled = true

             proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
             configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                 mappingFileUploadEnabled = false
             }*/
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compose.version.get()
    }

    flavorDimensions.add("default")

    productFlavors {
        create("standard") {
            buildConfigField("Boolean", "INCLUDE_UPDATER", "true")
        }
        create("dev") {

            resourceConfigurations.add("en")
        }
    }

    /* lint {
         disable("MissingTranslation")
         isAbortOnError = false
         isCheckReleaseBuilds = false
     }*/

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {

    implementation(kotlinx.bundles.kotlin)

    // Modified dependencies
    implementation(libs.j2k.subsample) {
        exclude(module = "image-decoder")
    }

    implementation(libs.bundles.tachiyomi)
    implementation(androidx.bundles.androidx)
    implementation(libs.bundles.google)
    implementation(libs.bundles.rx)
    implementation(libs.flowprefs)
    implementation(libs.bundles.ok)

    // TLS 1.3 support for Android < 10
    implementation(libs.conscrypt)

    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    implementation(libs.bundles.retrofit)

    // JSON
    implementation(libs.kotson)

    // Disk
    implementation(libs.disklrue)

    // HTML parser
    implementation(libs.jsoup)

    // Icons
    implementation(libs.bundles.iconics)

    //requrired outside bundle cause toml doesnt work with aar
    implementation("com.mikepenz:community-material-typeface:6.4.95.0-kotlin@aar")
    implementation("com.mikepenz:material-design-icons-dx-typeface:5.0.1.2-kotlin@aar")
    implementation("com.mikepenz:google-material-typeface-outlined:4.0.0.1-kotlin@aar")

    // Database
    implementation("androidx.sqlite:sqlite:2.2.0")
    implementation("com.github.inorichi.storio:storio-common:8be19de@aar")
    implementation("com.github.inorichi.storio:storio-sqlite:8be19de@aar")
    implementation("com.github.requery:sqlite-android:3.36.0")

    // Model View Presenter
    implementation(libs.bundles.nucleus)

    // Dependency injection
    implementation("com.github.inorichi.injekt:injekt-core:65b0440")

    // Image library
    implementation(libs.bundles.coil)

    // Logging
    implementation("com.elvishew:xlog:1.11.0")

    // UI
    //implementation("com.dmitrymalkovich.android:material-design-dimens:1.4")
    implementation("com.github.leandroBorgesFerreira:LoadingButtonAndroid:2.2.0")
    implementation(libs.bundles.fastadapter)

    implementation(libs.bundles.flexibleadapter)

    implementation("com.nightlynexus.viewstatepageradapter:viewstatepageradapter:1.1.0")
    implementation("com.github.sephiroth74:NumberSlidingPicker:1.0.3")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("com.github.CarlosEsco:ViewTooltip:f79a8955ef")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")
    implementation("me.saket.cascade:cascade:1.3.0")

    //Compose
    implementation(compose.bundles.compose)
    implementation(compose.gap)
    implementation(compose.bundles.accompanist)


    implementation(libs.pastelplaceholders)
    implementation(libs.bundles.conductor)
    implementation(libs.stringsimilarity)
    implementation(libs.versioncompare)
    implementation(libs.tokenbucket)
    implementation(libs.bundles.kahelpers)
    implementation(libs.sandwich)
    implementation(libs.aboutLibraries.compose)
    debugImplementation(libs.leakcanary)

    testImplementation(libs.bundles.tests)

}

tasks {
// See https://kotlinlang.org/docs/reference/experimental.html#experimental-status-of-experimental-api(-markers)
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlinx.coroutines.DelicateCoroutinesApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xopt-in=coil.annotation.ExperimentalCoilApi",
            "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.InternalCoroutinesApi",
            "-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }

    preBuild {
// dependsOn(formatKotlin)
    }
}

