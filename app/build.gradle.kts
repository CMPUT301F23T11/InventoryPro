plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.inventorypro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.inventorypro"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        animationsDisabled = true
    }

    buildFeatures{
        dataBinding = true
        viewBinding = true
    }


}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.android.gms:play-services-cast-framework:21.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.mockito:mockito-core:4.0.0")

    // For Android-specific tests
    androidTestImplementation("org.mockito:mockito-android:3.12.4")

    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

        implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
        // Add the dependency for the Cloud Storage library
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-storage")
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")

    compileOnly(files("${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"))
}