// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript{
    dependencies{
        classpath("com.google.gms:google-services:4.3.15")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
}
true // Needed to make the Suppress annotation work for the plugins block