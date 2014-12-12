// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.concurrent.Callable;
import java.util.Stack;

import javax.annotation.Nullable;


/**
 * Trivial implementation of a DynamicVariable
 */
public class DynamicVariable<A> {
    private final Stack<A> stack = new Stack<A> ();

    public DynamicVariable(@Nullable final A initialValue) {
        stack.push(initialValue);
    }

    public DynamicVariable() {
        this(null);
    }

    @Nullable
    public A get() {
        return stack.peek();
    }

    private void push(@Nullable A a) {
        stack.push(a);
    }

    private void pop() {
        stack.pop();
    }

    @Nullable
    public <B> B callWith (@Nullable A a, Callable<B> thunk) throws Exception {
        push(a);
        try {
            return thunk.call();
        }
        finally {
            pop();
        }
    }

    public void with (@Nullable A a, Runnable thunk) {
        push(a);
        try {
            thunk.run();
        }
        finally {
            pop();
        }
    }
}
