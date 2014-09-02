DrawerToggleHamburger
=====================
![normal] (https://github.com/mypplication/DrawerToggleHamburger/raw/dev/gifs/normal.gif)
![rounded] (https://github.com/mypplication/DrawerToggleHamburger/raw/dev/gifs/rounded.gif)
![arrow] (https://github.com/mypplication/DrawerToggleHamburger/raw/dev/gifs/arrow.gif)
![caret] (https://github.com/mypplication/DrawerToggleHamburger/raw/dev/gifs/caret.gif)
![transition] (https://github.com/mypplication/DrawerToggleHamburger/raw/dev/gifs/transition.gif)
![transition-start-end] (https://github.com/mypplication/DrawerToggleHamburger/raw/dev/gifs/transition-start-end.gif)

 
##How to use
See [ActionBarDrawerToggle](http://developer.android.com/training/implementing-navigation/nav-drawer.html). It's the same implementation. Just replace ActionbarDrawerToggle by DrawerToggleHamburger.
Only the constructor is different, instead of image ressource, you must define a size for icon.



#options
``` java
Resources r = getActivity().getResources();
	DisplayMetrics dm = r.getDisplayMetrics();
	int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, dm);
	int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, dm);
	int paddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
	int paddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
	int barHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm);

	mDrawerToggle.setClosedColor(Color.WHITE)
                         .setOpenedColor(Color.RED)
                         .setStyleShape(DrawerToggleHamburger.STYLE_CROSS)
                         .setPaddingLR(paddingLR)
                         .setPaddingTB(paddingTB)
                         .setRounded(true)
                         .setBarHeight(barHeight);
```
 

