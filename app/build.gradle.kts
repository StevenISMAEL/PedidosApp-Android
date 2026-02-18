plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pedidosapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.pedidosapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 1. LIBRERÍA DE RED (RETROFIT)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 2. BASE DE DATOS LOCAL (ROOM) - Versión Java
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion") // OJO: Usamos annotationProcessor en lugar de kapt

    // 3. GPS Y UBICACIÓN
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // 4. LECTOR DE CÓDIGO QR (ML Kit)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

}