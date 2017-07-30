#!/bin/sh

# Create tmp directory
TMP_NAME=`cat /dev/urandom | tr -cd 'a-f0-9' | head -c 32`
mkdir `echo $TMP_NAME`/
cd `echo $TMP_NAME`/

# Download latest BuildTools.jar
curl https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar --output BuildTools.jar

# Run BuildTools for all needed versions
VERSIONS[0]="1.8.8"

for i in "${VERSIONS[@]}"
do
  # Run BuildTools.jar for the version $i
  java -jar BuildTools.jar --rev `echo $i`

  # Delete all files but BuildTools.jar
  TMP_JAR=`tar -c BuildTools.jar | base64`
  rm -rf ./*
  echo "$TMP_JAR" | base64 --decode | tar -x
done

# Clean up tmp directory
cd ..
rm -rf `echo $TMP_NAME`
