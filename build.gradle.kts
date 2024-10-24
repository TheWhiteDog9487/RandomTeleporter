plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
    id("maven-publish")
}

version = "${project.extra["mod_version"]}"
group = project.extra["maven_group"] as String

base {
    archivesName.set(project.extra["archives_base_name"] as String)
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/") }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("randomteleporter") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.extra["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project.extra["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.extra["loader_version"]}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.extra["fabric_version"]}")

    // Uncomment the following line to enable the deprecated Fabric API modules. 
    // These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.
    // "modImplementation"("net.fabricmc.fabric-api:fabric-api-deprecated:${project.extra["fabric_version"]}")

    // ↓ 开发测试用
    modRuntimeOnly("com.terraformersmc:modmenu:${project.extra["modmenu_version"]}")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" } }
}

tasks.remapJar{
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html#org.gradle.api.tasks.bundling.Jar:archiveFileName
    // 用这个属性设置jar包的文件名格式
    // 别用上面那个Jar任务的配置，会被remapJar覆盖掉
    archiveFileName = "${project.base.archivesName.get()}-${project.version} mc${project.extra["minecraft_version"]}.jar"}
tasks.remapSourcesJar{
    archiveFileName = "${project.base.archivesName.get()}-${project.version} mc${project.extra["minecraft_version"]}-sources.jar"}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}