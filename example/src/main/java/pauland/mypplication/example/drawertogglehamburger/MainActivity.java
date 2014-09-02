package pauland.mypplication.example.drawertogglehamburger;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity
{

    private NavigationDrawerFragment mNavigationDrawerFragment;


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_home);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));
    }
}
