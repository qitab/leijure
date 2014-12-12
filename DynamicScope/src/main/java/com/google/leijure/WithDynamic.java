// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.HashMap;
import java.util.Map;

// import javax.annotation.Nullable;


/**
 * Trivial implementation of a DynamicVariable
 */
public class WithDynamic {
    private static ThreadLocal<DynamicEnvironment<Object,Object>> environment =
        new ThreadLocal<DynamicEnvironment<Object,Object>> () {
        @Override protected DynamicEnvironment<Object,Object> initialValue () {
            return new DynamicEnvironment<Object,Object> ();
        }};

    public static DynamicEnvironment<Object,Object> getEnvironment() {
        return environment.get();
    }

    public static Object get(Object key) {
        return environment.get().get(key);
    }

    private static Map<Object, Object> singletonMap(Object key, Object value) {
        final Map<Object, Object> map = new HashMap(1);
        map.put(key, value);
        return map;
    }

    private static Map<Object, Object> arrayMap(Object... kv) {
        final Map<Object, Object> map = new HashMap(kv.length/2);
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i],kv[i + 1]);
        }
        return map;
    }

    public static abstract class Run implements Fun.V {

        public Run (Map<?,?> m) {
            getEnvironment().with(m, this);
        }

        public Run (Object k, Object v) {
            getEnvironment().with(k, v, this);
        }

        public Run (Object... kv) {
            getEnvironment().with(kv, this);
        }
    }

    public static abstract class RunX<X extends Exception> implements Fun.VX<X> {

        public RunX (Map<?,?> m) throws X {
            getEnvironment().<X>with(m, this);
        }

        public RunX (Object k, Object v) throws X {
            getEnvironment().<X>with(k, v, this);
        }

        public RunX (Object... kv) throws X {
            getEnvironment().<X>with(kv, this);
        }
    }

    public static abstract class RunE implements Fun.VE {

        public RunE (Map<?,?> m) throws Exception {
            getEnvironment().with(m, this);
        }

        public RunE (Object k, Object v) throws Exception {
            getEnvironment().with(k, v, this);
        }

        public RunE (Object... kv) throws Exception {
            getEnvironment().with(kv, this);
        }
    }

    public static abstract class CallE <R> implements Fun.RE<R> {
        private Map<Object, Object> map;

        public R get () throws Exception {
            return getEnvironment().<R>with(map, this);
        }

        public CallE (Map<? extends Object,? extends Object> m) {
            map = (Map<Object,Object>)m;
        }

        public CallE (Object k, Object v) {
            map = singletonMap(k, v);
        }

        public CallE (Object... kv) {
            map = arrayMap(kv);
        }
    }

    public static abstract class CallX <R, X extends Exception> implements Fun.RX<R, X> {
        private Map<Object, Object> map;

        public R get () throws X {
            return getEnvironment().<R, X>with(map, this);
        }

        public CallX (Map<? extends Object,? extends Object> m) {
            map = (Map<Object,Object>)m;
        }

        public CallX (Object k, Object v) {
            map = singletonMap(k, v);
        }

        public CallX (Object... kv) {
            map = arrayMap(kv);
        }
    }

    public static abstract class Call <R> implements Fun.R<R> {
        private Map<Object, Object> map;

        public R get () {
            return getEnvironment().<R>with(map, this);
        }

        public Call (Map<? extends Object,? extends Object> m) {
            map = (Map<Object,Object>)m;
        }

        public Call (Object k, Object v) {
            map = singletonMap(k, v);
        }

        public Call (Object... kv) {
            map = arrayMap(kv);
        }
    }
}
