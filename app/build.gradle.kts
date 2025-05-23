plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales"
    compileSdk = 35

    defaultConfig {
        applicationId =
            "com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales"
        minSdk = 24
        targetSdk = 35
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
    implementation(libs.mpandroidchart)

    // Firebase (nuevas dependencias)
    implementation(platform(libs.firebase.bom))  // Bill of Materials
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)  // Opcional para login con Google

    // PDF Viewer
    implementation(libs.android.pdf.viewer)

    // Libreria para menu desplegable
    implementation(libs.android.drawerlayout)

    // Core runtime de WorkManager (Java)
    implementation(libs.androidx.work.runtime)
}