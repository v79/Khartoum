plugins {
   kotlin("multiplatform") version "2.0.20"
}

group = "org.liamjd.pi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
    mavenLocal()
}

kotlin {
    linuxArm64("pi") {
        compilations.getByName("main") {
            cinterops {
                val libcurl by creating {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/libcurl.def"))
                    includeDirs("src/include/curl")
                }
            }
        }
         /* compilations {
            "main" {
                cinterops {
                    val libbcm by cinterops.creating {}
                    val libbmp by cinterops.creating {}
                    val libcurl by cinterops.creating {
                        includeDirs("src/include/curl")
                    }
                }
            }
        }*/
        binaries {
            executable {
                this.optimized = false
                entryPoint = "org.liamjd.pi.main"
            }
        }
    }
}

