publishing {
    repositories {
        maven {
            name = "Catnies"
            url = uri("https://repo.catnies.top/releases")
            credentials(PasswordCredentials::class)
            authentication { create<BasicAuthentication>("basic") }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "net.nyana"
            artifactId = "nayana-nbt-tag"
            version = version
            from(components["java"])
            pom {
                name = "Nyana NBT Tag"
                url = "https://github.com/Catnies/Nyana-NBT"
            }
        }
    }
}