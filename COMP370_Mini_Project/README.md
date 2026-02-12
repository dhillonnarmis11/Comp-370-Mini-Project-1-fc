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
│   ├── Step_3_Design.pdf
│   ├── Step_7_and_8.pdf
│   ├── report_steps_1_to_4.txt
│   └── steps_1_2_planning.pdf
│
└── README.md                     # This file
```


## How to Compile

```
Step 1: Navigate to Source Directory
cd COMP370_Mini_Project/src

Step 2: Compile All Java Files
javac *.java

Verify Compilation
Check that .class files were created:
ls *.class

Running the System
The system requires running multiple processes in separate terminal windows.
Terminal 1: Start the Monitor (The monitor tracks server health and manages failover)
cd COMP370_Mini_Project/src
java Monitor
Terminal 2: Start Server 1 (Primary)
cd COMP370_Mini_Project/src
java ServerMain 1 8001 9101 primary
Terminal 3: Start Server 2 (Backup)
cd COMP370_Mini_Project/src
java ServerMain 2 8002 9102 backup
Terminal 4: Start Server 3 (Backup)
cd COMP370_Mini_Project/src
java ServerMain 3 8003 9103 backup

```

