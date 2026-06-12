#!/bin/bash
javac -d out --source-path src src/com/sd/goose/Main.java && java -cp out com.sd.goose.Main "$@"