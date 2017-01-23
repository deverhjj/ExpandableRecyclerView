package com.github.huajianjiang.expandablerecyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;

/**
 * Created by jhj_Plus on 2016/9/27.
 */
public class SavedState implements Parcelable {
    private static final String TAG = "SavedState";
    private boolean[] mExpandableState;
    private boolean[] mExpansionState;

    public SavedState(boolean[] expandableState, boolean[] expansionState) {
        mExpandableState = expandableState;
        mExpansionState = expansionState;
    }

    private SavedState(Parcel in) {
        mExpandableState = in.createBooleanArray();
        mExpansionState = in.createBooleanArray();
    }

    public boolean[] getExpandableState() {
        return mExpandableState;
    }

    public void setExpandableState(boolean[] expandableState) {
        mExpandableState = expandableState;
    }

    public boolean[] getExpansionState() {
        return mExpansionState;
    }

    public void setExpansionState(boolean[] expansionState) {
        mExpansionState = expansionState;
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
        @Override
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        @Override
        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(mExpandableState);
        dest.writeBooleanArray(mExpansionState);
    }
}
