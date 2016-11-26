package com.jhj.expandablerecyclerviewexample.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.jhj.expandablerecyclerview.util.Logger;
import com.jhj.expandablerecyclerviewexample.utils.Util;

/**
 * Created by jhj_Plus on 2016/9/27.
 */
public class Test implements Parcelable {

    private static final String TAG = "Test";

    private MyParent mMyParent = Util.getParent();

    private String mString="Test";

    public Test() {
    }

    private Test(Parcel in) {
        Logger.e(TAG, "create Test from Parcel");
        mMyParent = in.readParcelable(MyParent.class.getClassLoader());
        mString = in.readString();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            Logger.e(TAG, "createFromParcel");
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            Logger.e(TAG, "newArray");
            return new Test[size];
        }
    };

    public String getString() {
        return mString;
    }

    public void setString(String string) {
        mString = string;
    }

    public MyParent getMyParent() {
        return mMyParent;
    }

    public void setMyParent(MyParent myParent) {
        mMyParent = myParent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Logger.e(TAG,"writeToParcel");
        dest.writeParcelable(mMyParent, flags);
        dest.writeString(mString);
    }
}
