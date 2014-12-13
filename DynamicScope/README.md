DynamicScope
============

Java classes for dynamic scoping.


com.google.leijure.WithDynamic
------------------------------

This is the main class for playing with a (per-thread) dynamic environment,
which is a `Map<Object, Object>` (actually, an `org.pcollections.TreeHashPMap`)
where `null` is not valid as either key or value.
You can access the current thread's current bound value for key `k` with
`WithDynamic.get(k)` and its current map of all current bindings with
`WithDynamic.getBindings()`. Note that since `get(k)` returns an `Object`,
you may have to downcast the result to your intended type.

You locally bind a key `k` to value `v` around a dynamic scope with:

    WithDynamic.Run(k, v) { public void run() { ... statements in scope ... } };

and the body of the `run()` method with be run in a scope where the environment
has been extended with the additional binding of `k` to `v`.

The syntax is somewhat verbose, but it's plain Java, without any extension,
and some amount of verbosity is par for the course.
A variant taking advantage of Java 8 lambda expression is conceivable,
but hasn't been implemented yet, and might not save that much.

You can bind several keys at once by specifying
zero or more pairs of alternating keys and values,
and an optional single last argument of a `Map<K, V>` of keys and values:

    WithDynamic.Run(k1, v1, k2, v2, m) { public void run() { ... statements ... } };

You can run code that throws any `Exception` using `RunE` instead of `Run`:

    WithDynamic.Run(k1, v1, k2, v2, m) { public void run() throws Exception { ... } };

Or you can throw a subclass of Exception using `RunX<X>` instead of `RunE`:

    WithDynamic.<X>RunX(k1, v1, k2, v2, m) { public void run() throws X { ... } };

You can also locally bind variables around an expression that returns a value, using `Call`:

    A a = new WithDynamic.Call<A>(k, v, m) { public A call () { ... ; return aa; } }.get();

Mind the `.get()` at the end, that was made necessary because a bearable syntax
for the method override required the definition of an inner class, but
the constructor for that class couldn't return a result of a different class.

There also exist variants `CallE` and `CallX` for throwing exceptions:

    A a = new WithDynamic.CallE<A>(k, v, m) { public A call () throws Exception { ... } }.get();

    A a = new WithDynamic.CallX<A, X>(k, v, m) { public A call () throws X { ... } }.get();


Other files
-----------

Other files provide the internals for `WithDynamic`.
What they do is pretty straightforward.

