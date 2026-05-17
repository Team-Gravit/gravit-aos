import org.gradle.kotlin.dsl.implementation
import java.io.FileInputStream
import java.util.Properties;

val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.inuappcenter.gravit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.inuappcenter.gravit"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""
        manifestPlaceholders["auth0Domain"] = "dev-fl5wpn5srn5xay26.us.auth0.com"
        manifestPlaceholders["auth0Scheme"] = "gravit"
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "AUTH0_CLIENT_ID", "\"${properties.getProperty("AUTH0_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "AUTH0_DOMAIN", "\"${properties.getProperty("AUTH0_DOMAIN") ?: ""}\"")
        buildConfigField("String", "API_BASE_URL", "\"${properties.getProperty("API_BASE_URL") ?: ""}\"")

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")

        buildConfigField("String", "OAUTH_CLIENT_ID", "\"${properties.getProperty("OAUTH_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "OAUTH_CLIENT_SECRET", "\"${properties.getProperty("OAUTH_CLIENT_SECRET") ?: ""}\"")
        buildConfigField("String", "OAUTH_CLIENT_NAME", "\"${properties.getProperty("OAUTH_CLIENT_NAME") ?: ""}\"")

        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${properties.getProperty("GOOGLE_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "GOOGLE_CLIENT_SECRET", "\"${properties.getProperty("GOOGLE_CLIENT_SECRET") ?: ""}\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/io.netty.versions.properties"
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
    implementation(libs.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.browser)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.auth0)
    implementation(libs.androidx.security.crypto.v110alpha06)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.play.services.auth)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.foundation.layout)

    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")
    implementation(libs.generativeai)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.foundation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.compose.material:material:1.6.0")

    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:image:4.6.2")
    implementation("io.noties.markwon:image-coil:4.6.2")
    implementation("io.coil-kt:coil-svg:2.7.0")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.kakao.sdk:v2-user:2.20.0")
    implementation("com.navercorp.nid:oauth:5.11.1")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

}