// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Trivial class to load and use Clojure in a Java application that doesn't include it by default.
 */
public class LoadClojure {
  public static ClassLoader loader;
  public static Class symbolClass;
  public static Class varClass;
  public static Class ifnClass;
  public static Method symbolInternMethod;
  public static Method symbolGetNamespace;
  public static Method symbolGetName;
  public static Method varInternMethod;
  public static Method ifnInvoke1Method;
  public static Object loadStringFunction;

  public static final Object[] nullargs = new Object[] { };

  /**
   * intern: given a qualified name as a String, e.g. "clojure.core/read-string",
   * return the clojure Symbol with specified name.
   */
  public static final Object intern (String qualifiedName) {
    final Object[] args = new Object[] { qualifiedName };
    try {
      return symbolInternMethod.invoke(null, qualifiedName);
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * var: given  a qualified name as a String, e.g. "clojure.core/println",
   * return the corresponding var object.
   */
  public static final Object var (String qualifiedName) {
    try {
      final Object sym = intern(qualifiedName);
      final String ns = (String) symbolGetNamespace.invoke(sym, nullargs);
      final String name = (String) symbolGetName.invoke(sym, nullargs);
      return varInternMethod.invoke(null, intern(ns), intern(name));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * loadString: given a String s, evaluate the string as a sequence of
   * Clojure expressions as by load-string, returning the value of the last expression.
   */
  public static final Object loadString (String s) throws Exception {
    return ifnInvoke1Method.invoke(loadStringFunction, s);
  }

  /**
   * loadStrings: given an Iterable<String>, evaluate each string as by loadString,
   * and return the value of the last expression.
   */
  @SuppressWarnings("unchecked")
  public static Object loadStrings(Iterable<String> forms) throws Exception {
    Object result = null;
    for (String s : forms) {
      result = loadString(s);
    }
    return result;
  }

  /**
   * given an optional jar URL as an override,
   * return a list of URLs where to look for the clojure jar.
   */
  public static URL[] findClojureJarUrls (URL jarUrl) {
    final LinkedList<URL> urls = new LinkedList<URL>();
    try {
      urls.add(jarUrl);
    } catch (Exception ex) { ; }
    try {
      urls.add(new URL(System.getProperty("clojure.jar.url")));
    } catch (Exception ex) { ; }
    try {
      urls.add(new URL(System.getenv("CLOJURE_JAR_URL")));
    } catch (Exception ex) { ; }
    try {
      urls.add(new File(new File(System.getProperty("user.home")),
              "local/share/java/clojure.jar").toURI().toURL());
    } catch (Exception ex) { ; }
    try {
      urls.add(new File("/usr/local/share/java/clojure.jar").toURI().toURL());
      urls.add(new File("/usr/share/java/clojure.jar").toURI().toURL());
    } catch (Exception ex) { ; }
    urls.removeAll(Collections.singleton(null));
    return urls.toArray(new URL[urls.size()]);
  }

  public static Boolean isInitialized () {
    return loadStringFunction != null;
  }

  /**
   * The init method must be successfully called at least once before
   * you may use loadString or loadStrings.
   * Subsequent calls are no-op.
   * init takes an optional argument, being a URL for the Clojure jar file.
   */
  public static void init () throws Exception {
    init(null);
  }
  @SuppressWarnings("unchecked")
  public static void init (URL jarUrl) throws Exception {
    if (!isInitialized()) { // only initialize once
      // if Clojure is not present, load it from the proper jar URL
      try {
        // initialize the Clojure runtime if present
        loader = Thread.currentThread().getContextClassLoader();
        Class.forName("clojure.lang.RT", true, loader);
      } catch (ClassNotFoundException ex) {
        // if not present, find it and retry
        URL[] urls = findClojureJarUrls(jarUrl);
        loader = new URLClassLoader(urls);
        // Clojure needs this. See: http://dev.clojure.org/jira/browse/CLJ-260
        Thread.currentThread().setContextClassLoader(loader);
        Class.forName("clojure.lang.RT", true, loader);
      }

      // Now that Clojure is loaded, get references to its classes and methods
      symbolClass = Class.forName("clojure.lang.Symbol", true, loader);
      varClass = Class.forName("clojure.lang.Var", true, loader);
      ifnClass = Class.forName("clojure.lang.IFn", true, loader);

      symbolInternMethod = symbolClass.getMethod("intern", String.class);
      symbolGetNamespace = symbolClass.getMethod("getNamespace");
      symbolGetName = symbolClass.getMethod("getName");
      varInternMethod = varClass.getMethod("intern", symbolClass, symbolClass);
      ifnInvoke1Method = ifnClass.getMethod("invoke", Object.class);

      loadStringFunction = var("clojure.core/load-string");
    }
  }

  @SuppressWarnings("unchecked")
  public LoadClojure() throws Exception { init(); }
  @SuppressWarnings("unchecked")
  public LoadClojure(URL jarUrl) throws Exception { init(jarUrl); }


  public static void main(String[] args) throws Exception {
    // Process command line argument: accept --clojure_jar_url <url> as only option.
    URL jarUrl = null;
    LinkedList<String> arglist = new LinkedList<String>(Arrays.asList(args));
    if (!arglist.isEmpty()) {
        if (arglist.get(0).equals("--clojure_jar_url")) {
            arglist.pop();
            assert(!arglist.isEmpty());
            jarUrl = new URL(arglist.pop());
        } else if (arglist.get(0).startsWith("--clojure_jar_url=")) {
            jarUrl = new URL(arglist.pop().substring(18));
        }
    }
    init(jarUrl);
    /* Now that we're ready to evaluate things,
       load all (remaining) arguments as clojure code using load-string,
       and return the last value.

       Note that if you want to print something, you'll have to do it yourself,
       with e.g. (println foo) — it's not very useful to use the Java printer,
       too much pain to get to the clojure printer, and even worse to make it
       configurable so that nothing's printed when not desired.
       (And we shall not afford the use of a real command-line parsing library
       to achieve this configuration, for we here aim at minimalism.)
    */
    loadStrings(arglist);
  }
}
