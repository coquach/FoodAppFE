import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
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
            "OPEN_CAGE_API_KEY",
            "\"${project.findProperty("OPEN_CAGE_API_KEY") ?: ""}\""

        )
        buildConfigField(
            "String",
            "ORS_KEY",
            "\"${project.findProperty("ORS_KEY") ?: ""}\""

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
            buildConfigField(
                "String",
                "MAPS_BOX_KEY",
                "\"${project.findProperty("MAPS_BOX_KEYL") ?: ""}\""

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
            buildConfigField(
                "String",
                "MAPS_BOX_KEY",
                "\"${project.findProperty("MAPS_BOX_KEYL") ?: ""}\""

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

        }
        create("admin") {
            dimension = "environment"
            applicationIdSuffix = ".restaurant"



            resValue(
                type = "string",
                name = "app_name",
                value = "FA Restaurant"
            )
            resValue(
                "string",
                "app_description",
                "Quản lý quán ăn, theo dõi doanh thu, tối ưu vận hành."
            )
        }
        create("staff") {
            dimension = "environment"
            applicationIdSuffix = ".staff"

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
        create("shipper") {
            dimension = "environment"
            applicationIdSuffix = ".shipper"
            resValue(
                type = "string",
                name = "app_name",
                value = "FA Shipper"
            )
            resValue(
                "string",
                "app_description",
                "Theo dõi đơn hàng, giao hàng nhanh chóng."
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
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.transport.api)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.core.splashscreen)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.27.0")

    //Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //Navigation
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)

    //Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    //Coil
    implementation(libs.coil.compose)

    //Data Store
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)

    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.location)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")


    //Google Auth
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    //Material Icon
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.accompanist.systemuicontroller)

    //YChart
    implementation("co.yml:ycharts:2.1.0")

    // Room components
    implementation("androidx.room:room-runtime:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    implementation("androidx.room:room-paging:2.7.0")

    //Paging
    implementation("androidx.paging:paging-compose:3.3.6")

    //Swipe Action
    implementation("me.saket.swipe:swipe:1.3.0")

    //Bottom Bar Animated
    implementation("com.exyte:animated-navigation-bar:1.0.0")

    //MapBox
    implementation("com.mapbox.maps:android:11.12.0")
    implementation("com.mapbox.extension:maps-compose:11.12.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.mapbox.navigationcore:android:3.9.0")

    //Stripe
   

}

