Network On-Chip Generator
=======================

A network on chip generator that generates multi-radix n-mesh networks

## Implemented features
### Scala model
The scala model creates a 2-D mesh network with nodes (`NodeSim` class), routers (`Router` class) and channels (`Channel` class). The model is then used to generate the routing tables for all the nodes in the mesh. A deterministic routing algorithm "dimension-order" routing is used to find the routes for each node and its destination to all other nodes. Also a packet is sent from one node to another to verify the functionality.

### Chisel hardware
The generator creates a 2-D mesh network given the mesh configuration parameters. 
It creates the routing table for each node and connects them together in a mesh like structure.
It takes a request packet from the testbench to start the transmission of a packet from the 
source node to a destination node.

### Parameters

| Name         | Purpose                                        |
|--------------|------------------------------------------------|
| nRows        | The number of rows in the network              |
| nCols        | The number of columns in the network           |
| phits        | The number of bits that our channel transmits  |
| payloadPhits | The number of payload phits in a packet        |

## Running tests

```
sbt test
```

## What do the tests do?

### Scala Model
The `MeshNetworkModel` tests the mesh network model at Scala level. 
It creates a multi-radix n-mesh given rows and columns and runs the following tests:
* test the routing table generation against hard-coded routing values using `assert` statements
* send the packet from one node to another with different 2-D mesh configurations

### Co-simulation with hardware
The `MeshNetwork` class generates the hardware given the parameters of the mesh and 
then uses the scala model to test the various routes and the states that each node
is expected to be in. 

### Printing of tests
The test for each configuration of the generated hardware does some printing to help
see what is happening. This sample output is taken from a mesh configuration with `nRows=2`,
`nCols=2`, `phits=16` and `payloadPhits=3`.
In this specific scenario Node 00 is sending data to Node 11. 
Here's how the mesh network looks like:

Node 00 `__` Node 10

&emsp; `|`     &emsp; &emsp; &emsp;  `|`

Node 01 `__` Node 11

The output looks like the following:
```
CYCLE: 0
starting the request from node (0,0) to node (1,1)
payload phits in the packet are: 3
writing the payload 
CYCLE: 1
writing the payload 
CYCLE: 2
writing the payload 
CYCLE: 3
all the payload phits to the node are written
sending the header to E node
CYCLE: 4
sending payload phits
CYCLE: 7
now sending packet to the next adjacent node
sending the header to S node
CYCLE: 8
sending payload phits
CYCLE: 11
packet reached the destination, storing the payload in internal buffer
the payload phit received is : UInt<16>(1)
the payload phit received is : UInt<16>(2)
the payload phit received is : UInt<16>(3)
```
The printing is pretty self-explanatory. During cycles 0,1 and 2 the data to send is being 
stored inside the Node 00's buffer. In clock cycle 3, Node 00 starts sending the header phit 
to the node on E direction (i.e Node 10). During cycle 4 it starts sending the payload phits.
Since three payload phits needs to be transmitted, it takes until clock cycle 6 for the 
transmission to end. Since Node 10 is not the destination, it starts forwarding the packet
to the next node in S direction (i.e Node 11) during clock cycle 7. The complete packet is 
transmitted on clock cycle 11. Since Node 11 is the destination node, it stores the data
in its internal buffer. The last three print statements just output the data stored in the
internal buffer which is `16'd1`, `16'd2` and `16'd3`.

## Generating Verilog

```
sbt 'runMain noc.MeshNetworkMain'
```

### Dependencies

#### JDK 8 or newer

We recommend LTS releases Java 8 and Java 11. You can install the JDK as recommended by your operating system, or use the prebuilt binaries from [AdoptOpenJDK](https://adoptopenjdk.net/).

#### SBT or mill

SBT is the most common built tool in the Scala community. You can download it [here](https://www.scala-sbt.org/download.html).  
mill is another Scala/Java build tool without obscure DSL like SBT. You can download it [here](https://github.com/com-lihaoyi/mill/releases)

