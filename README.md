# ExpandableRecyclerView
Custom RecyclerView.Adapter that implement features like ExpandableListView

参考自 [Android 编程权威指南 Sample](https://www.bignerdranch.com/blog/expand-a-recyclerview-in-four-steps/?utm_source=Android+Weekly&utm_campaign=8f0cc3ff1f-Android_Weekly_165&utm_medium=email&utm_term=0_4eb677ad19-8f0cc3ff1f-337834121)

## Features

1. support `getParentType` and `getChildType`,support create different Parent or/and Child ItemView
2. support extra `notifyParentItemMoved`、`notifyChildItemMoved`、`notifyParentItemRangeChanged`、`notifyParentItemRangedRemoved`、`notifyChildItemRangeInserted`、`notifyChildItemRangeRemoved`、`notifyChildItemRangeChanged` api
3. encapsulate `ViewHolder`
4. now support set specified ParentItem which can be expandable dynamically
5. now support set ExpandableAdapter's `setExpandCollapseMode`! eg: MODE_SINGLE_EXPAND,in this mode,when expand another parentItem will auto collapse last expanded parentItem.

## Screenshots

![Demo](/screenshots/screenshot_3.png)
![Demo](/screenshots/screenshot_2.png)

## Usage
* Add a dependency to your `build.gradle`:

```grooey
dependencies {
    compile 'com.github.huajianjiang:expandablerecyclerview:1.0.0-RC1'
}
```

* or to your `pom.xml` if you are using Maven instead of Gradle:

```XML
<dependency>
  <groupId>com.github.huajianjiang</groupId>
  <artifactId>expandablerecyclerview</artifactId>
  <version>1.0.0-RC1</version>
  <type>pom</type>
</dependency>
```

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
