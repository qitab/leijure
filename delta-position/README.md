leijure.delta-position
======================

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
