package com.github.huajianjiang.expandablerecyclerview.sample;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.github.huajianjiang.expandablerecyclerview.util.Logger;

import java.util.ArrayList;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyDialog extends DialogFragment {
    private static final String TAG = "MyDialog";
    public static final String REQUEST = "request";

    private static final String PARENT_TYPE = "parent";
    private static final String CHILD_TYPE = "child";
    private static final String MOVE_PARENT_TYPE = "move_parent";
    private static final String MOVE_CHILD_TYPE = "move_child";
    private static final String ITEM_OPERATE_TYPE = "Item";
    private static final String ITEM_RANGE_OPERATE_TYPE = "ItemRange";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogStyle).setView(
                R.layout.table).setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> result = checkInput(getDialog().findViewById(R.id.table));
                        if (result == null || result.isEmpty()) {
                            return;
                        }
                        Fragment targetFragment = getTargetFragment();
                        Intent data = new Intent();
                        data.putStringArrayListExtra(REQUEST, result);
                        targetFragment.onActivityResult(SingleRvFragment.REQUEST_RESULT,
                                Activity.RESULT_OK, data);
                    }
                }).setNegativeButton(getString(R.string.cancel), null).create();
        return dialog;
    }



    private ArrayList<String> checkInput(View table) {
        if (!(table instanceof TableLayout)) return null;
        ArrayList<String> result = new ArrayList<>();
        TableLayout tableLayout = (TableLayout) table;
        final int childCount = tableLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View tableChild = tableLayout.getChildAt(i);
            if (tableChild instanceof TableRow) {
                TableRow tableRow = (TableRow) tableChild;
                final int count = tableRow.getChildCount();
                for (int j = 0; j < count; j++) {
                    View childView = tableRow.getChildAt(j);
                    if (!(childView instanceof EditText)) continue;
                    EditText editText = (EditText) childView;
                    String type = editText.getHint().toString();
                    String method = (String) editText.getTag();
                    String input = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(input)) continue;
                    String[] methods = input.split("\n");
                    for (String m : methods) {
                        String methodType = "";
                        String[] args = m.split(",");
                        if (type.equals(PARENT_TYPE)) {
                            if (args.length == 1) {
                                methodType = ITEM_OPERATE_TYPE;
                            } else if (args.length == 2) {
                                methodType = ITEM_RANGE_OPERATE_TYPE;
                            }
                        } else if (type.equals(CHILD_TYPE)) {
                            if (args.length == 2) {
                                methodType = ITEM_OPERATE_TYPE;
                            } else if (args.length == 3) {
                                methodType = ITEM_RANGE_OPERATE_TYPE;
                            }
                        } else if (type.equals(MOVE_PARENT_TYPE)) {
                            if (args.length == 2) {
                                methodType = ITEM_OPERATE_TYPE;
                            }
                        } else if (type.equals(MOVE_CHILD_TYPE)) {
                            if (args.length == 4) {
                                methodType = ITEM_OPERATE_TYPE;
                            }
                        }
                        String methodName = String.format(method, methodType);
                        String ma = String.format(getString(R.string.methodAndArgs), methodName, m);
                        result.add(ma);
                    }
                }
            }
        }
        Logger.e(TAG, "requestList=" + result.toString());
        return result;
    }
}
