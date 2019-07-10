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
$ ./gradlew runCipher Default
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

```
#### Running Demo

To run with ACCP disabled 

```
# To run SecureRandom
$ docker run -it accp-demo runRandomDefault

# To run Cipher
$ docker run -it accp-demo runCipherDefault
```

To run with ACCP enabled

```
# To run SecureRandom
$ docker run -it accp-demo runRandomAccp

# To run Cipher
$ docker run -it accp-demo runCipherAccp
```
