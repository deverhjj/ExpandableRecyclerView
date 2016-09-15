package com.jhj.expandablerecyclerviewexample.model;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Child {
    private static final String TAG = "Child";

    private int dot;

    private int type;
    private int pos;
    private int adapterPos;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDot() {
        return dot;
    }

    public void setDot(int dot) {
        this.dot = dot;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getAdapterPos() {
        return adapterPos;
    }

    public void setAdapterPos(int adapterPos) {
        this.adapterPos = adapterPos;
    }
}
