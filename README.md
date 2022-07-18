# konogonka

[![Build Status](https://ci.redrise.ru/api/badges/desu/konogonka/status.svg)](https://ci.redrise.ru/desu/konogonka)

GitHub mirror. [Click here to get it from independent source code location](https://git.redrise.ru/desu/konogonka) 

Nightly builds could be found somewhere on [redrise.ru](https://redrise.ru)

Deep WIP multi-tool to work with NS-specific files / filesystem images.

### License

[GNU General Public License v3+](https://github.com/developersu/konogonka/blob/master/LICENSE)

<img src="screenshots/1.png" alt="drawing" width="250"/> <img src="screenshots/2.png" alt="drawing" width="250"/> <img src="screenshots/3.png" alt="drawing" width="250"/>

<img src="screenshots/4.png" alt="drawing" width="250"/> <img src="screenshots/5.png" alt="drawing" width="250"/> <img src="screenshots/6.png" alt="drawing" width="250"/>

<img src="screenshots/7.png" alt="drawing" width="250"/>

### Used libraries & resources
* [Bouncy Castle](https://www.bouncycastle.org/) for Java.
* [Java-XTS-AES](https://github.com/horrorho/Java-XTS-AES) by horrorho with minimal changes.
* [OpenJFX](https://wiki.openjdk.java.net/display/OpenJFX/Main)
* Few icons taken from: [materialdesignicons.com](http://materialdesignicons.com/)

#### Thanks 
* Switch brew wiki
* Original ScriesM software
* roothorick, [shchmue](https://github.com/shchmue/), He, other Team AtlasNX discord members for their advices, notes and examples!

### System requirements

JRE/JDK 8u60 or higher.

### Notes about usage

1. Start from clicking on 'settings' and importing keys. Use import. Don't waste your time.
2. To open sub-file from the file use right mouse click and select 'Open'. Supported formats listed below.

### Build this

1. Install JDK
2. Install Maven
3. $ git clone https://github.com/developersu/konogonka.git
4. $ mvn -B -DskipTests clean package
5. $ java -jar target/konogonka-0.x.x-jar-with-dependencies.jar

### Checklist

* [x] NSP (PFS0)
* [x] XCI (+HFS0)
* [x] TIK
* [x] XML 
* [x] NRO
* [x] NPDM support (to check)
* [ ] CERT support
* [ ] CNMT support
* [ ] NSO support
* [x] RomFS
* [ ] LogPrinter to singleton implementation. 
* [x] 'Save to folder' option