# Amazon Corretto Crypto Provider Demo

The project contains demo class demonstrating the performance gains in using the 
[Amazon Corretto Crypto Provider](https://github.com/corretto/amazon-corretto-crypto-provider). Amazon Corretto Crypto
Provider is a collection of high-performance cryptographic implementations exposed via standard JCA/JCE interface. 

This demo uses as example of generating 16000 bytes of random data using SecureRandom. The same program can be run using
ACCP enabled or disabled. 

## Running the demo

The demo can be run in both a native linux machine or in a docker image. 

### Running on linux 

To run with ACCP disabled
```
$ ./gradlew runDefault
```

To run with ACCP enabled 
```
$ ./gradlew runAccp
```

### Running with docker

To run with ACCP disabled 

```
$ docker build . -t accp-demo

$ docker run -it accp-demo runDefault
```

To run with ACCP enabled

```
$ docker build . -t accp-demo

$ docker run -it accp-demo runAccp
```
