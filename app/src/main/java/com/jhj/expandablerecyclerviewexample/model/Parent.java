package com.jhj.expandablerecyclerviewexample.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.jhj.expandablerecyclerview.model.ParentItem;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class Parent implements ParentItem<Child>, Parcelable {
    private static final String TAG = "Parent";

    private boolean isExpandable = true;

    private boolean isInitiallyExpanded = true;

    private int dot;

    private int type;

    private String info;

    private List<Child> mChildren;

    public Parent() {
    }

    private Parent(Parcel in) {
        isExpandable = in.readByte() != 0;
        isInitiallyExpanded = in.readByte() != 0;
        dot = in.readInt();
        type = in.readInt();
        info = in.readString();
        mChildren = in.createTypedArrayList(Child.CREATOR);
    }

    public static final Creator<Parent> CREATOR = new Creator<Parent>() {
        @Override
        public Parent createFromParcel(Parcel in) {
            return new Parent(in);
        }

        @Override
        public Parent[] newArray(int size) {
            return new Parent[size];
        }
    };

    public void setChildren(List<Child> children)
    {
        mChildren = children;
    }

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
    public List<Child> getChildItems() {
        return mChildren;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        isInitiallyExpanded = initiallyExpanded;
    }

    @Override
    public boolean isExpandable() {
        return isExpandable;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return isInitiallyExpanded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isExpandable ? 1 : 0));
        dest.writeByte((byte) (isInitiallyExpanded ? 1 : 0));
        dest.writeInt(dot);
        dest.writeInt(type);
        dest.writeString(info);
        dest.writeTypedList(mChildren);
    }
}
