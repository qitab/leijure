// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import com.google.leijure.DynamicScope;

import junit.framework.TestCase;

/**
 * Testing dynamic variables
 */
public class TestDynamicScope extends TestCase {

    public void testDynamicScope() {
        new DynamicScope.RunWith("a", "b") {
            public void run () {
                assertEquals(DynamicScope.get("a"), "b");
                new DynamicScope.RunWith("a", "c", "b", "d") {
                    public void run () {
                        assertEquals(DynamicScope.get("a"), "c");
                        assertEquals(DynamicScope.get("b"), "d");
                        assertEquals(DynamicScope.get("c"), null);
                        assertEquals("ce", new DynamicScope.CallWith<String> ("b", "e") {
                                public String call () {
                                    return (String)DynamicScope.get("a") + (String)DynamicScope.get("b");
                                }}.get());
                    }};
            }};
    }
}
