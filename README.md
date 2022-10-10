# jodoro
A pomodoro desktop application.

## building locally
Create the `out` directory

```
mkdir out && cd out
```

here you'll put the `.class` files and crate the JAR file

```
javac -d . ../src/*.java
jar cfm jodoro.jar ../META-INF/MANIFEST.mf *.class ../media/*
java -jar jodoro.jar
```


## to do
* timer should only switch state w/ user interaction
* keyboard shortcuts (lots of them)
* de-clutter gui constructor
* graphs for # of sessions 
* add input field validation
* add app icon
* use maven
* add icons for play & pause buttons
 