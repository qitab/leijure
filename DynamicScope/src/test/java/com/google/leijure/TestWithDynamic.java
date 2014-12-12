// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import com.google.leijure.WithDynamic;
import com.google.leijure.DynamicScope;

import java.util.concurrent.Callable;

import junit.framework.TestCase;

/**
 * Testing dynamic variables
 */
public class TestWithDynamic extends TestCase {

    public void testWithDynamic() {
        new WithDynamic.Run("a", "b") {
            public void run () {
                assertEquals(WithDynamic.get("a"), "b");
                new WithDynamic.Run("a", "c", "b", "d") {
                    public void run () {
                        assertEquals(WithDynamic.get("a"), "c");
                        assertEquals(WithDynamic.get("b"), "d");
                        assertEquals(WithDynamic.get("c"), null);
                        assertEquals("ce", new WithDynamic.Call<String> ("b", "e") {
                                public String call () {
                                    return (String)WithDynamic.get("a") + (String)WithDynamic.get("b");
                                }}.get());
                    }};
            }};
    }
}
