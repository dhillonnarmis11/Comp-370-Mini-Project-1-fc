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
after a failover.

Networking is implemented using Java sockets, and concurrency is handled using thread pools so
multiple requests and heartbeat messages can be processed at the same time. Logging is used to
track important events such as server startup, failures, and failovers.

---

## Folder Structure

```
src/ Java source files
docs/ UML diagrams and documentation
scripts/ Scripts for running the system and simulating failures
tests/ Test scenarios and results
```


## How to Compile
From inside the `src` folder:
```
javac *.java
```

