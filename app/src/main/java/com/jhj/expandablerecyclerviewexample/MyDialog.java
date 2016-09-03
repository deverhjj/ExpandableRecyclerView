package com.jhj.expandablerecyclerviewexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyDialog extends DialogFragment {
    private static final String TAG = "MyDialog";

    public static final String METHODS = "methods";

    private static final String PARENT_TYPE = "parent";
    private static final String CHILD_TYPE = "child";
    private static final String ITEM_OPERATE_TYPE = "Item";
    private static final String ITEM_RANGE_OPERATE_TYPE = "ItemRange";

    private String operateType="";

    private View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mRootView=getActivity().getLayoutInflater().inflate(R.layout.table,null);

        AlertDialog dialog=new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.title_input))
                .setView(mRootView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<String> methodList=new ArrayList<>(6);
                        checkInput(methodList);
                        if (methodList.isEmpty()) {
                            return;
                        }
                            Fragment targetFragment=getTargetFragment();
                            Intent data=new Intent();
                            data.putStringArrayListExtra(METHODS,methodList);
                            targetFragment.onActivityResult(MainFragment.REQUEST_RESULT, Activity
                                    .RESULT_OK,data);

                    }
                }).setNegativeButton("取消",null).create();

         setCancelable(false);

        return dialog;
    }


    private void checkInput(List<String> methodList) {
        TableLayout tableLayout= (TableLayout) mRootView.findViewById(R.id.table);
        final int childCount=tableLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View tableChild=tableLayout.getChildAt(i);
            if (tableChild instanceof TableRow) {
                boolean isGoodInput=true;
                TableRow tableRow= (TableRow) tableChild;
                final int count=tableRow.getChildCount();
                for (int j = 0; j < count; j++) {
                    View childView=tableRow.getChildAt(j);
                    if (childView instanceof EditText) {
                        EditText editText= (EditText) childView;
                        String type=editText.getHint().toString();
                        String input=editText.getText().toString().trim();
                        String method= (String) editText.getTag();
                        if (TextUtils.isEmpty(input)) continue;
                        String[] args=new String[3];
                        if (!(isGoodInput = isGoodInput(type, input,args))) {
                            methodList.clear();
                            break;
                        }
                        methodList.add(String.format(getString(R.string.methodAndArgs),String
                                .format(method,
                                        operateType),args[0],args[1],args[2]));
                    }
                }
                if (!isGoodInput) break;
            }
        }
    }


    private boolean isGoodInput(String type, String input,String[] args) {
        String[] inputInfo = input.split(",");
        if (inputInfo.length==0) return false;
        boolean goodInput = true;

        if (type.equals(MyDialog.PARENT_TYPE)) {
            if (inputInfo.length > 2) {
                goodInput = false;
            }

            for (int i = 0; i < inputInfo.length; i++) {
                String at = inputInfo[i].trim();
                try {
                    Integer.parseInt(at);
                    args[i]=at;
                } catch (NumberFormatException e) {
                    goodInput = false;
                    break;
                }
            }

            if (goodInput) {
                operateType=inputInfo.length==1?ITEM_OPERATE_TYPE:ITEM_RANGE_OPERATE_TYPE;
            }
        } else if (type.equals(MyDialog.CHILD_TYPE)) {
            if (inputInfo.length < 2 && inputInfo.length > 3) {
                return false;
            }
            for (int i = 0; i < inputInfo.length; i++) {
                String at = inputInfo[i].trim();
                try {
                    Integer.parseInt(at);
                    args[i]=at;
                } catch (NumberFormatException e) {
                    goodInput = false;
                    break;
                }
            }
            if (goodInput) {
                operateType=inputInfo.length==2?ITEM_OPERATE_TYPE:ITEM_RANGE_OPERATE_TYPE;
            }

        }
        return goodInput;
    }
}
