# Mantis Kaiju (Forge 1.20.1)

A colossal, End City-inspired mantis kaiju that hides inside a shulker at the **highest point of every newly generated End City**.

## Features
- **Disguise:** looks, sounds and peeks exactly like a vanilla shulker and can't be hurt by anything but a player.
- **Awakening:** the first player hit bursts the box open (shulker-open + dragon growl, portal particles). The mantis grows out of it over one second to ~9 blocks tall, with a purple boss bar.
- **Boss stats:** 400 HP, 20 attack, 12 armor, full knockback resistance. Hits apply Levitation, so expect to get knocked off the tower.
- **Drops:** Mantis Kaiju Head + Mantis Kaiju Claws (guaranteed).
- **Mantis Head:** wear it on your head and **crouch** to see red outlines on hostile mobs and colour-coded outlines on ores within 20 blocks, through walls.
- **Mantis Claws (Curios `gloves` slot):** entity and block reach go to 15 blocks, and attack speed doubles.

## Requirements
- Minecraft 1.20.1, Forge 47.x
- Curios API 5.4+ for 1.20.1 (required)

## Building
The Gradle wrapper jar is binary, so it isn't in this repo. Copy `gradlew`, `gradlew.bat` and `gradle/wrapper/gradle-wrapper.jar` from the Forge 1.20.1 MDK (or run `gradle wrapper --gradle-version 8.1.1`), then:

```
./gradlew build        # jar in build/libs/
./gradlew runClient    # dev client with Curios
```

To test, use `/summon mantiskaiju:mantis_kaiju` or the spawn egg.

## Placeholders / TODO
- The mantis uses the vanilla purpur texture. Add a 64x64 `assets/mantiskaiju/textures/entity/mantis_kaiju.png` (UV layout in `MantisKaijuModel`) and update `MANTIS_TEXTURE` in `MantisKaijuRenderer`.
- The head and claw item icons use the vanilla shulker shell and shears sprites.
- If the server stops before the top of a city loads, that city's kaiju won't spawn. Only cities generated after the mod is installed get one.
