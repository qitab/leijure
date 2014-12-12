// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.concurrent.Callable;

/**
 * Trivial implementation of a DynamicScope
 */
public class DynamicScope implements Runnable {
    public void run() { }
    public static class Call <T> implements Callable<T> {
        public T call () throws Exception { return null; }
    }
}
