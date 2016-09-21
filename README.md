# ExpandableRecyclerView
自定义 RecyclerView.Adapter 实现类似 ExpandableListView 特性

参考自 [Android 编程权威指南 Sample](https://www.bignerdranch.com/blog/expand-a-recyclerview-in-four-steps/?utm_source=Android+Weekly&utm_campaign=8f0cc3ff1f-Android_Weekly_165&utm_medium=email&utm_term=0_4eb677ad19-8f0cc3ff1f-337834121)

1. support `getParentType` and `getChildType`,支持创建不同的 Parent 和 Child
2. support extra `notifyParentItemMoved`、`notifyChildItemMoved`、`notifyParentItemRangeChanged`、`notifyParentItemRangedRemoved`、`notifyChildItemRangeInserted`、`notifyChildItemRangeRemoved`、`notifyChildItemRangeChanged` api
3. encapsulate `ViewHolder`
4. now support set specified ParentItem which can be expandable dynamically

![Demo](/screenshots/screenshot_1.png)
![Demo](/screenshots/screenshot_2.png)

License
=======

    Copyright (C) 2015 HuaJian Jiang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
