MoneyX
=========

MoneyX is a purposely insecure only payment application

Setup
----
1. Install Gradle, the package manager, using ```sudo apt-get install gradle``` (Ubuntu/Debian) or ```brew install gradle``` (OSX).
2. Make sure Java8 (listed as version 1.8) is installed and the default Java on your system. Obtaining this will depend on your OS, but generally you can download the Oracle JDK from their [website](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
3. Once in the code directly, run ```gradle build``` to assemble the app and pull in dependencies
5. Set up a Redis server (```brew install redis```), and run it. You can easily start it in a terminal using the ```redis-server``` command.
7. Launch the application from the same directory with ```gradle run```
