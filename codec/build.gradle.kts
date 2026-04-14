repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(project(":tag"))
    compileOnly("com.mojang:datafixerupper:8.0.16")
}

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
            artifactId = "nayana-nbt-codec"
            version = version
            from(components["java"])
            pom {
                name = "Nyana NBT Codec"
                url = "https://github.com/Catnies/Nyana-NBT"
            }
        }
    }
}