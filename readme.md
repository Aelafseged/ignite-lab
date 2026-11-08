# Apache Ignite Cluster & Thin Client Demo

This project demonstrates how to set up a local multi-node Apache Ignite cluster and connect to it using a lightweight Thin Client. It is designed to run on modern Java versions (Java 17+).

## 📂 Project Structure

- **`IgniteNode.java`**: The server node class. Launches an Ignite server instance that participates in the cluster (Discovery & Communication).
- **`MyIgniteClient.java`**: The thin client class. Connects to the cluster externally to perform cache operations without joining the cluster topology.

## Architecture Overview

This project utilizes three distinct network layers within Ignite:

1. **Discovery SPI (`TcpDiscoverySpi`)**: Used by server nodes to find each other and maintain the cluster topology. (Configured to use ports `47500+`).
2. **Communication SPI (`TcpCommunicationSpi`)**: Used by server nodes to exchange data and route requests. (Configured to use ports `41700+`).
3. **Client Connector**: A separate lightweight protocol used by external Thin Clients to connect to the cluster. (Configured to use ports `10800+`).

---

## Getting Started

### Prerequisites
- **Java 17 or higher** (Project was tested on Java 22).
- **Gradle** (or use the included Gradle Wrapper).
- **IntelliJ IDEA** (Recommended for running the multiple configurations).

### 1. Build the Project
Ensure dependencies are downloaded and the project compiles:
```bash
./gradlew build
```

### 2. Start the Cluster Nodes
The cluster relies on a **Seed Node** (`IgniteNode 2`) which listens on the discovery port `47502`.

You need to run the `IgniteNode` class three times with different arguments:
- **Node 2 (Coordinator)**: Program arguments: `2`
- **Node 0**: Program arguments: `0`
- **Node 1**: Program arguments: `1`

### 3. Run the Thin Client
Once the cluster is up and running, execute `MyIgniteClient`. It will connect to the cluster via the client connector ports (`10800`, `10801`, `10802`) and insert/retrieve data from the `customers` cache.

---

## Critical: JVM Configuration for Java 17+

Apache Ignite uses reflection and internal memory operations (`sun.misc.Unsafe`, `java.nio`) that are restricted by default in Java 17+ due to the Java Module System (`JPMS`).

**If you do not apply these VM options, the application will crash with an `InaccessibleObjectException` (e.g., `module java.base does not "opens java.nio"`).**

### How to add VM Options in IntelliJ IDEA:
1. Go to **Run** -> **Edit Configurations...**
2. Select your configuration (e.g., `Ignite Node 1` or `MyIgniteClient`).
3. Click **Modify options** (top right) and select **Add VM options**.
4. Paste the following arguments into the **VM options** field:

### Advanced VM Options (Required for both Nodes and Client)

Copy and paste this complete set of arguments. This opens the necessary `java.base` packages to allow Ignite to function correctly:

```text
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.util.concurrent=ALL-UNNAMED
--add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED
--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED
```

> **Note:** The first line `--add-opens=java.base/java.nio=ALL-UNNAMED` specifically fixes the `java.nio.DirectByteBuffer.address field is unavailable` error. However, it is highly recommended to use the full block above to prevent future unrelated reflection errors.

---

## 💡 Troubleshooting

- **`Work directory path must be absolute`**: Ignite requires absolute paths for storing data. In `IgniteNode.java`, this is resolved using `System.getProperty("user.dir")`.


*Note This README was partially AI-assisted.*