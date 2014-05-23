// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
// Original author: Francois-Rene Rideau <tunes@google.com>

import java.lang.IllegalAccessException;
import java.lang.NoSuchMethodException;
import java.lang.System;
import java.lang.Thread;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;

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

    public static final Object intern (String qualifiedName) {
        final Object[] args = new Object[] { qualifiedName };
        try {
            return symbolInternMethod.invoke(null, qualifiedName);
        } catch (Exception ex) {
            return null;
        }
    }

    public static final Object var (String qualifiedName) {
        try {
            final Object sym = intern(qualifiedName);
            final String ns = (String)symbolGetNamespace.invoke(sym, nullargs);
            final String name = (String)symbolGetName.invoke(sym, nullargs);
            return varInternMethod.invoke(null, intern(ns), intern(name));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final Object loadString (String s) {
        try {
            return ifnInvoke1Method.invoke(loadStringFunction, s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static URL[] findClojureJarUrls (URL jarUrl) {
        final LinkedList<URL> urls = new LinkedList<URL>();
        try {
            urls.add(jarUrl);
        } catch (Exception ex) { }
        try {
            urls.add(new URL(System.getProperty("clojure.jar.url")));
        } catch (Exception ex) { }
        try {
            urls.add(new URL(System.getenv("CLOJURE_JAR_URL")));
        } catch (Exception ex) { }
        try {
            urls.add(new File(new File(System.getProperty("user.home")),
                              "local/share/java/clojure.jar").toURI().toURL());
        } catch (Exception ex) { }
        try {
            urls.add(new File("/usr/local/share/java/clojure.jar").toURI().toURL());
            urls.add(new File("/usr/share/java/clojure.jar").toURI().toURL());
        } catch (Exception ex) { }
        urls.removeAll(Collections.singleton(null));
        return urls.toArray(new URL[urls.size()]);
    }

    public static void init () {
        init(null);
    }
    @SuppressWarnings("unchecked")
    public static void init (URL jarUrl) {
        if (loadStringFunction == null) { // only initialize once
            try {
                // if Clojure is not present, load it from the proper jar URL
                try {
                    Class.forName("clojure.lang.RT");
                } catch (ClassNotFoundException ex) {
                    URL[] urls = findClojureJarUrls(jarUrl);
                    loader = new URLClassLoader(urls);
                    // See: http://dev.clojure.org/jira/browse/CLJ-260
                    Thread.currentThread().setContextClassLoader(loader);
                    Class.forName("clojure.lang.RT", true, loader);
                }

                // Now that Clojure is loaded, get references to its classes and methods
                symbolClass = Class.forName("clojure.lang.Symbol", true, loader);
                varClass = Class.forName("clojure.lang.Var", true, loader);
                ifnClass = Class.forName("clojure.lang.IFn", true, loader);

                symbolInternMethod =
                    symbolClass.getDeclaredMethod("intern", new Class[] { String.class });
                symbolGetNamespace =
                    symbolClass.getDeclaredMethod("getNamespace", new Class[] { });
                symbolGetName =
                    symbolClass.getDeclaredMethod("getName", new Class[] { });
                varInternMethod =
                    varClass.getDeclaredMethod("intern", new Class[] { symbolClass, symbolClass });
                ifnInvoke1Method =
                ifnClass.getDeclaredMethod("invoke", new Class[] { Object.class });

                loadStringFunction = var("clojure.core/load-string");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Object loadStrings(Iterable<String> forms) {
        Object result = null;
        for (String s: forms) {
            result = loadString(s);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // Process command line argument: accept --clojure_jar_url <url> as only option.
        URL jarUrl = null;
        LinkedList<String> arglist = new LinkedList<String>(Arrays.asList(args));
        if (!arglist.isEmpty() && arglist.get(0).equals("--clojure_jar_url")) {
            arglist.pop();
            if (!arglist.isEmpty()) {
                try {
                    jarUrl = new URL(arglist.get(0));
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                arglist.pop();
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
