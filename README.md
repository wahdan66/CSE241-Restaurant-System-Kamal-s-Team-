# 🍽️ JavaFX Restaurant Management & Ordering System

An object-oriented desktop and mobile-ready restaurant ordering, reservation, and POS application built using JavaFX and pure Java following the **MVC Architecture**.

---

## 👥 Team Members

* **Kamal Wahdan** — Team Lead & Core Architect
* **Ziad Atef** — Developer 
* **Nour Hatem** — Developer 

---

## ⚙️ Prerequisites & Tech Stack

Before running or building this project, ensure your environment meets the following requirements:

* **Java Development Kit (JDK):** JDK 26 or higher
* **IDE:** IntelliJ IDEA (Recommended)
* **GUI Framework:** JavaFX
* **Version Control:** Git & GitHub Desktop

---

## 🏗️ Project Architecture & Package Structure

The system is structured using a clean **Model-View-Controller (MVC)** design pattern to separate business logic, user interface, and data access layers:

```text
src/
└── com.restaurant/
    ├── model/         # Domain entities (User, Table, Order, MenuItem, Enums)
    ├── view/          # JavaFX FXML views and UI styling
    ├── controller/    # Event handlers and controller logic
    ├── db/            # Data layer (In-memory storage & persistence)
    ├── util/          # Helper classes, formatters, and constants
    └── network/       # Client-server communication modules