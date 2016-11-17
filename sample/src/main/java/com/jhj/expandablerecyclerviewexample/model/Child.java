package com.jhj.expandablerecyclerviewexample.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Child implements Parcelable {
    private static final String TAG = "Child";

    private int dot;

    private int type;

    private String info;

    public Child() {
    }

    private Child(Parcel in) {
        dot = in.readInt();
        type = in.readInt();
        info = in.readString();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dot);
        dest.writeInt(type);
        dest.writeString(info);
    }
}
