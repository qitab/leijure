// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
package com.google.leijure;

import com.google.leijure.LoadClojure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

import junit.framework.TestCase;

/**
 * Trivial class to load and use Clojure in a Java application that doesn't include it by default.
 */
public class TestLoadClojure extends TestCase {
    public class A {
        int x;
    }
    public class B extends A {
        int y;
        public B(int ix,int iy) { x = ix ; y = iy; };
    }
    public int getX(A a) { return a.x; }

    public void testLoadClojure () throws Exception {
        LoadClojure lc = new LoadClojure();
        assertEquals(4L, lc.loadString("(+ 2 2)"));
    }
}
