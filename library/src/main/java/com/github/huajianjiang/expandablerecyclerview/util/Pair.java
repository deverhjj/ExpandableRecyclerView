package com.github.huajianjiang.expandablerecyclerview.util;

/**
 * @author HuaJian Jiang.
 *         Date 2016/11/20.
 */
public class Pair<F, S> {
    private static final String TAG = Pair.class.getSimpleName();
    public F first;
    public S second;

    public Pair() {
        this((F) null, (S) null);
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Pair<F, S> create() {
        return create((F) null, (S) null);
    }

    public static <F, S> Pair<F, S> create(F first, S second) {
        return new Pair(first, second);
    }

}
