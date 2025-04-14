import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.foodapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"${project.findProperty("MAPS_API_KEY") ?: ""}\""
        )

    }
    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BACKEND_URL",
                "\"${project.findProperty("BACKEND_URL") ?: ""}\""

            )
            buildConfigField(
                "String",
                "GOOGLE_WEB_CLIENT_ID",
                "\"${project.findProperty("GOOGLE_WEB_CLIENT_ID") ?: ""}\""
            )


        }
        release {
            buildConfigField(
                "String",
                "BACKEND_URL",
                "\"${project.findProperty("BACKEND_URL") ?: ""}\""
            )
            buildConfigField(
                "String",
                "GOOGLE_WEB_CLIENT_ID",
                "\"${project.findProperty("GOOGLE_WEB_CLIENT_ID") ?: ""}\""
            )
            
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    flavorDimensions += "environment"

    productFlavors {
        create("customer") {
            dimension = "environment"
            buildConfigField("String", "APP_VARIANT", "\"customer\"")
        }
        create("restaurant") {
            dimension = "environment"
            applicationIdSuffix= ".restaurant"
            buildConfigField("String", "APP_VARIANT", "\"admin\"")


            resValue(
                type = "string",
                name = "app_name",
                value = "FA Restaurant"
            )
            resValue(
                "string",
                "app_description",
                "Quản lý đơn hàng, theo dõi doanh thu, tối ưu vận hành."
            )
        }
        create("staff") {
            dimension = "environment"
            applicationIdSuffix=  ".staff"
            buildConfigField("String", "APP_VARIANT", "\"staff\"")
            resValue(
                type = "string",
                name = "app_name",
                value = "FA Staff"
            )
            resValue(
                "string",
                "app_description",
                "Hỗ trợ khách hàng, xử lý đơn hàng nhanh chóng."
            )
        }
    }
}
secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.properties"
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
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.transport.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.core.splashscreen)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.play.services.maps)

    implementation(libs.maps.compose)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.location)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-auth")

    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.systemuicontroller)

    implementation("co.yml:ycharts:2.1.0")


}
kapt {
    correctErrorTypes = true
}

