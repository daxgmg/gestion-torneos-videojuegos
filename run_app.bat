@echo off
set /p CP=<classpath.txt
java -cp "target/classes;%CP%" Main
