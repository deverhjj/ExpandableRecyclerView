/*
 * Copyright (c) 2015 Huajian Jiang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.huajianjiang.expandablerecyclerview.widget;

import android.view.View;


/**
 * <p>
 * 子列表项 ChildViewHolder ，用于实现子列表项视图的创建
 * 客户端 ChildViewHolder 应该实现该类实现可扩展的 {@code RecyclerView}
 * </p>
 * <p>
 * Created by jhj_Plus on 2015/12/23.
 */
public class ChildViewHolder<C> extends BaseViewHolder {
    private static final String TAG = "ChildViewHolder";

    private int parentPosition;

    private int childPosition;

    private C child;

    public ChildViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 返回该 parent 的位置，不一定是同步过的,例如在 parent move 之后该值并没有同步更新，因此需要重新查询
     *
     * @return parent 的折叠位置
     */
    public int getParentPosition() {
        return parentPosition;
    }

    void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    /**
     * 返回该 child 的位置，不一定是同步过的,例如在 child move 之后该值并没有同步更新，因此需要重新查询
     *
     * @return child 的折叠位置
     */
    public int getChildPosition() {
        return childPosition;
    }

    void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public C getChild() {
        return child;
    }

    void setChild(C child) {
        this.child = child;
    }
}
