@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

android {
    // TODO: Renommage du namespace selon le nom du projet
    namespace = "fr.imt.atlantique.codesvi.app"
    compileSdk = 34

    defaultConfig {
        // TODO: Renommage du applicationId selon le nom du projet
        applicationId = "fr.imt.atlantique.codesvi.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // des librairies de base
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // libreries pour splash screen
    implementation(libs.core.splashscreen)

    // des librairies de jetpack compose
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.icons)
    implementation(libs.compose.extended.icons)
    implementation(libs.navigation.compose)


    // des librairies de material design
    implementation(libs.material3)
    implementation(libs.androidx.material3.windowsize)

    // des librairies de hilt
    implementation(libs.android.hilt)
    implementation(libs.hiltnav.compose)
    implementation(libs.play.services.pal)
    kapt(libs.hilt.android.compiler)

    // des libreries de database
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)


    // importe coroutines
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.coroutines.android)

    // des librairies de test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    // des librairies de debug
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.timber)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    //implementation("com.google.firebase:firebase-storage-ktx:21.0.1")





}



kapt {
    correctErrorTypes = true
}