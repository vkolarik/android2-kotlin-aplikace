import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").reader())

android {
    val versionMajor = 0
    val versionMinor = 0
    val versionPatch = 1
    val myVersionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
    val myVersionName = "${versionMajor}.${versionMinor}.${versionPatch}"


    namespace = "cz.mendelu.project"
    compileSdk = 34

    defaultConfig {
        applicationId = "pef.mendelu.project.xkolari1"
        minSdk = 26
        targetSdk = 34
        versionCode = myVersionCode
        versionName = myVersionName

        testInstrumentationRunner = "cz.mendelu.project.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", properties.getProperty("baseurldevel"))
        }
        debug {
            buildConfigField("String", "BASE_URL", properties.getProperty("baseurlproduction"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runner)
    testImplementation(libs.junit)
    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.kapt)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit.okhtt3)
    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    implementation(libs.lifecycle)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ktx)
    implementation(libs.navigation.compose)

    implementation(libs.googlemap)
    implementation(libs.googlemap.compose)
    implementation(libs.googlemap.foundation)
    implementation(libs.googlemap.utils)
    implementation(libs.googlemap.widgets)
    implementation(libs.googlemap.compose.utils)

    implementation(libs.lifecycle)
    implementation(libs.room.ktx)
    implementation(libs.room.viewmodel)
    implementation(libs.room.lifecycle)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler.kapt)

    //ze stareho projektu
    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(libs.sentry)

    implementation("com.google.zxing:core:3.5.2")

    implementation(libs.text.recognition)
    implementation(libs.camera.core)
    implementation(libs.camera.view)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecyvle)
    implementation(libs.camera.extensions)
    implementation(libs.camera.video)

    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    // If you already have the core icons, make sure you have this too:
    implementation("androidx.compose.material:material-icons-core:1.5.4")

    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)
    implementation(libs.tracing)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

}