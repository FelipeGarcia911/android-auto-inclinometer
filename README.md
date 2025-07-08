# GR Offroad Meter

Welcome to GR Offroad Meter! This is an Android application designed to serve as a digital inclinometer, ideal for off-road and 4x4 driving enthusiasts. The application provides real-time readings of the vehicle's pitch and roll angles.

> **Developer's Note:** This is a personal project developed to explore and test the capabilities of the **Google Gemini CLI** and the **Vibe Coding** concept. It is a playground for experimenting with AI-assisted development in a command-line environment.

## ✨ Features

- **Real-Time Measurement:** Displays the vehicle's pitch and roll angles.
- **Visual Interface:** A clear and easy-to-read user interface that simulates a physical inclinometer.
- **Android Auto Support:** A dedicated module (`car`) for integration with Android Auto-compatible vehicle systems.
- **Calibration:** Functionality to calibrate the inclinometer's "zero point" based on the device's position in the vehicle.
- **Orientation Lock:** Allows the user to lock the screen orientation to portrait or landscape for a stable display.

## 🛠️ Project Structure

The project is organized into the following Gradle modules:

- **`:app`**: The main module containing the Android application for phones and tablets.
- **`:car`**: Module for Android Auto functionality, providing an adapted interface for the vehicle's screen.
- **`:common`**: A library module containing shared business logic, such as sensor calculations and data models, used by the `:app` and `:car` modules.

## 🚀 Technologies Used

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Architecture:** MVVM (Model-View-ViewModel) with Use Cases
- **UI Toolkit:** Jetpack Compose
- **Dependency Injection:** Hilt
- **Build Tool:** Gradle with Kotlin DSL
- **Dependency Management:** Gradle Version Catalog (`libs.versions.toml`)

## ⚙️ How to Build

1.  Clone this repository:
    ```bash
    git clone https://github.com/your-username/GR Offroad Meter.git
    ```
2.  Open the project in Android Studio.
3.  Let Gradle sync and download all dependencies.
4.  To build the project from the command line, run:
    ```bash
    ./gradlew build
    ```
5.  To install the application on a connected device or emulator, run:
    ```bash
    ./gradlew installDebug
    ```
