// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.pcollections.HashPMap;

/**
 * Trivial implementation of a DynamicScope providing get(key) to read bindings,
 * and RunWith() and CallWith() to dynamically define new bindings in the current thread.
 */
public class DynamicScope {
    private static ThreadLocal<DynamicEnvironment<Object,Object>> environment =
        new ThreadLocal<DynamicEnvironment<Object,Object>> () {
        @Override protected DynamicEnvironment<Object,Object> initialValue () {
            return new DynamicEnvironment<Object,Object> ();
        }};

    public static DynamicEnvironment<Object,Object> getEnvironment() {
        return environment.get();
    }

    public static HashPMap<Object,Object> getBindings() {
        return getEnvironment().getBindings();
    }

    public static Object get(Object key) {
        return getEnvironment().get(key);
    }

    public static abstract class RunWith implements Fun.V {

        public RunWith (Map<?,?> m) {
            getEnvironment().with(m, this);
        }

        public RunWith (Object k, Object v) {
            getEnvironment().with(k, v, this);
        }

        public RunWith (Object... kv) {
            getEnvironment().with(kv, this);
        }
    }

    public static abstract class RunWithX<X extends Exception> implements Fun.VX<X> {

        public RunWithX (Map<?,?> m) throws X {
            getEnvironment().<X>with(m, this);
        }

        public RunWithX (Object k, Object v) throws X {
            getEnvironment().<X>with(k, v, this);
        }

        public RunWithX (Object... kv) throws X {
            getEnvironment().<X>with(kv, this);
        }
    }

    public static abstract class RunWithE implements Fun.VE {

        public RunWithE (Map<?,?> m) throws Exception {
            getEnvironment().with(m, this);
        }

        public RunWithE (Object k, Object v) throws Exception {
            getEnvironment().with(k, v, this);
        }

        public RunWithE (Object... kv) throws Exception {
            getEnvironment().with(kv, this);
        }
    }

    private static Object[] Array1(Object x) {
        return new Object[] { x };
    }

    private static Object[] Array2(Object x, Object y) {
        return new Object[] { x, y };
    }

    public static abstract class CallWith <R> implements Fun.R<R> {
        private Object[] kv;

        public R get () {
            return getEnvironment().<R>with(kv, this);
        }

        public CallWith (Map<? extends Object,? extends Object> m) {
            kv = Array1(m);
        }

        public CallWith (Object k, Object v) {
            kv = Array2(k, v);
        }

        public CallWith (Object... kvl) {
            kv = kvl;
        }
    }

    public static abstract class CallWithX <R, X extends Exception> implements Fun.RX<R, X> {
        private Object[] kv;

        public R get () throws X {
            return getEnvironment().<R, X>with(kv, this);
        }

        public CallWithX (Map<? extends Object,? extends Object> m) {
            kv = Array1(m);
        }

        public CallWithX (Object k, Object v) {
            kv = Array2(k, v);
        }

        public CallWithX (Object... kvl) {
            kv = kvl;
        }
    }

    public static abstract class CallWithE <R> implements Fun.RE<R> {
        private Object[] kv;

        public R get () throws Exception {
            return getEnvironment().<R>with(kv, this);
        }

        public CallWithE (Map<? extends Object,? extends Object> m) {
            kv = Array1(m);
        }

        public CallWithE (Object k, Object v) {
            kv = Array2(k, v);
        }

        public CallWithE (Object... kvl) {
            kv = kvl;
        }
    }
}
