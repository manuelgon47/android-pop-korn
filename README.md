# Android Pop Corn Menu

A popcorn menu for android
<br /><br />
<img src="https://github.com/BD-ES/android-pop-korn/blob/master/screenshots/pop-corn-menu.gif" />

<h3>Installation</h3>
Requeriments:
- minSdkVersion 21
- App style with NoActionBar

Steps to install the module:
<br />
- In terminal go to: $YourAndroidWorkspace/AppName/yourappmodule/libs
- Make git clone from this repo inside libs folder
- Import the android library<br/>
<img src="https://github.com/BD-ES/android-pop-korn/blob/master/screenshots/import_module.png" />
- In your app gradle.build:
```
dependencies {
    compile project(":popcornmenu")
}
```
<br />

<h3>How to use</h3>
In your xml activity add:
```
<com.mgonzalez.bd.popcornmenu.PopCornMenu
    android:id="@+id/popmenu"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"
    android:layout_alignParentRight="true" />
```

Just with that you will get the pop corn menu working without buttons.

<h3>How to custom</h3>
<h5>Add menu entries</h5>
```
PopCornMenu menu = (PopCornMenu)findViewById(R.id.popmenu);
if (menu != null) {
    int[] buttons = new int[]{R.drawable.drawableButton1, R.drawable.drawableButton2, R.drawable.drawableButton3, R.drawable.drawableButton4};
    menu.setup(buttons);
}
```

<br />
<h5>Set buttons listener</h5>
You receive an index that correspond with the position in which you put the button.
```
menu.setListener(new PopCornMenuListener() {
    @Override
    public void onPopCornMenuClicked(int index) {
        switch (index) {
            case 0:
                Toast.makeText(MainActivity.this, "Tap on drawableButton1", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "Tap on drawableButton2", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MainActivity.this, "Tap on drawableButton3", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(MainActivity.this, "Tap on drawableButton4", Toast.LENGTH_SHORT).show();
                break;
        }
    }
});
```

<br />
<h5>Custom display animation duration</h5>
You can change the duration in which the pop corn menu displays
```
menu.setAnimationDuration(250L);
```

<br />
<h5>Custom menu button background</h5>
You have 3 options:
<br />
- Using an hex value (0x AA: Alpha; HHHHHH: Hex color)
```
menu.changeMenuColor(0xffE342B3);
```
- Using the class android.graphics.Color
```
menu.changeMenuColor(Color.RED);
```
- Using your values from your xml colours file
```
menu.changeMenuColor(getResources().getColor(R.color.your_colour_resource));
```

<br />
# License
```
Copyright (C) 2015 Manuel González Villegas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
