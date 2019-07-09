## Description
This benchmark evaluates the performance of [Amazon Corretto Crypto Provider](https://github.com/corretto/amazon-corretto-crypto-provider) libraries with the default libraries in [Corretto8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html)

## Runbook
1. Make sure [Corretto8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html) is installed
 and set as `JAVA_HOME`.

2. Execute the benchmark from its current folder:
 * Linux
   ```
   ../gradlew jmh
   ```

3. Alternatively, Execute the benchmark from the jmh-benchmarks folder:
 * Linux
   ```
   ./gradlew :ACCP:jmh
   ```