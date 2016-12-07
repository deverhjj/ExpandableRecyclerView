package com.github.huajianjiang.expandablerecyclerview.sample.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.huajianjiang.expandablerecyclerview.widget.Parent;

import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyParent implements Parent<MyChild>, Parcelable {
    private static final String TAG = "MyParent";
    private boolean isExpandable = true;
    private boolean isInitiallyExpanded = true;
    private int dot;
    private int type;
    private String info;
    private List<MyChild> mMyChildren;

    public MyParent() {
    }

    private MyParent(Parcel in) {
        isExpandable = in.readByte() != 0;
        isInitiallyExpanded = in.readByte() != 0;
        dot = in.readInt();
        type = in.readInt();
        info = in.readString();
        mMyChildren = in.createTypedArrayList(MyChild.CREATOR);
    }

    public void setMyChildren(List<MyChild> myChildren)
    {
        mMyChildren = myChildren;
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

    public boolean hasChildren() {
        return mMyChildren != null && !mMyChildren.isEmpty();
    }

    @Override
    public List<MyChild> getChildren() {
        return mMyChildren;
    }

    @Override
    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return isInitiallyExpanded;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        isInitiallyExpanded = initiallyExpanded;
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
        dest.writeTypedList(mMyChildren);
    }

    public static final Creator<MyParent> CREATOR = new Creator<MyParent>() {
        @Override
        public MyParent createFromParcel(Parcel in) {
            return new MyParent(in);
        }

        @Override
        public MyParent[] newArray(int size) {
            return new MyParent[size];
        }
    };

    @Override
    public String toString() {
        return hashCode() + "";
    }
}
