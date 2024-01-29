# jodoro

A pomodoro desktop application.

## building locally

First, make sure you have Java 17 installed. Then, you can create the `target` directory by running:

```
mvn package
```

This generates a JAR files under the newly-created target directory. Run the JAR using `java -jar`.

```
java -jar target/jodoro-<version>.jar
```

### building the installer for macOS

If you're on macOS, you can install jodoro under Applications by creating the installer with
`jpackage`:

```
jpackage \
   --input target \
   --name jodoro \
   --main-jar jodoro-<version>.jar \
   --type dmg \
   --java-options '--enable-preview'
```

and then install the application like you would with any other `.dmg` file.

### building the installer for Linux

To create a `.pkg` with `jpackage` run the following:   

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

## releases

You can find both installers in the [Releases](https://github.com/bnuredini/jodoro/releases)
page, under Assets.

## to do

* add app icon
* fix bug occurring when input fields are modified on a running session
* add input field validation
* add quick settings buttons in settings pane
* keyboard shortcuts (lots of them)
* graphs for # of sessions 
* add icons for play & pause buttons
* add a second sound if timer is not respected
 