import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

val syncAppIcons = tasks.register("syncAppIcons") {
    val androidAppRes = rootProject.file("androidApp/src/main/res")
    val outResDir = layout.buildDirectory.dir("generated/iconRes")
    inputs.dir(androidAppRes)
    outputs.dir(outResDir)
    doLast {
        val targetRoot = outResDir.get().asFile
        targetRoot.deleteRecursively()
        targetRoot.mkdirs()

        val foldersToCopy = listOf(
            "drawable-mdpi",
            "drawable-hdpi",
            "drawable-xhdpi",
            "drawable-xxhdpi",
            "drawable-xxxhdpi",
            "drawable-nodpi",
            "mipmap-anydpi",
            "mipmap-anydpi-v26",
        )
        for (folder in foldersToCopy) {
            val srcFolder = File(androidAppRes, folder)
            if (srcFolder.exists()) {
                srcFolder.copyRecursively(File(targetRoot, folder), overwrite = true)
            }
        }

        val srcDrawable = File(androidAppRes, "drawable")
        val dstDrawable = File(targetRoot, "drawable").apply { mkdirs() }
        srcDrawable.listFiles()?.filter {
            it.name.startsWith("app_icon_") || it.name == "ic_launcher_monochrome.xml"
        }?.forEach { file ->
            file.copyTo(File(dstDrawable, file.name), overwrite = true)
        }

        // Also copy desktop logo previews into drawable-nodpi so Settings can preview Desktop & iOS icons
        val dstNodpi = File(targetRoot, "drawable-nodpi").apply { mkdirs() }
        val desktopLogo = rootProject.file("desktopApp/src/desktopMain/resources/logo.png")
        val desktopLogoDark = rootProject.file("desktopApp/src/desktopMain/resources/logo_dark.png")
        val iosLogo = rootProject.file("iosApp/Metrolist/Assets.xcassets/AppIcon.appiconset/AppIcon.png")
        val iosLogoTinted = rootProject.file("iosApp/Metrolist/Assets.xcassets/AppIcon.appiconset/AppIcon-Tinted.png")
        if (desktopLogo.exists()) desktopLogo.copyTo(File(dstNodpi, "preview_desktop_logo.png"), overwrite = true)
        if (desktopLogoDark.exists()) desktopLogoDark.copyTo(File(dstNodpi, "preview_desktop_logo_dark.png"), overwrite = true)
        if (iosLogo.exists()) iosLogo.copyTo(File(dstNodpi, "preview_ios_logo.png"), overwrite = true)
        if (iosLogoTinted.exists()) iosLogoTinted.copyTo(File(dstNodpi, "preview_ios_logo_tinted.png"), overwrite = true)

        // Ensure all 8 alternate iOS appiconsets are populated in iosApp/Metrolist/Assets.xcassets/
        val iosAssetsDir = rootProject.file("iosApp/Metrolist/Assets.xcassets")
        val nodpiSrc = File(androidAppRes, "drawable-nodpi")
        val variants = listOf(
            Triple("AppIcon-ArcticHaze", "app_icon_arctic_haze_background.webp", "app_icon_arctic_haze_foreground.webp"),
            Triple("AppIcon-LavenderLeap", "app_icon_lavender_leap_background.webp", "app_icon_lavender_leap_foreground.webp"),
            Triple("AppIcon-Metromallow", "app_icon_metromallow_background.webp", "app_icon_metromallow_foreground.webp"),
            Triple("AppIcon-PrideSpecial", "app_icon_pride_special_background.webp", "app_icon_pride_special_foreground.webp"),
            Triple("AppIcon-Static", "app_icon_static_background.webp", "app_icon_static_foreground.webp"),
        )
        for ((setName, bgName, fgName) in variants) {
            val dir = File(iosAssetsDir, "$setName.appiconset").apply { mkdirs() }
            val outPng = File(dir, "$setName.png")
            if (!outPng.exists()) {
                ProcessBuilder(
                    "/usr/bin/convert",
                    File(nodpiSrc, bgName).absolutePath,
                    File(nodpiSrc, fgName).absolutePath,
                    "-gravity", "center", "-composite",
                    "-resize", "1024x1024",
                    outPng.absolutePath,
                ).redirectErrorStream(true).start().waitFor()
            }
            File(dir, "Contents.json").writeText(
                """
                {
                  "images" : [
                    {
                      "filename" : "$setName.png",
                      "idiom" : "universal",
                      "platform" : "ios",
                      "size" : "1024x1024"
                    }
                  ],
                  "info" : {
                    "author" : "xcode",
                    "version" : 1
                  }
                }
                """.trimIndent() + "\n",
            )
        }
        val fullVariants = listOf(
            "AppIcon-ZenithBlack" to "app_icon_zenith_black_full.webp",
            "AppIcon-ZenithWhite" to "app_icon_zenith_white_full.webp",
            "AppIcon-Gradient" to "app_icon_gradient_full.webp",
        )
        for ((setName, fullName) in fullVariants) {
            val dir = File(iosAssetsDir, "$setName.appiconset").apply { mkdirs() }
            val outPng = File(dir, "$setName.png")
            if (!outPng.exists()) {
                ProcessBuilder(
                    "/usr/bin/convert",
                    File(nodpiSrc, fullName).absolutePath,
                    "-resize", "1024x1024",
                    outPng.absolutePath,
                ).redirectErrorStream(true).start().waitFor()
            }
            File(dir, "Contents.json").writeText(
                """
                {
                  "images" : [
                    {
                      "filename" : "$setName.png",
                      "idiom" : "universal",
                      "platform" : "ios",
                      "size" : "1024x1024"
                    }
                  ],
                  "info" : {
                    "author" : "xcode",
                    "version" : 1
                  }
                }
                """.trimIndent() + "\n",
            )
        }
    }
}

android {
    namespace = "com.metrolist.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nimzz.yurutune"
        minSdk = 24
        targetSdk = 36
        versionCode = 154
        versionName = "14.0.0"
        manifestPlaceholders["appLabel"] = "YuruTune"
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
            manifestPlaceholders["appLabel"] = "YuruTune"
        }
        release {
            isMinifyEnabled = false
            manifestPlaceholders["appLabel"] = "YuruTune"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets["main"].res.directories.add("build/generated/iconRes")
}

tasks.named("preBuild") {
    dependsOn(syncAppIcons)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
}
