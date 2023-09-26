# jodoro

A pomodoro desktop application.

## building locally

Create the `target` directory

```
mvn package
```

then you can run the JAR

```
java -jar target/jodoro-<version>.jar
```

### building the installer for macOS

If you're on macOS, you can install jodoro under Applications by first creating the installer with
`jpackage`:

```
jpackage \
   --input target \
   --name jodoro \
   --main-jar jodoro-<version>.jar \
   --type dmg \
   --java-options '--enable-preview'
```

and install the application like you would with any other `.dmg` file.

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

## to do

* keyboard shortcuts (lots of them)
* de-clutter gui constructor
* graphs for # of sessions 
* add input field validation
* fix bug occurring when input fields are modified on a running session
* add app icon
* use maven
* add icons for play & pause buttons
* add quick settings in settings pane
* add a second sound if timer is not respected
* add workflow using `jpackage`