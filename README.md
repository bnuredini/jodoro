# jodoro

A pomodoro desktop application.

## trying it out 

### installing

Head over to the [Releases](https://github.com/bnuredini/jodoro/releases) page and select the
appropriate installer for your operating system. If you can't find your system listed there, you can
try to build the project locally. 

### building locally

First, make sure you have Java 17 installed. Then, you can create the `target` directory by running:

```
mvn package
```

This generates a JAR file under the newly-created target directory. Run the JAR using `java -jar`.

```
java -jar target/jodoro-<version>.jar
```

(Make sure to substitue `<version>` with the version you just built.)

### building the installer for macOS

If you're on macOS and want to build the installer from scrach instead of using the one provided
under [releases](https://github.com/bnuredini/jodoro/releases), you can use `jpackage`. First make
sure to build the project by running `mvn package`, then run  

```
jpackage \
   --input target \
   --name jodoro \
   --main-jar jodoro-<version>.jar \
   --type dmg \
   --java-options '--enable-preview'
```

which will output a `.dmg` file that you can use like any other installer. After the install, you
should be able to find jodoro under Application.

### building the installer for Linux

To create a `.pkg` file with `jpackage` run the following:   

```
jpackage \
   --input target \
   --name jodoro \
   --main-jar jodoro-<version>.jar \
   --type pkg \
   --java-options '--enable-preview'
``` 

If you need to create a `.deb` file, just use `--type deb` instead. "app-image", "exe", "msi", and
"rpm" are also supported.

## license

Licensed under MIT. For more information, see the [license file](./LICENSE).