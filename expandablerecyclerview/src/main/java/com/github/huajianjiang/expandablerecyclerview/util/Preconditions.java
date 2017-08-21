package com.github.huajianjiang.expandablerecyclerview.util;

import java.util.Collection;

/**
 * Title:
 * <p>Description:
 * <p>Author: Huajian Jiang
 * <br>Date: 2017/3/28
 * <br>Email: developer.huajianjiang@gmail.com
 */

public class Preconditions {

    public static boolean isNullOrEmpty(Object[] obj) {
        return obj == null || obj.length == 0;
    }

    public static boolean isNullOrEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static <T> T checkNoNull(T obj, String msg) {
        if (obj == null) throw new IllegalArgumentException(msg);
        return obj;
    }

}
