// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import com.google.leijure.DynamicVariable;
import com.google.leijure.DynamicScope;

import java.util.concurrent.Callable;

import junit.framework.TestCase;

/**
 * Testing dynamic variables
 */
public class TestDynamicVariable extends TestCase {

    public void testDynamicInteger() throws Exception {
        final DynamicVariable<Integer> var = new DynamicVariable<Integer>(0);
        assertEquals(var.get(), (Integer)0);
        Integer i = var.with(3, new Callable<Integer> () { @Override public Integer call() {
                    return var.get(); }
            });
        assertEquals(i, (Integer)3);
        var.with(5, new Runnable () { @Override public void run() {
            assertEquals(var.get(), (Integer)5);
        }});
    }
}
