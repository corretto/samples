## Description
This benchmark compares the performance of [Amazon Corretto Crypto Provider](https://github.com/corretto/amazon-corretto-crypto-provider) with the default provider in [Corretto 8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html)

## Runbook
1. Make sure [Corretto 8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html) is installed
 and set as `JAVA_HOME`.

2. To execute the benchmarks from the current folder `./samples/benchmarks/ACCP` run: 
 * Linux
   ```
   ../gradlew jmh
   ```
   
3. Alternatively, execute the benchmark from the `./samples/benchmarks` folder:
 * Linux
   ```
   ./gradlew :ACCP:jmh
   ```