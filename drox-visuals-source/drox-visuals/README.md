# Drox Visuals

Client-side Fabric mod for Minecraft Java 26.3 that adds animated HUD and atmosphere effects.

## What it does

- Animated rainbow accent bar above the hotbar
- Speed lines at the screen edges while sprinting or gliding
- Soft vignette and a light colour grade per dimension (Overworld, Nether, End)
- Red pulse when your health is low and a flash when you take damage
- Ambient glowing particles around you, themed per dimension
- Glowing trail while sprinting or gliding, and a dust ring after a hard landing

## Requirements

Minecraft 26.3, Fabric Loader 0.20.1+, Fabric API 0.165.0+26.3, Java 25.

## Install

Put `drox-visuals-1.0.0.jar` and Fabric API in your `mods` folder.

## Build the jar

Option A, GitHub (no local setup): create a new GitHub repository, upload everything in this folder,
open the **Actions** tab, wait for "Build Drox Visuals" to finish, and download the `drox-visuals`
artifact. It contains the jar.

Option B, locally: install JDK 25 and Gradle 9.7.1, then run `gradle build` in this folder.
The jar is created in `build/libs/`.

## Status

This first version has not been compiled or run against 26.3 yet. If the build fails, the error log
shows which Minecraft call changed; send it back and it is a quick fix.
Camera and player-motion effects are not included yet because they need mixins into rendering code.
