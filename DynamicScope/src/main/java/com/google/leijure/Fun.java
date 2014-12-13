// Copyright (c) 2014 Google, Inc.
// The use and distribution terms for this software are covered by the
// Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
// Original author: Francois-Rene Rideau <tunes@google.com>
package com.google.leijure;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

/**
 * Functions returning zero or one (R)esult, taking zero or one (A)rgument,
 * and throwing no exception, one (X)ception or all (E)exceptions.
 */
public class Fun {
    public interface RAE<R, A> {
        @Nullable public R call(@Nullable A a) throws Exception;
    }
    public interface RAX<R, A, X extends Exception> {
        @Nullable public R call(@Nullable A a) throws X;
    }
    public interface RA<R, A> {
        @Nullable public R call(@Nullable A a);
    }
    public interface RE<R> extends Callable<R> {
        @Nullable public R call() throws Exception;
    }
    public interface RX<R, X extends Exception> {
        @Nullable public R call() throws X;
    }
    public interface R<R> extends RE<R> {
        @Nullable public R call();
    }
    public interface VAE<A> {
        public void run(@Nullable A a) throws Exception;
    }
    public interface VAX<A, X extends Exception> {
        public void run(@Nullable A a) throws X;
    }
    public interface VA<A> {
        public void run(@Nullable A a);
    }
    public interface VE {
        public void run() throws Exception;
    }
    public interface VX<X extends Exception> {
        public void run() throws X;
    }
    public interface V extends Runnable {
        public void run();
    }
}
