#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: ./run_demo.sh <mode> <provider>"
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

java $jvmArgs -cp build/libs/amazoncorrettocryptoprovider-1.0-SNAPSHOT.jar $classname
