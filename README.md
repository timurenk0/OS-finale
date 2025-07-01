# 🖥️ OS-Finale: Operating System and File System Simulator

A **Java based simulation** of a modern operating system and file system showcasing key OS concepts like memory management, CPU scheduling, file system management, and process lifecycle.

---
## How to Run?

### Simply run Main.java file

---

## 🚀 Features

### 🧠 Memory Management
- Virtual memory using paging
- RAM ↔ HDD swapping simulation

### 🖥️ CPU Scheduling
- Custom scheduler implementation
- Multi-event execution handling

### 📁 File System
- Node-based file/directory simulation
- Create/delete files, browse directories

### 🔄 Process Lifecycle
- Full process pipeline: New → Terminated
- Page-based execution

---

## 📂 Project Structure

```
OS-Finale/
├── fileSystem/                  # File system logic (files, directories, tree structure)
│   ├── FileSystem.java
│   ├── FileNode.java
├── GUI/                         # User-friendly graphical interface
│   ├── FileSystemGUI.java
│   ├── OperatingSystemGUI.java
│   └── ...
├── operatingSystem/             # Core OS components (CPU, RAM, HDD, Process, etc.)
│   ├── CPUManager.java
│   ├── RAMManager.java
│   ├── DiskManager.java
│   ├── Scheduler.java
│   ├── Process.java
│   ├── Page.java
│   ├── MySemaphore.java
├── Main.java                    # Entry point of the simulator
├── .gitignore                   # Good old gitignore file
└── README.md                    # Project documentation

```
