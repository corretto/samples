# Amazon Corretto Crypto Provider Demo

The project contains demo class demonstrating the performance gains in using the 
[Amazon Corretto Crypto Provider](https://github.com/corretto/amazon-corretto-crypto-provider). Amazon Corretto Crypto
Provider is a collection of high-performance cryptographic implementations exposed via standard JCA/JCE interface. 

The project contains 2 demos 

1. SecureRandom : This demo uses as example of generating 16000 bytes of random data using SecureRandom. The same 
program can be run using ACCP enabled or disabled.
1. Cipher : This demonstrates encryption of 16000 bytes of data. It can be run with both ACCP Enabled or Disabled.  

## Running the demo

The demo can be run in both a native linux machine or in a docker image. 

### Running on linux 

To run with ACCP disabled
```
# To run SecureRandom
$ ./gradlew runRandomDefault

# To run Cipher
$ ./gradlew runCipherDefault
```

To run with ACCP enabled 
```
# To run SecureRandom
$ ./gradlew runRandomAccp

# To run Cipher
$ ./gradlew runCipherAccp
```

### Running with docker

#### Prepare the docker image

```
$ docker build . -t accp-demo
$ docker run -it accp-demo
```
This will launch a docker shell. The below steps need to be executed in the docker shell. 

#### Running demo using run_demo.sh

The `run_demo.sh` provides a wrapper to execute the class within the jar file.

Format: 
```
./run_demo.sh <mode> <provider>
mode: secureRandom or cipher
provider: default or accp
```

To run with ACCP disabled
```
# To run SecureRandom
./build/run_demo.sh secureRandom default

# To run Cipher
./build/run_demo.sh cipher default
```

To run with ACCP enabled

```
# To run SecureRandom
./build/run_demo.sh secureRandom accp

#To run Cipher
./build/run_demo.sh cipher accp
```

#### Running demo using Gradle

To run with ACCP disabled 

```
# To run SecureRandom
$ ./gradlew runRandomDefault

# To run Cipher
$ ./gradlew runCipherDefault
```

To run with ACCP enabled

```
# To run SecureRandom
$ ./gradlew runRandomAccp

# To run Cipher
$ ./gradlew runCipherAccp


```
