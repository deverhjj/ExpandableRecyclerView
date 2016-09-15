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

import com.jhj.expandablerecyclerview.utils.Logger;
import com.jhj.expandablerecyclerviewexample.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhj_Plus on 2016/9/2.
 */
public class MyDialog extends DialogFragment {
    private static final String TAG = "MyDialog";

    /**
     * 匹配处理parent操作的参数输入格式（add，remove，change）
     * format：arg1 or arg1,arg2 or X\nX(X 为 arg1或arg2或两个的任意组合)
     * arg1：操作 position，arg2：操作 count
     * eg: 1表示在1的位置的一次parent操作，1,2 表示在position为1上2次parent操作，1,2\n3,4 表示有两次parent操作，position
     * 分别在1和3并且分别操作2,4次
     * note:参数之前用,隔开并且中间两边都没有其他多余字符，否则视为失败输入
     */
    private static final String REGX_BASE_PARENT="(\\d+,\\d+|\\d+)";
    private static final String REGX_PARENT=REGX_BASE_PARENT+"|"+REGX_BASE_PARENT+"\n"+REGX_BASE_PARENT;
    /**
     * 匹配处理child操作的参数输入格式（add，remove，change）
     * format：arg1,arg2 or arg1,arg2,arg3 or X\nX(X 为 arg1,arg2或arg1,arg2,arg3或两个的任意组合)
     * arg1：操作 parentPosition，arg2：childPosition。arg3：操作 count
     * eg: 1,2表示在 parentPosition=1，childPosition=2的位置的一次child操作，1,2,3
     * 表示在parentPosition=1，childPosition=2的位置的3次child操作，1,
     * 2\n3,4,5表示有两次child操作，分别在parentPosition=1，childPosition=2，parentPosition=3，childPosition=4上执行
     * 1次和5次操作
     * note:参数之前用,隔开并且中间两边都没有其他多余字符，否则视为失败输入
     */
    private static final String REGX_BASE_CHILD="(\\d+,\\d+|\\d+,\\d+,\\d+)";
    private static final String REGX_CHILD=REGX_BASE_CHILD+"|"+REGX_BASE_CHILD+"\n"+REGX_BASE_CHILD;

    public static final String REQUEST = "request";

    private static final String PARENT_TYPE = "parent";
    private static final String CHILD_TYPE = "child";
    private static final String ITEM_OPERATE_TYPE = "Item";
    private static final String ITEM_RANGE_OPERATE_TYPE = "ItemRange";

    private View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mRootView=getActivity().getLayoutInflater().inflate(R.layout.table,null);

        AlertDialog dialog=new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.title_input))
                .setView(mRootView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ArrayList<String> requestList=new ArrayList<>(6);
                        checkInput(requestList);
                        if (requestList.isEmpty()) {
                            return;
                        }
                        Fragment targetFragment = getTargetFragment();
                        Intent data = new Intent();
                        data.putStringArrayListExtra(REQUEST, requestList);
                        targetFragment.onActivityResult(MainFragment.REQUEST_RESULT,
                                Activity.RESULT_OK, data);

                    }
                }).setNegativeButton("取消",null).create();

         setCancelable(false);
        return dialog;
    }


    private void checkInput(List<String> requestList) {
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
                        List<Helper> operations=isGoodInput(type, input);
                        if (!(isGoodInput = (operations != null && !operations.isEmpty()))) {
                            requestList.clear();
                            Util.showToast(getActivity(),"input error");
                            break;
                        }
                        //同一操作是否存在两次
                        final int operationCount=operations.size();
                            for (int k = 0; k < operationCount; k++) {
                                Helper operation = operations.get(k);
                                requestList.add(String.format(getString(R.string.methodAndArgs),
                                        String.format(method, operation.operationType),
                                        operation.args));
                            }
                    }
                }
                if (!isGoodInput) break;
            }
        }

        Logger.e(TAG, "requestList="+requestList.toString());
    }


    private List<Helper> isGoodInput(String type, String input) {
        boolean isParentType=type.equals(MyDialog.PARENT_TYPE);
        List<Helper> helpers=new ArrayList<>(2);
        //同一操作是否存在两次
        final String[] operations=input.split("\n");
            for (String operation : operations) {

                boolean isGood=operation.matches(isParentType?REGX_PARENT:REGX_CHILD);
                if (!isGood) return null;

                Helper helper = new Helper();

                String[] inputSplit = operation.split(",");
                helper.operationType =
                        inputSplit.length == (isParentType ? 1 : 2) ? ITEM_OPERATE_TYPE
                                : ITEM_RANGE_OPERATE_TYPE;
                helper.args = operation;

                helpers.add(helper);
            }
        return helpers;
    }


    class Helper {
        String operationType;
        String args;
    }

}
