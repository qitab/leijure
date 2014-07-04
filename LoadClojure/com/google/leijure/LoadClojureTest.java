// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
package com.google.leijure;

import com.google.leijure.LoadClojure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
// import java.util.ListUtils;

/**
 * Trivial class to load and use Clojure in a Java application that doesn't include it by default.
 */
public class Test {
    public class A {
        int x;
    }
    public class B extends A {
        int y;
        public B(int ix,int iy) { x = ix ; y = iy; };
    }
    public int getX(A a) { return a.x; }

    public static List<String> foo = new LinkedList<String>();
    public static void main(String[] args) throws Exception {
        LinkedList<String> arglist = new LinkedList<String>(Arrays.asList(args));
        foo.add("1");
        foo.add("2");
        new LoadClojure().loadString("(println com.google.leijure.Test/foo)");
        System.out.println(Test.class.getMethods("getX")); // , A.class));
  }
}
