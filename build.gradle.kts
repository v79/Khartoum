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
    linuxArm64 {
        compilations.getByName("main") {
            cinterops {
                val libcurl by creating {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/libcurl.def"))
                    includeDirs("src/include/curl")
                }
                val libbcm by cinterops.creating {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/libbcm.def"))
                }
            }
        }
        binaries {
            executable {
                entryPoint = "org.liamjd.pi.main"
            }
        }
    }

    mingwX64()

    /*  jvm() {
      }*/


    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        }
        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        /* jvmTest.dependencies {
             implementation(kotlin("test-junit"))
         }*/
        /*
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
         */
    }
    jvmToolchain(17)
}
