Leijure
=======

This is a collection of utilities written in Clojure, or related to using Clojure.

So far, only one utility is available, com.google.leijure.LoadClojure,
a trivial java class to (dynamically) load Clojure and evaluate code with it.


com.google.leijure.LoadClojure
------------------------------

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


leijure.delta-position
----------------------

Clojure namespace with utilities to maintain line and column number information.

If you want to know what line and column you're at while reading a stream,
you can just use clojure.lang.LineNumberingPushbackReader...
unless you care about tab width, that said class doesn't handle
(inheriting this behavior or lack thereof from java.io.LineNumberingReader).
If you care about tab width, use this delta-position library instead.

A δposition is a map representing how a given string affects the position.
Importantly, combining two δposition is associative
— it's not merely a position that you have to compute from the beginning
of the file, but a delta that you can compute between arbitrary points.

Thus, if you want fast random access to the line and column
information from a random byte or character position in your file,
you can divide your file in chunks of strings,
compute δposition for each chunk in parallel,
and build a tree index that makes it easy to
compute the position of a random character or byte.
Of course, if you're using a variable length encoding,
such as the default, UTF-8, be sure to properly handle
the codepoints that may be broken at your chunk boundary, and e.g.
attach each partial codepoint to the previous chunk.

This (version of this) library makes no attempt to recognize
double-width characters. This shouldn't be too hard to implement, though.

fn positioned-stream [input {options}]
  given as input a java.lang.String or a java.lang.Reader,
  returns a lazy sequence of vectors [x l c]
  where x are the characters in the input, at line l and column c.
  options is an optional persistent map with the following option keywords:
  :δposition is the initial δposition, defaulting to null-δposition;
  :encoder is an encoder for the input charset, defaulting to utf8-encoder;
  :line-offset is the offset of the first line, defaulting to 0 (other common value 1);
  :column-offset is the offset of the first column, defaulting to 0 (other common value 1).

fn charset-encoder [name]
  Given a String name naming a charset (as per java.nio.charset.Charset/forName),
  returns an encoder for that charset.
var utf8-encoder
  The default encoder, for charset "UTF-8"

var null-δposition
  the neutral element for δposition composition, δposition for the empty string.
fn combine-δposition [& δpositions]
  the operation to combine δpositions.
fn char-δposition [char {encoder}]
  given a character and an optional encoder, returns the δposition for that character.
fn inc-δposition [δposition char {encoder}]
  given a δposition, a character and an optional encoder,
  returns the δposition after adding that character.
fn seq-δposition [s {encoder}]
  given a sequence of characters and an optional encoder,
  returns the δposition for that sequence.


Copyright and Disclaimer
------------------------

Copyright (c) 2014 Google, Inc.
The use and distribution terms for this software are covered by the
Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
Disclaimer: This code was originally written by a Google employee, but
Google does not offer support for this code.
