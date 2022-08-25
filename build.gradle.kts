// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
}

tasks.withType<Javadoc>().all {
    enabled = false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

