#!/bin/sh
mvn package
mvn exec:java -Dexec.mainClass=ApiServer
# java -classpath ./target/classes:$HOME/.m2/repository/org/json/json/20220320/json-20220320.jar ApiServer
