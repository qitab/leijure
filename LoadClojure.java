// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)

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
    public static Class SymbolClass;
    public static Class VarClass;
    public static Class IFnClass;
    public static Method SymbolInternMethod;
    public static Method SymbolGetNamespace;
    public static Method SymbolGetName;
    public static Method VarInternMethod;
    public static Method IFnInvoke1Method;
    public static Object LoadStringFunction;

    public static final Object[] nullargs = new Object[] { };

    public static final Object intern (String qualifiedName) {
        final Object[] args = new Object[] { qualifiedName };
        try {
            return SymbolInternMethod.invoke(null, qualifiedName);
        } catch (Exception ex) {
            return null;
        }
    }

    public static final Object var (String qualifiedName) {
        try {
            final Object sym = intern(qualifiedName);
            final String ns = (String)SymbolGetNamespace.invoke(sym, nullargs);
            final String name = (String)SymbolGetName.invoke(sym, nullargs);
            return VarInternMethod.invoke(null, intern(ns), intern(name));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final Object loadString (String s) {
        try {
            return IFnInvoke1Method.invoke(LoadStringFunction, s);
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

    public static Boolean init () {
        return init(null);
    }
    @SuppressWarnings("unchecked")
    public static Boolean init (URL jarUrl) {
        try {
            URL[] urls = findClojureJarUrls(jarUrl);
            loader = new URLClassLoader(urls);

            // See: http://dev.clojure.org/jira/browse/CLJ-260
            Thread.currentThread().setContextClassLoader(loader);

            Class.forName("clojure.lang.RT", true, loader); // initialize Clojure
            SymbolClass = Class.forName("clojure.lang.Symbol", true, loader);
            VarClass = Class.forName("clojure.lang.Var", true, loader);
            IFnClass = Class.forName("clojure.lang.IFn", true, loader);
            // ClojureClass = loader.loadClass("clojure.java.api.Clojure", true, loader);

            SymbolInternMethod =
                SymbolClass.getDeclaredMethod("intern", new Class[] { String.class });
            SymbolGetNamespace =
                SymbolClass.getDeclaredMethod("getNamespace", new Class[] { });
            SymbolGetName =
                SymbolClass.getDeclaredMethod("getName", new Class[] { });
            VarInternMethod =
                VarClass.getDeclaredMethod("intern", new Class[] { SymbolClass, SymbolClass });
            IFnInvoke1Method =
                IFnClass.getDeclaredMethod("invoke", new Class[] { Object.class });

            // ReadStringFunction = var("clojure.core/read-string");
            // EvalFunction = var("clojure.core/eval");
            LoadStringFunction = var("clojure.core/load-string");
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
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
        System.out.println(loadStrings(arglist));
    }
}
