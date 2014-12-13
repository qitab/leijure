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
import org.pcollections.PMap;


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

    public HashPMap<K, V> getBindings() {
        return environment.get();
    }

    public static <K, V> HashPMap<K, V> plusArray (HashPMap<K, V> bindings, Object... kv) {
        for (int i = 0; i < kv.length; i += 2) {
            bindings = bindings.plus((K)kv[i], (V)kv[i + 1]);
        }
        if (kv.length % 2 == 1) {
            bindings = bindings.plusAll((Map<K, V>)kv[kv.length - 1]);
        }
        return bindings;
    }

    @Nullable
    public V get(Object k) {
        return getBindings().<K, V>get(k);
    }

    public Set<Map.Entry<K, V>> entrySet () {
        return getBindings().entrySet();
    }

    public <T> T with(Map<? extends K, ? extends V> newBindings, Callable<T> thunk) throws Exception {
        return environment.<T>with(getBindings().plusAll(newBindings), thunk);
    }

    public <T> T with(Object[] kv, Callable<T> thunk) throws Exception {
        return environment.<T>with(plusArray(getBindings(), kv), thunk);
    }

    public <T> T with(K k, V v, Callable<T> thunk) throws Exception {
        return environment.<T>with(getBindings().plus(k,v), thunk);
    }

    public <T, X extends Exception> T with(Map<? extends K, ? extends V> newBindings,
                                           Fun.RX<T, X> thunk) throws X {
        return environment.<T, X>with(getBindings().plusAll(newBindings), thunk);
    }

    public <T, X extends Exception> T with(Object[] kv, Fun.RX<T, X> thunk) throws X {
        return environment.<T, X>with(plusArray(getBindings(), kv), thunk);
    }

    public <T, X extends Exception> T with(K k, V v, Fun.RX<T, X> thunk) throws X {
        return environment.<T, X>with(getBindings().plus(k,v), thunk);
    }

    public <T> T with(Map<? extends K, ? extends V> newBindings, Fun.R<T> thunk) {
        return environment.<T>with(getBindings().plusAll(newBindings), thunk);
    }

    public <T> T with(Object[] kv, Fun.R<T> thunk) {
        return environment.<T>with(plusArray(getBindings(), kv), thunk);
    }

    public <T> T with(K k, V v, Fun.R<T> thunk) {
        return environment.<T>with(getBindings().plus(k,v), thunk);
    }

    public void with(Map<? extends K, ? extends V> newBindings, Fun.VE thunk) throws Exception {
        environment.with(getBindings().plusAll(newBindings), thunk);
    }

    public void with(Object[] kv, Fun.VE thunk) throws Exception {
        environment.with(plusArray(getBindings(), kv), thunk);
    }

    public void with(K k, V v, Fun.VE thunk) throws Exception {
        environment.with(getBindings().plus(k,v), thunk);
    }

    public <X extends Exception> void with(Map<? extends K, ? extends V> newBindings, Fun.VX<X> thunk) throws X {
        environment.<X>with(getBindings().plusAll(newBindings), thunk);
    }

    public <X extends Exception> void with(Object[] kv, Fun.VX<X> thunk) throws X {
        environment.<X>with(plusArray(getBindings(), kv), thunk);
    }

    public <X extends Exception> void with(K k, V v, Fun.VX<X> thunk) throws X {
        environment.<X>with(getBindings().plus(k,v), thunk);
    }

    public void with(Map<? extends K, ? extends V> newBindings, Runnable thunk) {
        environment.with(getBindings().plusAll(newBindings), thunk);
    }

    public void with(Object[] kv, Runnable thunk) {
        environment.with(plusArray(getBindings(), kv), thunk);
    }

    public void with(K k, V v, Runnable thunk) {
        environment.with(getBindings().plus(k,v), thunk);
    }
}
