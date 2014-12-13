DynamicScope
============

Java classes for dynamic scoping.


com.google.leijure.DynamicScope
-------------------------------

This is the main class for playing with a (per-thread) dynamic environment,
which is a `Map<Object, Object>` (actually, an `org.pcollections.HashPMap`)
where `null` is not valid as either key or value.
You can access the current thread's current bound value for key `k` with `DynamicScope.get(k)`
and its current map of all current bindings with `DynamicScope.getBindings()`.
Note that since `DynamicScope.get(k)` returns an `Object`,
you may have to downcast the result to your intended type: `(V)DynamicScope.get(k)`.

You locally bind a key `k` to value `v` around a dynamic scope with:

    WithDynamic.RunWith(k, v) { public void run() { ... statements in scope ... } };

and the body of the `run()` method with be run in a scope where the environment
has been extended with the additional binding of `k` to `v`.

The syntax is somewhat verbose, but it's plain Java, without any extension,
and some amount of verbosity is par for the course.
Java syntax pretty much required that the code in the scope be specified
as a method override in a new class, and this is the simplest we've found.
A variant taking advantage of Java 8 lambda expression is conceivable,
but hasn't been implemented yet, and might not save that much verbosity.

You can bind several keys at once by specifying
zero or more pairs of alternating keys and values,
and an optional single last argument of a `Map<K, V>` of keys and values:

    DynamicScope.RunWith(k1, v1, k2, v2, m) { public void run() { ... statements ... } };

You can run code that throws any `Exception` using `RunWithE` instead of `RunWith`:

    DynamicScope.RunWithE(k1, v1, k2, v2, m) { public void run() throws Exception { ... } };

Or you can throw a subclass of Exception using `RunWithX<X>` instead of `RunWithE`:

    DynamicScope.<X>RunWithX(k1, v1, k2, v2, m) { public void run() throws X { ... } };

You can also locally bind variables around an expression that returns a value, using `Call`:

    A a = new DynamicScope.CallWith<A>(k, v, m) { public A call () { ... ; return aa; } }.get();

Mind the `.get()` at the end, that was made necessary because a bearable syntax
for the method override required the definition of an inner class, but
the constructor for that class couldn't return a result of a different class.

There also exist variants `CallWithE` and `CallWithX` for throwing exceptions:

    A a = new DynamicScope.CallWithE<A>(k, v, m) { public A call () throws Exception { ... } }.get();

    A a = new DynamicScope.CallWithX<A, X>(k, v, m) { public A call () throws X { ... } }.get();


Other files
-----------

Other files provide the internals for `DynamicScope`.
What they do is pretty straightforward.

