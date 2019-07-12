#!/bin/bash

#Wrapper to run the jar file.
if [ $# -ne 2 ]; then
    echo "Usage: ./run_demo.sh <secureRandom/cipher> <default/accp>"
    exit 1
fi

classname="com.amazon.corretto.demo.SecureRandomDemo"
jvmArgs=""

if [ $1 = "cipher" ]; then
    classname="com.amazon.corretto.demo.CipherDemo"
fi

if [ $2 = "accp" ]; then
    jvmArgs="-Djava.security.properties=amazon-corretto-crypto-provider.security"
fi

echo java $jvmArgs -cp build/libs/ACCPDemoApp-1.0-SNAPSHOT.jar $classname
java $jvmArgs -cp build/libs/ACCPDemoApp-1.0-SNAPSHOT.jar $classname

# java -cp build/libs/ACCPDemoApp-1.0-SNAPSHOT.jar:build/libs/AmazonCorrettoCryptoProvider-1.1.0-linux-x86_64.jar -Djava.security.properties=amazon-corretto-crypto-provider.security com.amazon.corretto.demo.CipherDemo
