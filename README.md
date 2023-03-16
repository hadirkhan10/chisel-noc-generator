Network On-Chip Generator
=======================

A network on chip generator that for now focuses on creating a 2-D mesh with deterministic routing

## Implemented features
### Scala model
The scala model creates a 2-D mesh network with nodes (`NodeSim` class), routers (`Router` class) and channels (`Channel` class). The model is then used to generate the routing tables for all the nodes in the mesh. A deterministic routing algorithm "dimension-order" routing is used to find the routes for each node and its destination to all other nodes. Also a packet is sent from one node to another to verify the functionality.

### Chisel hardware
The generator creates a 2-D mesh network given the mesh configuration parameters. It creates the routing table for each node and connects them together in a mesh like structure. It takes a request packet from the testbench to start the transmission of a packet from the source node to a destination node. Right now the hardware can transmit only a single payload phit inside a packet and at a given time only one node can send data to another node. 

## Running tests

```
sbt test
```

## What do the tests do?

### Scala Model
The `MeshNetworkModel` tests the mesh network model at Scala level. It creates a 2-D mesh given rows and columns and runs the following tests:
* test the routing table generation against hard-coded routing values
* send the packet from one node to another with different 2-D mesh configurations

### Co-simulation with hardware
The `MeshNetwork` class generates the hardware given the parameters of the mesh and then uses the scala model to test the various routes and the states that each node is expected to be in. 


### Dependencies

#### JDK 8 or newer

We recommend LTS releases Java 8 and Java 11. You can install the JDK as recommended by your operating system, or use the prebuilt binaries from [AdoptOpenJDK](https://adoptopenjdk.net/).

#### SBT or mill

SBT is the most common built tool in the Scala community. You can download it [here](https://www.scala-sbt.org/download.html).  
mill is another Scala/Java build tool without obscure DSL like SBT. You can download it [here](https://github.com/com-lihaoyi/mill/releases)

