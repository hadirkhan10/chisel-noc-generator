Network On-Chip Generator
=======================

A network on chip generator that for now focuses on creating a 2-D mesh with deterministic routing

## Implemented features
### Scala model
The scala model creates a 2-D mesh network with nodes (`NodeSim` class), routers (`Router` class) and channels (`Channel` class). The model is then used to generate the routing tables for all the nodes in the mesh. A deterministic routing algorithm "dimension-order" routing is used to find the routes for each node and its destination to all other nodes. Also a packet is sent from one node to another to verify the functionality.

### Chisel hardware
To be implemented

## Running tests

```
sbt test
```

## What do the tests do?
The `MeshNetworkModel` tests the mesh network model at Scala level. It creates a 2-D mesh given rows and columns and runs the following tests:
* test the routing table generation against hard-coded routing values
* send the packet from one node to another with different 2-D mesh configurations


### Dependencies

#### JDK 8 or newer

We recommend LTS releases Java 8 and Java 11. You can install the JDK as recommended by your operating system, or use the prebuilt binaries from [AdoptOpenJDK](https://adoptopenjdk.net/).

#### SBT or mill

SBT is the most common built tool in the Scala community. You can download it [here](https://www.scala-sbt.org/download.html).  
mill is another Scala/Java build tool without obscure DSL like SBT. You can download it [here](https://github.com/com-lihaoyi/mill/releases)

