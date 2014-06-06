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
  private static ClassLoader loader;
  private static Class symbolClass;
  private static Class varClass;
  private static Class ifnClass;
  private static Method symbolInternMethod;
  private static Method symbolGetNamespace;
  private static Method symbolGetName;
  private static Method varInternMethod;
  private static Method ifnInvoke1Method;
  private static Object loadStringFunction;

  private static final Object[] nullargs = new Object[] { };

  /**
   * Returns the clojure Symbol with specified name.
   * @param qualifiedName the String holding the qualified, e.g. "clojure.core/read-string"
   * @return the interned Symbol (actually of type Symbol) or null if an exception occured
   */
  private static final Object intern (String qualifiedName) {
    final Object[] args = new Object[] { qualifiedName };
    try {
      return symbolInternMethod.invoke(null, qualifiedName);
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Returns the clojure Var object with specified name.
   * @param qualifiedName the String holding the qualified, e.g. {@code "clojure.core/println"}
   * @return the interned Var (actually of type {@code Var}) or {@code null} if an exception occured
   */
  private static final Object var (String qualifiedName) {
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
   * Evaluates a string as a sequence of Clojure expressions as by {@code load-string}
   * @param forms a String containing clojure expressions to evaluate
   * @return the value of the last expression in the string
   */
  public static final Object loadString (String forms) throws Exception {
    return ifnInvoke1Method.invoke(loadStringFunction, forms);
  }

  /**
   * Evaluates a sequence of strings each as a sequence of Clojure expressions
   * @param forms an {@code Iterable<String>} each containing clojure expressions
   * to evaluate as by {@link #loadString}
   * @return the value of the last expression in the last string, or {@code null}
   */
  public static Object loadStrings(Iterable<String> forms) throws Exception {
    Object result = null;
    for (String form : forms) {
      result = loadString(form);
    }
    return result;
  }

  /**
   * Computes a list of URLs where to look for the clojure jar.
   * @param jarURL an optional jar URL to override default locations.
   * @return a list of URLs.
   */
  private static URL[] findClojureJarUrls (URL jarUrl) {
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

  private static Boolean isInitialized () {
    return loadStringFunction != null;
  }

  /**
   * Initializes the LoadClojure class.
   * The init method must be successfully called at least once before
   * you may use {@link #loadString} or {@link #loadStrings}.
   * Subsequent calls are no-op.
   * @see #init(URL) to specify the location of the clojure.jar
   */
  public static void init () throws Exception {
    init(null);
  }
  /**
   * Initializes the LoadClojure class.
   * The init method must be successfully called at least once before
   * you may use {@link #loadString} or {@link #loadStrings}.
   * Subsequent calls are no-op.
   * @param jarUrl an optional URL where to look for the clojure jar.
   */
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

  public LoadClojure() throws Exception { init(); }
  public LoadClojure(URL jarUrl) throws Exception { init(jarUrl); }

  private static final String JAR_URL_OPTION = "--clojure_jar_url";

  /**
   * Evaluates Clojure expressions specified at the command-line.
   * This function, provided for test and demonstration purposes,
   * shows how to use LoadClojure.
   * @param args the command-line arguments, which may optionally start with
   * the argument <code>--clojure_jar_url &lt;url&gt;</code> to specify
   * which jar to load Clojure from.
   */
  public static void main(String[] args) throws Exception {
    // Process command line argument: accept --clojure_jar_url <url> as only option.
    URL jarUrl = null;
    LinkedList<String> arglist = new LinkedList<String>(Arrays.asList(args));
    if (!arglist.isEmpty()) {
      if (arglist.get(0).equals(JAR_URL_OPTION)) {
        arglist.pop();
        assert(!arglist.isEmpty());
        jarUrl = new URL(arglist.pop());
      } else if (arglist.get(0).startsWith(JAR_URL_OPTION + "=")) {
        jarUrl = new URL(arglist.pop().substring(JAR_URL_OPTION.length() + 1));
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
