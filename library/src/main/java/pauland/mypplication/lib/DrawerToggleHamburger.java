/*
 * Copyright (C) 2013 The Android Open Source Project
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
 */


package pauland.mypplication.lib;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Method;

/**
 * Created by Pauland on 29/07/2014.
 * <p/>
 * Custom implementation based on {@link android.support.v4.app.ActionBarDrawerToggle}
 * <p/>
 * This class provides a handy way to tie together the functionality of
 * {@link DrawerLayout} and the framework <code>ActionBar</code> to implement the recommended
 * design for navigation drawers.
 * <p/>
 * <p>To use <code>ActionBarDrawerToggle</code>, create one in your Activity and call through
 * to the following methods corresponding to your Activity callbacks:</p>
 * <p/>
 * <ul>
 * <li>{@link Activity#onConfigurationChanged(android.content.res.Configuration) onConfigurationChanged}</li>
 * <li>{@link Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected}</li>
 * </ul>
 * <p/>
 * <p>Call {@link #syncState()} from your <code>Activity</code>'s
 * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} to synchronize the indicator
 * with the state of the linked DrawerLayout after <code>onRestoreInstanceState</code>
 * has occurred.</p>
 * <p/>
 * <p><code>ActionBarDrawerToggle</code> can be used directly as a
 * {@link DrawerLayout.DrawerListener}, or if you are already providing your own listener,
 * call through to each of the listener methods from your own.</p>
 */
public class DrawerToggleHamburger implements DrawerLayout.DrawerListener
{

    /**
     * When the Drawer opens, the icon will be cross-shaped (Default)
     */
    public static final int STYLE_CROSS = 0;

    /**
     * When the Drawer opens, the icon will be arrow-shaped
     */
    public static final int STYLE_ARROW = 1;

    /**
     * When the Drawer opens, the icon will be caret-shaped
     */
    public static final int STYLE_CARET = 2;

    private static final ActionBarDrawerToggleImpl IMPL;

