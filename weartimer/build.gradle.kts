plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.weartimer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.timer"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
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
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.wear:wear-ongoing:1.0.0")
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.wear:wear-ongoing:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.wear.compose:compose-material:1.2.1")
    implementation("androidx.wear.compose:compose-foundation:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.wear.tiles:tiles:1.2.0")
    implementation("androidx.wear.tiles:tiles-material:1.2.0")
    implementation("com.google.android.horologist:horologist-compose-tools:0.5.13")
    implementation("com.google.android.horologist:horologist-tiles:0.5.13")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.0")


    // Horologist
    implementation("com.google.android.horologist:horologist-composables:0.5.13")
    implementation("com.google.android.horologist:horologist-compose-layout:0.5.13")
    implementation("com.google.android.horologist:horologist-compose-material:0.5.13")

    implementation("androidx.wear.protolayout:protolayout-expression:1.1.0-alpha03")

    // Use to implement support for Wear ProtoLayout
    implementation("androidx.wear.protolayout:protolayout:1.1.0-alpha03")

    // Use to utilize components and layouts with Material design in your ProtoLayout
    implementation("androidx.wear.protolayout:protolayout-material:1.1.0-alpha03")
    implementation("androidx.compose.foundation:foundation-android:1.5.4")


    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}