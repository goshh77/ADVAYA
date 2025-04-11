Mesh-Based Content Sharing App

This Android application demonstrates **peer-to-peer content sharing** using **Wi-Fi Direct (P2P)**, designed primarily for educational use cases like sharing resources and quizzes between teachers and students — even **without internet connectivity**.

Features

- Discover nearby devices using **Wi-Fi Direct**
- Establish peer-to-peer connections
- Exchange simple messages between devices (can be extended to share files/resources)
- Mesh networking foundation for decentralized content sharing
- Jetpack Compose UI for peer discovery and interaction

---
Project Structure

. ├── app │ ├── src │ │ ├── main │ │ │ ├── AndroidManifest.xml # Permissions and configuration │ │ │ ├── java │ │ │ │ └── com.example.wifidirectdemo │ │ │ │ ├── MainActivity.kt # Main logic for P2P connection and messaging │ │ │ │ └── WiFiDirectBroadcastReceiver.kt # Broadcast listener for Wi-Fi Direct state


---


1. `MainActivity.kt`

- Initializes the **Wi-Fi P2P manager**
- Requests **fine location permission** (mandatory for Wi-Fi Direct)
- Provides UI to:
  - Discover peers
  - Display discovered devices
  - Connect to a selected device
- Handles communication:
  - Starts a **server** on the group owner
  - Starts a **client** to receive messages

2. `WiFiDirectBroadcastReceiver.kt`

- Listens for system events:
  - Peer list updates
  - Connection state changes
- Updates the peer list in real-time
- Triggers connection info retrieval on successful connection

3. `AndroidManifest.xml`

- Declares all necessary permissions for:
  - Internet & Wi-Fi state
  - Location access
  - Nearby Wi-Fi communication
- Declares main activity and optional libraries

---

Setup & Run

Prerequisites

- Android Studio installed
- Android device or emulator (real devices preferred for P2P)
- Android 10+ recommended

 Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/mesh-content-sharing.git
   cd mesh-content-sharing
Open in Android Studio

Run on two real devices (with location & Wi-Fi enabled)

Tap "Discover Peers" on both → Connect → Exchange message

Tech Stack

    Android (Kotlin)

    Wi-Fi Direct API

    Jetpack Compose

    BroadcastReceiver

    Socket Programming (Java Sockets)
