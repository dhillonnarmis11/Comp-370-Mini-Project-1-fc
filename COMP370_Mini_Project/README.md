# COMP370 Mini Project 1 

# Team Members - Group 4

Simran Dhillon 300211810

Charvi Charvi 300207650

Harjot Josan 300211962

Mohammad Salema 300223224

---

## Project Overview

This project implements a Server Redundancy Management System (SRMS) that simulates a
primary–backup server architecture. Multiple server processes run at the same time, but only
one server acts as the primary and handles client requests. The remaining servers act as backups
and automatically take over if the primary server fails.

The goal of the project is to demonstrate core software engineering concepts such as requirements
analysis, system design using UML, object-oriented programming, networking, concurrency, and
fault tolerance.

---

## System Description

The system consists of multiple independent server processes, a monitor component, and a client.
Servers periodically send heartbeat messages so the monitor can track their health. If the primary
server becomes unavailable, the monitor promotes a backup server to primary using a deterministic
rule. Clients always communicate with the current primary server and can automatically reconnect
after a failover. Networking is implemented using Java sockets, and concurrency is handled using thread pools so
multiple requests and heartbeat messages can be processed at the same time. Logging is used to
track important events such as server startup, failures, and failovers.

---

## Folder Structure

```
COMP370_Mini_Project/
│
├── src/                          # Java source files
│   ├── ServerMain.java           # Entry point for server processes
│   ├── Monitor.java              # Monitor with heartbeat tracking
│   ├── ServerProcess.java        # Abstract server base class
│   ├── PrimaryServer.java        # Primary server implementation
│   ├── BackupServer.java         # Backup server implementation
│   ├── ConnectionHandler.java    # Client connection handler
│   ├── ControlListener.java      # Listens for PROMOTE commands
│   ├── HeartbeatSenderUDP.java   # Sends heartbeat messages
│   ├── FailoverManager.java      # Failover selection logic
│   ├── Log.java                  # Logging utility
│   └── logs/                     # Log files directory
│       ├── monitor.log
│       ├── server-1.log
│       ├── server-2.log
│       └── server-3.log
│
├── docs/                         # Documentation and UML diagrams
│   ├── Mini_Project_1_Final_Report.pdf
│   ├── Step_3_Design.pdf
│   ├── Step_7_and_8.pdf
│   ├── report_steps_1_to_4.txt
│   └── steps_1_2_planning.pdf
│
└── README.md                     # This file
```


## How to Compile

```
Prerequisites

Java Development Kit (JDK) 8 or higher
Operating System: Linux, macOS, or Windows
Network: Localhost support (for testing)

Running the System
The system requires running multiple processes in separate terminal windows.
Navigate to Source Directory for each server and start

Terminal 1- Monitor
cd ~/Desktop/projectComp370
java -cp bin Monitor

Server 1- Primary
cd ~/Desktop/projectComp370
java -cp bin ServerMain 1 9001 9101 primary

Server 2- backup 
cd ~/Desktop/projectComp370
java -cp bin ServerMain 2 9002 9102 backup

Server 3
cd ~/Desktop/projectComp370
java -cp bin ServerMain 3 9003 9103 backup

```

