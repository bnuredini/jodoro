# jodoro

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
* hide settings on startup
* declutter gui constructor
* create gap between text fields
* save data
* graphs for # of sessions 

















































































































done
----
* use minutes & seconds instead of just seconds
* add sounds
