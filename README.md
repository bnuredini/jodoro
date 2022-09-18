# jodoro

## building locally
Create the `out` directory

```
mkdir out && cd out
```

here you'll put the `.class` files and create the JAR file.

```
javac -d . ../src/*.java
jar cfm jodoro.jar ../META-INF/MANIFEST.mf *.class ../media/*
java -jar jodoro.jar
```


## to do
* add app icon
* timer should only switch state w/ user interaction
* hide settings on startup
* declutter gui constructor
* create gap between text fields
* save data
* graphs for # of sessions 
