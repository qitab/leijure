// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * Trivial implementation of a DynamicVariable
 */
public class WithDynamic {
    private static ThreadLocal<DynamicEnvironment<Object,Object>> environment =
        new ThreadLocal <DynamicEnvironment<Object,Object>> () {
        @Override protected DynamicEnvironment<Object,Object> initialValue () {
            return new DynamicEnvironment<Object,Object> ();
        }
    };

    private static DynamicEnvironment<Object,Object> getEnvironment() {
        return environment.get();
    }

    public class Run implements Runnable {
        public void run () {}

        public Run (Map<?,?> m) {
            getEnvironment().with(m, this);
        }

        public Run (Object k, Object v) {
            getEnvironment().with(k, v, this);
        }

        public Run (Object k1, Object v1, Object k2, Object v2) {
            getEnvironment().with(k1, v1, k2, v2, this);
        }

        public Run (Object... kv) {
            getEnvironment().with(kv, this);
        }
    }

    public class Call <T> implements Callable<T> {
        private final Map<Object, Object> map;

        public T call () throws Exception { return null; }

        public T get () throws Exception {
            DynamicEnvironment<Object,Object> env = getEnvironment();
            return env.<T>with(map, this);
        }

        public Call (Map<? extends Object,? extends Object> m) {
            map = (Map<Object,Object>)m;
        }

        public Call (Object k, Object v) {
            map = new HashMap(1);
            map.put(k,v);
        }

        public Call (Object k1, Object v1, Object k2, Object v2) {
            map = new HashMap(2);
            map.put(k1,v1);
            map.put(k2,v2);
        }

        public Call (Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
            map = new HashMap(3);
            map.put(k1,v1);
            map.put(k2,v2);
            map.put(k3,v3);
        }

        public Call (Object... kv) {
            map = new HashMap(kv.length/2);
            for (int i = 0; i < kv.length; i += 2) {
                map.put(kv[i],kv[i + 1]);
            }
        }
    }
}
