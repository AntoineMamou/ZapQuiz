pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // jitpack c'est un autre repository pour les librairies officielles de Google pour Android
        // Ajout de jitpack pour les librairies non disponibles sur mavenCentral
        maven(url = "https://www.jitpack.io")
        maven(url = "https://androidx.dev/snapshots/latest/artifacts/repository")
    }
}

// TODO 1: Renommage du projet
rootProject.name = "app"

// Pour votre projet, il y aura un seul modeul principal parceque c'est une petite application
// ça peut etre différent pour un projet plus grand ou multiplatform (Android, iOS, Web, Desktop)
include(":app")