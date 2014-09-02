package pauland.mypplication.example.drawertogglehamburger;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import pauland.mypplication.lib.DrawerToggleHamburger;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
{

    private DrawerToggleHamburger mDrawerToggle;
    private DrawerLayout          mDrawerLayout;

    public NavigationDrawerFragment ()
    {
    }

    public void setUp (DrawerLayout drawerLayout)
    {
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Resources r = getActivity().getResources();
        DisplayMetrics dm = r.getDisplayMetrics();
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, dm);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, dm);
        int paddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
        int paddingTB = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, dm);
        int barHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm);


        mDrawerToggle = new DrawerToggleHamburger(getActivity(),                    /* host Activity */
            mDrawerLayout,                    /* DrawerLayout object */
            width, height, R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
            R.string.navigation_drawer_close  /* "close drawer" description for accessibility */);

        mDrawerToggle.setClosedColor(Color.WHITE)
                     .setOpenedColor(Color.RED)
                     .setStyleShape(DrawerToggleHamburger.STYLE_CROSS)
                     .setPaddingLR(paddingLR)
                     .setPaddingTB(paddingTB)
                     .setRounded(true)
                     .setBarHeight(barHeight);

        mDrawerLayout.post(new Runnable()
        {
            @Override
            public void run ()
            {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.f_drawer, container, false);

        CheckBox cb = (CheckBox) v.findViewById(R.id.rounded);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged (final CompoundButton buttonView, final boolean isChecked)
            {
                mDrawerToggle.setRounded(isChecked);
            }
        });

        RadioGroup rg = (RadioGroup) v.findViewById(R.id.style_group);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged (final RadioGroup group, final int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.style_arrow:
                        mDrawerToggle.setStyleShape(DrawerToggleHamburger.STYLE_ARROW);
                        break;
                    case R.id.style_caret:
                        mDrawerToggle.setStyleShape(DrawerToggleHamburger.STYLE_CARET);
                        break;

                    case R.id.style_cross:
                        mDrawerToggle.setStyleShape(DrawerToggleHamburger.STYLE_CROSS);
                        break;

                }
            }
        });

        EditText openedColor = (EditText) v.findViewById(R.id.opened_color);
        openedColor.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged (final CharSequence s, final int start, final int count, final int after)
            {

            }

            @Override
            public void onTextChanged (final CharSequence s, final int start, final int before, final int count)
            {

            }

            @Override
            public void afterTextChanged (final Editable s)
            {
                try
                {
                    int color = Color.parseColor(s.toString());
                    mDrawerToggle.setOpenedColor(color);
                }
                catch (Exception ex) {}

            }
        });

        EditText closedColor = (EditText) v.findViewById(R.id.closed_color);
        closedColor.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged (final CharSequence s, final int start, final int count, final int after)
            {

            }

            @Override
            public void onTextChanged (final CharSequence s, final int start, final int before, final int count)
            {

            }

            @Override
            public void afterTextChanged (final Editable s)
            {
                try
                {
                    int color = Color.parseColor(s.toString());
                    mDrawerToggle.setClosedColor(color);
                }
                catch (Exception ex) {}

            }
        });


        return v;
    }

    @Override
    public void onActivityCreated (final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar ()
    {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
