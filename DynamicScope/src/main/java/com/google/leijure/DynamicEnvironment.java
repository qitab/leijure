// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;


/**
 * Trivial implementation of a DynamicEnvironment mapping K to V.
 */
public class DynamicEnvironment<K, V> extends AbstractMap<K, V> {
    private final DynamicVariable<HashPMap<K, V>> environment;

    public DynamicEnvironment(@Nullable Map<K, V> initialValue) {
        HashPMap<K, V> initialMap;
        if (initialValue == null) {
            initialMap = HashTreePMap.<K, V>empty();
        } else if (initialValue instanceof HashPMap) {
            initialMap = (HashPMap<K, V>)initialValue;
        } else {
            initialMap = HashTreePMap.<K, V>empty().<K, V>plusAll(initialValue);
        }
        environment = new DynamicVariable<HashPMap<K, V>>(initialMap);
    }

    public DynamicEnvironment() {
        this(null);
    }

    public HashPMap<K, V> getEnvironment() {
        return environment.get();
    }

    private static <K, V> HashPMap<K, V> plusArray (HashPMap<K, V> bindings, Object... kv) {
        for (int i = 0; i < kv.length; i += 2) {
            bindings = bindings.plus((K)kv[i], (V)kv[i + 1]);
        }
        return bindings;
    }

    @Nullable
    public V get(Object k) {
        return getEnvironment().<K, V>get(k);
    }

    public Set<Map.Entry<K, V>> entrySet () {
        return getEnvironment().entrySet();
    }

    public <T> T callWith(Map<? extends K, ? extends V> newBindings, Callable<T> thunk) throws Exception {
        return environment.<T>callWith(getEnvironment().plusAll(newBindings), thunk);
    }

    public <T> T callWith(Object[] kv, Callable<T> thunk) throws Exception {
        return environment.<T>callWith(plusArray(getEnvironment(), kv), thunk);
    }

    public <T> T callWith(K k, V v, Callable<T> thunk) throws Exception {
        return environment.<T>callWith(environment.get().plus(k,v), thunk);
    }

    public <T> T callWith(K k1, V v1, K k2, V v2, Callable<T> thunk) throws Exception {
        return environment.<T>callWith(environment.get().plus(k1,v1).plus(k2,v2), thunk);
    }

    public <T> T callWith(K k1, V v1, K k2, V v2, K k3, V v3, Callable<T> thunk) throws Exception {
        return environment.<T>callWith(environment.get().plus(k1,v1).plus(k2,v2).plus(k3,v3), thunk);
    }

    public void with(Map<? extends K, ? extends V> newBindings, Runnable thunk) {
        environment.with(environment.get().plusAll(newBindings), thunk);
    }

    public void with(Object[] kv, Runnable thunk) {
        environment.with(plusArray(environment.get(), kv), thunk);
    }

    public void with(K k, V v, Runnable thunk) {
        environment.with(environment.get().plus(k,v), thunk);
    }

    public void with(K k1, V v1, K k2, V v2, Runnable thunk) {
        environment.with(environment.get().plus(k1,v1).plus(k2,v2), thunk);
    }

    public void with(K k1, V v1, K k2, V v2, K k3, V v3, Runnable thunk) {
        environment.with(environment.get().plus(k1,v1).plus(k2,v2).plus(k3,v3), thunk);
    }
}
