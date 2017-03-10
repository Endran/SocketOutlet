[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Language](https://img.shields.io/badge/language-java%2Fkotlin-yellowgreen.svg)](https://www.google.nl/search?q=kotlin)
[![](https://jitpack.io/v/endran/SocketOutlet.svg)](https://jitpack.io/#endran/SocketOutlet)

#### Master
[![Build Status](https://travis-ci.org/Endran/SocketOutlet.svg?branch=master)](https://travis-ci.org/Endran/SocketOutlet)
[![Coverage Status](https://coveralls.io/repos/github/Endran/SocketOutlet/badge.svg?branch=master)](https://coveralls.io/github/Endran/SocketOutlet?branch=master)

#### Develop
[![Build Status](https://travis-ci.org/Endran/SocketOutlet.svg?branch=develop)](https://travis-ci.org/Endran/SocketOutlet)
[![Coverage Status](https://coveralls.io/repos/github/Endran/SocketOutlet/badge.svg?branch=develop)](https://coveralls.io/github/Endran/SocketOutlet?branch=develop)

# Socket Outlet
Always wondered why there isn't a simple solution to pass objects around over sockets? Well, those days are over! 
_Socket Outlet_ solves your problems. By creating a couple of simple classes, you can communicate over sockets easily,
between several modules in your project. Ideally you have a shared codebase between the server and the client, but it is not mandatory. 
If you are not sharing a code base, make sure that the classes you have duplicated at all side share the same simple class 
name and the same field names. Jackson ObjectMappers are used for Json serialization.

## Usage

Say we have a server and several clients that share some base. In this shared code two POJOs are defined.
```kotlin
data class SimpleThing(val someString: String, val anInt: Int, val optionalBoolean: Boolean? = null)
data class ComplexThing(val someDate: ZonedDateTime, val simpleThing: SimpleThing, val simpleThingList: List<SimpleThing>)
```

Using SocketOutlet the clients and the server can tranfser these objects seamlessly. To send something all you need is the code below.
Further down we show how to setup the client and the server, and how to receive callbacks from send items.

```kotlin
client.send(simpleThing)
 
server.send("CLIENT_ID", complexThing)
```

### The Registry

To receive callbacks from recieved items you need to register `Outlets` to the `OutletRegistry`. An `Outlet` is a handler
for a specific type of POJO. Above we have defined the `SimpleThing` and `ComplexThing` POJOs, below we define the `Outlets`
for these POJOs:

```kotlin
class SimpleThingOutlet : Outlet<SimpleThing>(SimpleThing::class.java) {

    private val logger = CustomLogger(CustomLogger.Level.INFO)

    override fun onMessage(message: SimpleThing, egress: Egress) {
        logger.i { "Just received a SimpleThing: ${message.toString()}" }
    }
}
 
class ComplexThingOutlet : Outlet<ComplexThing>(ComplexThing::class.java) {

    private val logger = CustomLogger(CustomLogger.Level.INFO)

    override fun onMessage(message: ComplexThing, egress: Egress) {
        logger.i { "Just received a ComplexThing: ${message.toString()}" }
        egress.send(message.simpleThing)
        logger.i { "And replied with the SimpleThing: ${message.simpleThing.toString()}" }
    }
}
```

All we need to do now is register these `Outlets` to the `OutletRegistry` and we will receive callbacks on our `Outlet` 
as soon as the client or server receives a call.

```kotlin
val outletRegistry = OutletRegistry()
outletRegistry.register(SimpleThingOutlet())
outletRegistry.register(ComplexThingOutlet())
```

`Egress` is a convenience object to reply to callbacks from an outlet. The `Egress` will send an message to the origin 
of the callback (either the server or a specific client).

### The Client

Below is an example of initialising a `SocketOutletClient`, send a message, and stopping the client. You need to provide an client ID, server ip address and port, and the `OutletRegistry`

```kotlin
val client = SocketOutletClient("CLIENT_ID", ipAddress, port, outletRegistry)
client.start()
 
client.send(complexSharedThing)
 
Thread.sleep(5000)
client.stop()
```

Quality of service methods are available as well.
```kotlin
client.serverConnectedCallback = {
    println("Connected to server")
}
 
client.serverDisconnectedCallback = {
    println("Disconnected from server")
}
 
client.isRunning()
```

### The Server

Creating the `SocketOutletServer` is just as easy as the client. All you need is an `OutletRegistry` and a port number. 

```kotlin
val server = SocketOutletServer(outletRegistry)
server.open(port) // This will start a new thread for the server to run in. This call is non-blocking

while (server.running){
    // Do some otherstuff it you want
    Thread.sleep(1000)
}
 
server.close()
```

Quality of service methods are available as well, which also gives you the id of a client.
```kotlin
server.clientConnectedCallback = {
    println("Client with id $it connected")
}
 
server.clientDisconnectedCallback = {
    println("Client with id $it disconnected")
}
```

When sending a message to a client you need to know the ID of a client.
```kotlin
server.send("CLIENT_ID", simpleThing)
```

## Download

SocketOutlet is available in JitPack. SocketOutlet consists out of 3 modules, the client, the server and a core part. Core is a 
transitive dependency of both the client and the server, so don't worry about that. Usually you will only need either the 
server in your project, or the client.

### Gradle:

Add JitPack in your root build.gradle at the end of your repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependencies to the module build.gradle:
```groovy
dependencies {
    compile "com.github.endran.SocketOutlet:server:<version>" // Usually your module needs either the server
    compile "com.github.endran.SocketOutlet:client:<version>" // or the client, but not both in the same module
}
```

### Maven:
Add the JitPack repository to your build file
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add the dependencies. Usually your module needs either the server or the client, but not both in the same module.
```xml
<dependencies>
    <dependency>
        <groupId>com.github.endran.SocketOutlet</groupId>
        <artifactId>server</artifactId>
        <version>someVersion</version>
    </dependency>
    <dependency>
        <groupId>com.github.endran.SocketOutlet</groupId>
        <artifactId>client</artifactId>
        <version>someVersion</version>
    </dependency>
</dependencies>
```

## License

    Copyright 2017 David Hardy
    Copyright 2017 Craftsmenlabs
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
