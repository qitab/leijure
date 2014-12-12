com.google.leijure.LoadClojure
==============================

Java class com.google.leijure.LoadClojure to dynamically load Clojure and evaluate code with it.

Suppose you have an application with which you somehow do not want to bundle Clojure,
but that you like to once in a while be able to query or control using Clojure.
For asynchronous access, you could use `liverepl`. But for synchronous queries,
here is `LoadClojure`. You can evaluate clojure code with:

        staticVariable = ...
        new LoadClojure().loadString("(println my.fully.qualified.Class/staticVariable)");

Alternatively, you can:

        clojure = new LoadClojure(new URL("file:///home/me/jars/clojure.jar"));
        clojure.loadStrings(Arrays.asList(argv));

Or you can:

        new LoadClojure(new URL("file:///home/me/jars/clojure.jar"));
        LoadClojure.loadStrings(Arrays.asList(argv));


`loadString` will load code from one string and return the results from the last evaluated form.
`loadStrings` will load code from each of many strings and return the results from the last form
of the last string.

The `LoadClojure` constructor will try to locate your `clojure`.jar in various ways:

  * from the current `ClassLoader`,
  * from a user-specified `URL` (passed to the `LoadClojure` constructor),
  * from the Java system property `clojure.jar.url`,
  * from the URL specified by environment variable `CLOJURE_JAR_URL`,
  * from the file `~/local/share/java/clojure.jar`,
  * from the file `/usr/local/share/java/clojure.jar`,
  * from the file `/usr/share/java/clojure.jar`.
    (The `share/java/*.jar` is a convention notably followed by Debian.)

You may specify a jar that has clojure plus additional code you want loaded with it,
you may load clojure then use clojure to load additional code.
