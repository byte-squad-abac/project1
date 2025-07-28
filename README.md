# Space Invaders ‑ Extended Edition

> **Course**: GDD — Game Development & Design  
> **Assessment Weight**: 20%  
> **Due**: **Monday, 28 July 2025**  
> **Template Starter**: `mchayapol/gdd-space-invaders-project`


## Table of Contents

1. [Overview](#overview)  
2. [Description](#description)  
3. [Requirements](#requirements)
4. [Gameplay & Features](#gameplay--features)
5. [Controls](#controls)  
6. [Build & Run](#build--run)  
7. [Team](#team)  

---

## Overview

This project reimagines **Konami’s Life Force / Salamander** side-scroll shooter within the classic *Space Invaders* codebase used in class.  
The game is built with Java (Swing/AWT) and features **two scrolling stages**, a **boss fight**, **animated sprites**, multiple enemies, and a **power-up system**.  
Development is limited to the class codebase to prove originality and understanding.

🕹 Gameplay Video (10 min) → **[YouTube Link Here](https://youtu.be/TZaROLjQLn4)**

---

## Description

- Start from this code template  
  [mchayapol/gdd-space-invaders-project](https://github.com/mchayapol/gdd-space-invaders-project)  
- Use similar game mechanics  
  [https://www.retrogames.cz/play_234-NES.php](https://www.retrogames.cz/play_234-NES.php)  
  [https://www.youtube.com/watch?v=9FV4a_cEl3o](https://www.youtube.com/watch?v=9FV4a_cEl3o)  
- Some resources of sprites (rotate if needed):
  - [TurboGrafx-16 – Salamander / Life Force – Vic Viper – The Spriters Resource](https://www.spriters-resource.com/turbografx_16/salamanderlifeforce/sheet/123101/)  
  - [NES – Life Force / Salamander – Vic Viper & Lord British – The Spriters Resource](https://www.spriters-resource.com/nes/lifeforcesalamander/sheet/121201/)

## Requirements

1. Side-scroll (Optional) 
2. Must extend in-class codebase only  ☑️
3. Title Scene with team names  ☑️
4. At least **two** stages  ☑️
5. Each stage plays ~5 minutes (load array from external CSV)  
   - Ref: [Reading a CSV File into an Array | Baeldung](https://www.baeldung.com/java-csv-file-array)  
6. Last stage has a boss fight  
7. At least **two** enemy types  ☑️
8. All sprites must be animated (drawing or clipping)  
9. Enemy bombs must be in a separate list, part of `Enemy`  ☑️
10. Power-Ups:
    - Speed up × 4 ☑️
    - Multi-shot × 4 ☑️
    - *(Optional)* Weapon upgrade (e.g. 3-way shots) 
11. Dashboard must show:
    - Score ☑️
    - Speed ☑️
    - Shots upgrade ☑️

---




## Gameplay & Features

| Requirement           | Implementation                                                                 |
| --------------------- | ------------------------------------------------------------------------------ |
| **Side-Scrolling**    | Camera scrolls across stage map loaded from CSV (`assets/stages/stage#.csv`)   |
| **≥ Two Stages**      | `SceneStage1`, `SceneStage2` (~5 min each)                                     |
| **Boss Fight**        | Final boss appears after stage 2 ends                                          |
| **Animated Sprites**  | Clipped animation using `SpriteSheet.java`                                     |
| **Enemy Types**       | Includes `StraightEnemy`, `ZigZagEnemy`, and `Boss`                            |
| **Enemy Bombs**       | Managed in `List<Bomb>` per `Enemy`                                            |
| **Power-Ups**         | 4 levels of Speed and Multi-Shot, optional 3-way shot                          |
| **Dashboard (HUD)**   | Displays Score, Speed Level, Shot Level, and Lives                             |

---

## Controls

Shoot / Fire - Space<br>
left / right movement - left / right arrow key

---

## Build & Run

> Requires: JDK 17+, Maven 3.9+ (or Gradle 8 optional)

```bash
# 1. Clone the project
git clone https://github.com/byte-squad-abac/project1.git
cd project1

# 2. Build
mvn clean package     # or use Gradle if configured

# 3. Run
java -jar target/space-invaders.jar
```
## Team

Lut Lat Aung - 6511163 [Github](https://github.com/Lut-Lat-Aung)<br>
Lu Phone Maw - 6511157 [Github](https://github.com/luphone04)<br>
Wai Yan Paing - 6511171 [Github](https://github.com/Lucas1792003)<br>


