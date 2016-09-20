# ExpandableRecyclerView
自定义 RecyclerView.Adapter 实现类似 ExpandableListView 特性

参考自 [Android 编程权威指南 Sample](https://www.bignerdranch.com/blog/expand-a-recyclerview-in-four-steps/?utm_source=Android+Weekly&utm_campaign=8f0cc3ff1f-Android_Weekly_165&utm_medium=email&utm_term=0_4eb677ad19-8f0cc3ff1f-337834121)

1. support `getParentType` and `getChildType`,支持创建不同的 Parent 和 Child
2. support extra `notifyParentItemMoved`、`notifyChildItemMoved`、`notifyParentItemRangeChanged`、`notifyParentItemRangedRemoved`、`notifyChildItemRangeInserted`、`notifyChildItemRangeRemoved`、`notifyChildItemRangeChanged` api
3. encapsulate `ViewHolder`

![][https://github.com/HuaJianJiang/ExpandableRecyclerView/raw/master/device-2016-09-20-161148.png]
