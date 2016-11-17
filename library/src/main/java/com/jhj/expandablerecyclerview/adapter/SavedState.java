package com.jhj.expandablerecyclerview.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.jhj.expandablerecyclerview.utils.Logger;

/**
 * Created by jhj_Plus on 2016/9/27.
 */
public class SavedState implements Parcelable {
    private static final String TAG = "SavedState";

    private boolean[] mExpansionState;

    public SavedState(boolean[] expansionState) {
        mExpansionState = expansionState;
    }

    private SavedState(Parcel in) {
        Logger.e(TAG,"***********create SavedState from Parcel*********");
        mExpansionState = in.createBooleanArray();
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
            Logger.e(TAG,"***********createFromParcel*********");
            return new SavedState(in);
        }

        @Override
        public SavedState[] newArray(int size) {
            Logger.e(TAG,"***********newArray*********");
            return new SavedState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Logger.e(TAG,"***********writeToParcel*********");
        dest.writeBooleanArray(mExpansionState);
    }
}