    static
    {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 18)
        {
            IMPL = new ActionBarDrawerToggleImplJellybeanMR2();
        }
        else if (version >= 11)
        {
            IMPL = new ActionBarDrawerToggleImplHC();
        }
        else
        {
            IMPL = new ActionBarDrawerToggleImplBase();
        }
    }

    // android.R.id.home as defined by public API in v11
    private static final int ID_HOME = 0x0102002c;
    private final Activity     mActivity;
    private final Delegate     mActivityImpl;
    private final DrawerLayout mDrawerLayout;
    private final int          mOpenDrawerContentDescRes;
    private final int          mCloseDrawerContentDescRes;
    private boolean mDrawerIndicatorEnabled = true;
    private Drawable          mThemeImage;
    private TransformDrawable mSlider;
    private Object            mSetIndicatorInfo;

    /**
     * Construct a new ActionBarDrawerToggle.
     * <p/>
     * <p>The given {@link android.app.Activity} will be linked to the specified {@link android.support.v4.widget.DrawerLayout}.
     * The provided drawer indicator drawable will animate slightly off-screen as the drawer
     * is opened, indicating that in the open state the drawer will move off-screen when pressed
     * and in the closed state the drawer will move on-screen when pressed.</p>
     * <p/>
     * <p>String resources must be provided to describe the open/close drawer actions for
     * accessibility services.</p>
     *
     * @param activity                  The Activity hosting the drawer
     * @param drawerLayout              The DrawerLayout to link to the given Activity's ActionBar
     * @param width                     The width of the icon entire (including margins). In pixel.
     * @param height                    The height of the icon entire (including margins). In pixel.
     * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
     *                                  for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     *                                  for accessibility
     */
    public DrawerToggleHamburger (Activity activity, DrawerLayout drawerLayout, int width, int height, int openDrawerContentDescRes, int closeDrawerContentDescRes)
    {
        mActivity = activity;

        // Allow the Activity to provide an impl
        if (activity instanceof DelegateProvider)
        {
            mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        }
        else
        {
            mActivityImpl = null;
        }

        mDrawerLayout = drawerLayout;
        mOpenDrawerContentDescRes = openDrawerContentDescRes;
        mCloseDrawerContentDescRes = closeDrawerContentDescRes;

        mThemeImage = getThemeUpIndicator();
        BitmapDrawable bd = new BitmapDrawable(activity.getResources(), Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8));
        mSlider = new TransformDrawable(mActivity.getResources(), bd);

    }

    /**
     * set padding left and padding right inside icon
     *
     * @param paddingLR paddings in pixel.
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     */
    public DrawerToggleHamburger setPaddingLR (int paddingLR)
    {
        mSlider.setPaddingLR(paddingLR);
        return this;
    }

    /**
     * Set height for a bar in icon
     *
     * @param barHeight bar height in pixel.
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     */
    public DrawerToggleHamburger setBarHeight (int barHeight)
    {
        mSlider.setBarHeight(barHeight);
        return this;
    }

    /**
     * Set padding top and padding bottom inside icon
     *
     * @param paddingTB paddings in pixel
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     */
    public DrawerToggleHamburger setPaddingTB (int paddingTB)
    {
        mSlider.setPaddingTB(paddingTB);
        return this;
    }

    /**
     * Set icon opened color
     *
     * @param color A color (NOT a resource) use <code>getResources().getColor(resId)</code> for resource</code>
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     */
    public DrawerToggleHamburger setOpenedColor (int color)
    {
        mSlider.setOpenedColor(color);
        return this;
    }

    /**
     * Set icon closed color
     *
     * @param color A color (NOT a resource) use <code>getResources().getColor(resId)</code> for resource</code>
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     */
    public DrawerToggleHamburger setClosedColor (int color)
    {
        mSlider.setClosedColor(color);
        return this;
    }

    /**
     * set if the bar ends are rounded
     *
     * @param rounded true if rounded, else false (default)
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     */
    public DrawerToggleHamburger setRounded (boolean rounded)
    {
        mSlider.setRounded(rounded);
        return this;
    }

    /**
     * Set the style shape for opened drawer
     *
     * @param style value for the new style (default {@link #STYLE_CROSS})
     * @return {@link pauland.mypplication.lib.DrawerToggleHamburger}
     * @see #STYLE_ARROW
     * @see #STYLE_CARET
     * @see #STYLE_CROSS
     */
    public DrawerToggleHamburger setStyleShape (int style)
    {
        if (style != STYLE_ARROW && style != STYLE_CARET && style != STYLE_CROSS)
            mSlider.setStyleShape(STYLE_CROSS);
        else
            mSlider.setStyleShape(style);

        return this;
    }

    /**
     * @return true if the enhanced drawer indicator is enabled, false otherwise
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public boolean isDrawerIndicatorEnabled ()
    {
        return mDrawerIndicatorEnabled;
    }

    /**
     * Enable or disable the drawer indicator. The indicator defaults to enabled.
     * <p/>
     * <p>When the indicator is disabled, the <code>ActionBar</code> will revert to displaying
     * the home-as-up indicator provided by the <code>Activity</code>'s theme in the
     * <code>android.R.attr.homeAsUpIndicator</code> attribute instead of the animated
     * drawer glyph.</p>
     *
     * @param enable true to enable, false to disable
     */
    public void setDrawerIndicatorEnabled (boolean enable)
    {
        if (enable != mDrawerIndicatorEnabled)
        {
            if (enable)
            {
                setActionBarUpIndicator(mSlider, mDrawerLayout.isDrawerOpen(GravityCompat.START) ? mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
            }
            else
            {
                setActionBarUpIndicator(mThemeImage, 0);
            }
            mDrawerIndicatorEnabled = enable;
        }
    }

    /**
     * This method should always be called by your <code>Activity</code>'s
     * {@link android.app.Activity#onConfigurationChanged(android.content.res.Configuration) onConfigurationChanged}
     * method.
     *
     * @param newConfig The new configuration
     */
    public void onConfigurationChanged (Configuration newConfig)
    {
        // Reload drawables that can change with configuration
        mThemeImage = getThemeUpIndicator();
        syncState();
    }

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
     * <p/>
     * <p>This should be called from your <code>Activity</code>'s
     * {@link android.app.Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
     * the DrawerLayout's instance state has been restored, and any other time when the state
     * may have diverged in such a way that the ActionBarDrawerToggle was not notified.
     * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
     */
    public void syncState ()
    {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mSlider.setPosition(1);
        }
        else
        {
            mSlider.setPosition(0);
        }

        if (mDrawerIndicatorEnabled)
        {
            setActionBarUpIndicator(mSlider, mDrawerLayout.isDrawerOpen(GravityCompat.START) ? mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
        }
    }

    /**
     * This method should be called by your <code>Activity</code>'s
     * {@link android.app.Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected} method.
     * If it returns true, your <code>onOptionsItemSelected</code> method should return true and
     * skip further processing.
     *
     * @param item the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not occur
     */
    public boolean onOptionsItemSelected (MenuItem item)
    {
        if (item != null && item.getItemId() == ID_HOME && mDrawerIndicatorEnabled)
        {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START))
            {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    /**
     * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView  The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    @Override
    public void onDrawerSlide (View drawerView, float slideOffset)
    {
        float glyphOffset = mSlider.getPosition();
        if (slideOffset > 0.5f)
        {
            glyphOffset = Math.max(glyphOffset, Math.max(0.f, slideOffset - 0.5f) * 2);
        }
        else
        {
            glyphOffset = Math.min(glyphOffset, slideOffset * 2);
        }
        mSlider.setPosition(glyphOffset);
    }

    /**
     * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now open
     */
    @Override
    public void onDrawerOpened (View drawerView)
    {
        mSlider.setPosition(1);
        if (mDrawerIndicatorEnabled)
        {
            setActionBarDescription(mCloseDrawerContentDescRes);
        }
    }

    /**
     * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param drawerView Drawer view that is now closed
     */
    @Override
    public void onDrawerClosed (View drawerView)
    {
        mSlider.setPosition(0);
        if (mDrawerIndicatorEnabled)
        {
            setActionBarDescription(mOpenDrawerContentDescRes);
        }
    }

    /**
     * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
     * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
     * through to this method from your own listener object.
     *
     * @param newState The new drawer motion state
     */
    @Override
    public void onDrawerStateChanged (int newState)
    {
    }

    Drawable getThemeUpIndicator ()
    {
        if (mActivityImpl != null)
        {
            return mActivityImpl.getThemeUpIndicator();
        }
        return IMPL.getThemeUpIndicator(mActivity);
    }

    void setActionBarUpIndicator (Drawable upDrawable, int contentDescRes)
    {
        if (mActivityImpl != null)
        {
            mActivityImpl.setActionBarUpIndicator(upDrawable, contentDescRes);
            return;
        }
        mSetIndicatorInfo = IMPL.setActionBarUpIndicator(mSetIndicatorInfo, mActivity, upDrawable, contentDescRes);
    }

    void setActionBarDescription (int contentDescRes)
    {
        if (mActivityImpl != null)
        {
            mActivityImpl.setActionBarDescription(contentDescRes);
            return;
        }
        mSetIndicatorInfo = IMPL.setActionBarDescription(mSetIndicatorInfo, mActivity, contentDescRes);
    }


    /**
     * Allows an implementing Activity to return an {@link android.support.v4.app.ActionBarDrawerToggle.Delegate} to use
     * with ActionBarDrawerToggle.
     */
    public interface DelegateProvider
    {

        /**
         * @return Delegate to use for ActionBarDrawableToggles, or null if the Activity
         * does not wish to override the default behavior.
         */
        Delegate getDrawerToggleDelegate ();
    }

    public interface Delegate
    {
        /**
         * @return Up indicator drawable as defined in the Activity's theme, or null if one is not
         * defined.
         */
        Drawable getThemeUpIndicator ();

        /**
         * Set the Action Bar's up indicator drawable and content description.
         *
         * @param upDrawable     - Drawable to set as up indicator
         * @param contentDescRes - Content description to set
         */
        void setActionBarUpIndicator (Drawable upDrawable, int contentDescRes);

        /**
         * Set the Action Bar's up indicator content description.
         *
         * @param contentDescRes - Content description to set
         */
        void setActionBarDescription (int contentDescRes);
    }

    private interface ActionBarDrawerToggleImpl
    {
        Drawable getThemeUpIndicator (Activity activity);

        Object setActionBarUpIndicator (Object info, Activity activity, Drawable themeImage, int contentDescRes);

        Object setActionBarDescription (Object info, Activity activity, int contentDescRes);

    }

    private static class ActionBarDrawerToggleImplBase implements ActionBarDrawerToggleImpl
    {
        @Override
        public Drawable getThemeUpIndicator (Activity activity)
        {
            return null;
        }

        @Override
        public Object setActionBarUpIndicator (Object info, Activity activity, Drawable themeImage, int contentDescRes)
        {
            // No action bar to set.
            return info;
        }

        @Override
        public Object setActionBarDescription (Object info, Activity activity, int contentDescRes)
        {
            // No action bar to set
            return info;
        }


    }

    private static class ActionBarDrawerToggleImplHC implements ActionBarDrawerToggleImpl
    {


        @Override
        public Drawable getThemeUpIndicator (Activity activity)
        {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(activity);
        }

        @Override
        public Object setActionBarUpIndicator (Object info, Activity activity, Drawable themeImage, int contentDescRes)
        {
            return ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(info, activity, themeImage, contentDescRes);
        }

        @Override
        public Object setActionBarDescription (Object info, Activity activity, int contentDescRes)
        {
            return ActionBarDrawerToggleHoneycomb.setActionBarDescription(info, activity, contentDescRes);
        }


    }

    private static class ActionBarDrawerToggleImplJellybeanMR2 implements ActionBarDrawerToggleImpl
    {


        @Override
        public Drawable getThemeUpIndicator (Activity activity)
        {
            return ActionBarDrawerToggleJellybeanMR2.getThemeUpIndicator(activity);
        }

        @Override
        public Object setActionBarUpIndicator (Object info, Activity activity, Drawable themeImage, int contentDescRes)
        {
            return ActionBarDrawerToggleJellybeanMR2.setActionBarUpIndicator(info, activity, themeImage, contentDescRes);
        }

        @Override
        public Object setActionBarDescription (Object info, Activity activity, int contentDescRes)
        {
            return ActionBarDrawerToggleJellybeanMR2.setActionBarDescription(info, activity, contentDescRes);
        }


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static class ActionBarDrawerToggleHoneycomb
    {
        private static final String TAG         = "ActionBarDrawerToggleHoneycomb";
        private static final int[]  THEME_ATTRS = new int[]{android.R.attr.homeAsUpIndicator};


        public static Object setActionBarUpIndicator (Object info, Activity activity, Drawable drawable, int contentDescRes)
        {
            if (info == null)
            {
                info = new SetIndicatorInfo(activity);
            }
            final SetIndicatorInfo sii = (SetIndicatorInfo) info;
            if (sii.setHomeAsUpIndicator != null)
            {
                try
                {
                    final ActionBar actionBar = activity.getActionBar();
                    sii.setHomeAsUpIndicator.invoke(actionBar, drawable);
                    sii.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Couldn't set home-as-up indicator via JB-MR2 API", e);
                }
            }
            else if (sii.upIndicatorView != null)
            {
                sii.upIndicatorView.setImageDrawable(drawable);
            }
            else
            {
                Log.w(TAG, "Couldn't set home-as-up indicator");
            }
            return info;
        }

        public static Object setActionBarDescription (Object info, Activity activity, int contentDescRes)
        {
            if (info == null)
            {
                info = new SetIndicatorInfo(activity);
            }
            final SetIndicatorInfo sii = (SetIndicatorInfo) info;
            if (sii.setHomeAsUpIndicator != null)
            {
                try
                {
                    final ActionBar actionBar = activity.getActionBar();
                    sii.setHomeActionContentDescription.invoke(actionBar, contentDescRes);
                    if (Build.VERSION.SDK_INT <= 19)
                    {
                        // For API 19 and earlier, we need to manually force the
                        // action bar to generate a new content description.
                        actionBar.setSubtitle(actionBar.getSubtitle());
                    }
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Couldn't set content description via JB-MR2 API", e);
                }
            }
            return info;
        }

        public static Drawable getThemeUpIndicator (Activity activity)
        {
            final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
            final Drawable result = a.getDrawable(0);
            a.recycle();
            return result;
        }


        private static class SetIndicatorInfo
        {
            public Method    setHomeAsUpIndicator;
            public Method    setHomeActionContentDescription;
            public ImageView upIndicatorView;

            SetIndicatorInfo (Activity activity)
            {
                try
                {
                    setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator", Drawable.class);
                    setHomeActionContentDescription = ActionBar.class.getDeclaredMethod("setHomeActionContentDescription", Integer.TYPE);
                    // If we got the method we won't need the stuff below.
                    return;
                }
                catch (NoSuchMethodException e)
                {
                    // Oh well. We'll use the other mechanism below instead.
                }
                final View home = activity.findViewById(android.R.id.home);
                if (home == null)
                {
                    // Action bar doesn't have a known configuration, an OEM messed with things.
                    return;
                }
                final ViewGroup parent = (ViewGroup) home.getParent();
                final int childCount = parent.getChildCount();
                if (childCount != 2)
                {
                    // No idea which one will be the right one, an OEM messed with things.
                    return;
                }
                final View first = parent.getChildAt(0);
                final View second = parent.getChildAt(1);
                final View up = first.getId() == android.R.id.home ? second : first;
                if (up instanceof ImageView)
                {
                    // Jackpot! (Probably...)
                    upIndicatorView = (ImageView) up;
                }
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static class ActionBarDrawerToggleJellybeanMR2
    {
        private static final String TAG         = "ActionBarDrawerToggleImplJellybeanMR2";
        private static final int[]  THEME_ATTRS = new int[]{android.R.attr.homeAsUpIndicator};

        public static Object setActionBarUpIndicator (Object info, Activity activity, Drawable drawable, int contentDescRes)
        {
            final ActionBar actionBar = activity.getActionBar();
            if (actionBar != null)
            {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
            return info;
        }


        public static Object setActionBarDescription (Object info, Activity activity, int contentDescRes)
        {
            final ActionBar actionBar = activity.getActionBar();
            if (actionBar != null)
            {
                actionBar.setHomeActionContentDescription(contentDescRes);
            }
            return info;
        }

        public static Drawable getThemeUpIndicator (Activity activity)
        {
            final TypedArray a = activity.obtainStyledAttributes(THEME_ATTRS);
            final Drawable result = a.getDrawable(0);
            a.recycle();
            return result;
        }
    }

    private class TransformDrawable extends InsetDrawable implements Drawable.Callback
    {
        private final Rect  mTmpRect     = new Rect();
        private final RectF mTmpRectDraw = new RectF();
        private float mPosition;
        private int   mColorFrom, mColorTo;
        private Paint mPaintIcon, mPaintCenterBar;
        private boolean mRounded;
        private int     mStyle, mPaddingLR, mPaddingTB, mBarHeight;
        private float top, left, right, bottom;

        private TransformDrawable (Resources resource, Drawable d)
        {
            super(d, 0);
            mPaintIcon = new Paint();
            mPaintCenterBar = new Paint();
            mColorFrom = Color.WHITE;
            mColorTo = mColorFrom;
            mRounded = false;
            mPaddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, resource.getDisplayMetrics());
            mPaddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, resource.getDisplayMetrics());
            mBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, resource.getDisplayMetrics());
            mStyle = STYLE_CROSS;

            mPaintIcon.setAntiAlias(true);
            mPaintIcon.setDither(true);
            mPaintCenterBar.setAntiAlias(true);
            mPaintCenterBar.setDither(true);


        }

        public float getPosition ()
        {
            return mPosition;
        }

        /**
         * Sets the current position along the offset.
         *
         * @param position a value between 0 and 1
         */
        public void setPosition (float position)
        {
            mPosition = position;
            invalidateSelf();
        }

        public void setBarHeight (int barHeight)
        {
            mBarHeight = barHeight;
            invalidateSelf();
        }

        public void setClosedColor (int color)
        {
            mColorFrom = color;
            invalidateSelf();
        }


        public void setOpenedColor (int color)
        {
            mColorTo = color;
            invalidateSelf();
        }

        public void setRounded (boolean rounded)
        {
            mRounded = rounded;
            invalidateSelf();
        }

        public void setPaddingLR (int paddingLR)
        {
            mPaddingLR = paddingLR;
            invalidateSelf();
        }

        public void setPaddingTB (int paddingTB)
        {
            mPaddingTB = paddingTB;
            invalidateSelf();
        }

        public void setStyleShape (int style)
        {
            mStyle = style;
            invalidateSelf();
        }

        @Override
        public void draw (Canvas canvas)
        {
            switch (mStyle)
            {
                case STYLE_CROSS:
                    drawCrossStyle(canvas);
                    break;
                case STYLE_CARET:
                    drawCaretStyle(canvas);
                    break;
                case STYLE_ARROW:
                    drawArrowStyle(canvas);
                    break;
            }


            super.draw(canvas);
            canvas.restore();
        }

        private void drawCrossStyle (Canvas canvas)
        {
            copyBounds(mTmpRect);
            canvas.save();

            mPaintIcon.setColor(transitionColor(mPosition, mColorFrom, mColorTo));
            mPaintCenterBar.setColor(transitionColor(mPosition, mColorFrom, mColorTo));

            final int width = mTmpRect.width();
            final int height = mTmpRect.height();

            //space between bars
            final int spaceBwBar = (height - mPaddingTB * 2 - mBarHeight * 3) / 2;
            int currentY = mPaddingTB;

            canvas.save();
            canvas.translate(0, mPosition * ((height / 2) - (currentY + mBarHeight / 2)));
            canvas.rotate(45 * mPosition, width / 2, (currentY + mBarHeight / 2));
            mTmpRectDraw.set(mPaddingLR, currentY, width - mPaddingLR, currentY + mBarHeight);

            currentY += mBarHeight + spaceBwBar;

            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintIcon);
            else
                canvas.drawRect(mTmpRectDraw, mPaintIcon);

            canvas.restore();


            mPaintCenterBar.setAlpha(255 - (int) (255 * mPosition));
            mTmpRectDraw.set(mPaddingLR, currentY, width - mPaddingLR, currentY + mBarHeight);

            currentY += mBarHeight + spaceBwBar;

            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintCenterBar);
            else
                canvas.drawRect(mTmpRectDraw, mPaintCenterBar);


            canvas.save();
            canvas.translate(0, mPosition * ((height / 2) - (currentY + mBarHeight / 2)));
            canvas.rotate(-45 * mPosition, width / 2, (currentY + mBarHeight / 2));


            mTmpRectDraw.set(mPaddingLR, currentY, width - mPaddingLR, currentY + mBarHeight);
            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintIcon);
            else
                canvas.drawRect(mTmpRectDraw, mPaintIcon);

            canvas.restore();
        }


        private void drawArrowStyle (Canvas canvas)
        {
            copyBounds(mTmpRect);
            canvas.save();

            mPaintIcon.setColor(transitionColor(mPosition, mColorFrom, mColorTo));
            mPaintCenterBar.setColor(transitionColor(mPosition, mColorFrom, mColorTo));

            final int width = mTmpRect.width();
            final int height = mTmpRect.height();
            final int barWidth = width - mPaddingLR * 2;


            //space between bars
            final int spaceBwBar = (height - mPaddingTB * 2 - mBarHeight * 3) / 2;
            int currentY = mPaddingTB;

            left = mPaddingLR;
            top = currentY + ((mBarHeight + spaceBwBar) * mPosition);
            right = width - mPaddingLR - ((barWidth / 2) * mPosition);
            bottom = top + mBarHeight;

            //TOP BAR
            canvas.save();
            canvas.rotate(-35 * mPosition, left, top);
            mTmpRectDraw.set(left, top, right, bottom);
            currentY += mBarHeight + spaceBwBar;
            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintIcon);
            else
                canvas.drawRect(mTmpRectDraw, mPaintIcon);
            canvas.restore();


            //MIDDLE BAR
            mTmpRectDraw.set(mPaddingLR, currentY, width - mPaddingLR, currentY + mBarHeight);
            currentY += mBarHeight + spaceBwBar;

            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintCenterBar);
            else
                canvas.drawRect(mTmpRectDraw, mPaintCenterBar);


            //BOTTOM BAR
            top = currentY - ((spaceBwBar + mBarHeight) * mPosition);
            bottom = top + mBarHeight;

            canvas.save();
            canvas.rotate(35 * mPosition, left, bottom);
            mTmpRectDraw.set(left, top, right, bottom);

            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintIcon);
            else
                canvas.drawRect(mTmpRectDraw, mPaintIcon);

            canvas.restore();
        }

        private void drawCaretStyle (Canvas canvas)
        {
            copyBounds(mTmpRect);
            canvas.save();

            mPaintIcon.setColor(transitionColor(mPosition, mColorFrom, mColorTo));
            mPaintCenterBar.setColor(transitionColor(mPosition, mColorFrom, mColorTo));


            final int width = mTmpRect.width();
            final int height = mTmpRect.height();
            final int barWidth = width - mPaddingLR * 2;


            //space between bars
            final int spaceBwBar = (height - mPaddingTB * 2 - mBarHeight * 3) / 2;
            int currentY = mPaddingTB;

            left = mPaddingLR;
            top = currentY + ((mBarHeight + spaceBwBar) * mPosition);
            right = width - mPaddingLR - ((barWidth / 2) * mPosition);
            bottom = top + mBarHeight;

            canvas.save();
            canvas.rotate(-40 * mPosition, left, top);
            mTmpRectDraw.set(left, top, right, bottom);
            currentY += mBarHeight + spaceBwBar;
            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintIcon);
            else
                canvas.drawRect(mTmpRectDraw, mPaintIcon);
            canvas.restore();


            top = currentY;
            bottom = top + mBarHeight;
            mPaintCenterBar.setAlpha(255 - (int) (255 * mPosition));
            mTmpRectDraw.set(left, top, right, bottom);
            currentY += mBarHeight + spaceBwBar;
            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintCenterBar);
            else
                canvas.drawRect(mTmpRectDraw, mPaintCenterBar);


            //BOTTOM BAR
            top = currentY - ((spaceBwBar + mBarHeight + mBarHeight / 2) * mPosition);
            bottom = top + mBarHeight;

            canvas.save();
            canvas.rotate(40 * mPosition, left, bottom);
            mTmpRectDraw.set(left, top, right, bottom);

            if (mRounded)
                canvas.drawRoundRect(mTmpRectDraw, mBarHeight / 2, mBarHeight / 2, mPaintIcon);
            else
                canvas.drawRect(mTmpRectDraw, mPaintIcon);

            canvas.restore();

        }

        private int transitionColor (float value, int from, int to)
        {
            int alpha = (int) Math.abs((value * Color.alpha(to)) + ((1 - value) * Color.alpha(from)));
            int red = (int) Math.abs((value * Color.red(to)) + ((1 - value) * Color.red(from)));
            int green = (int) Math.abs((value * Color.green(to)) + ((1 - value) * Color.green(from)));
            int blue = (int) Math.abs((value * Color.blue(to)) + ((1 - value) * Color.blue(from)));
            return Color.argb(alpha, red, green, blue);
        }
    }
}
