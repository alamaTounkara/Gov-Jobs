package govjobs.govjob.ui;


import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import govjobs.govjob.R;
import govjobs.govjob.util.AccountManger;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavDrawerFragment extends android.support.v4.app.Fragment {
    private String TAG = "NavDrawerFragmen";


    public Boolean mIsAppOpenedFirstTime = true;//true if app was opened for the first time, false otherwise
    public Boolean mFromSavedInstanceState = false;//true if frag is coming back from a rotation




    @InjectView(R.id.searchImageView)
    ImageView mSearchImageView;
    @InjectView(R.id.drawerList)
    RelativeLayout mDrawerListView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    //boolean specifying if user was login or not
    private boolean mIsUserLogin = false;
    Toolbar mToolbar;


    /**
     * ActionBarDrawerToggle  class provides a handy way to tie together the functionality of
     * DrawerLayout and the framework ActionBar to implement the recommended design for navigation
     * drawers.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    //inner view that represents our drawer content
    private View mdrawerContainer;
    //account manager
    private AccountManger mAccountManger;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.inject(getActivity());//initializing all the inputs

        if ((mAccountManger.isKeepLogin()) && (mAccountManger.isUserLogin())) {
            mAccountManger.showFormattedUserDashBoard(mAccountManger.getUserID());
            mIsUserLogin = true;
        } else {

            mIsUserLogin = false;
            mAccountManger.showLoginPage();
        }
        changeTitle(mToolbar,"GOV JOBS");//making "GOV JOBS" our app title
    }

    /**
     * Used to set our Navigation drawer
     *
     * @param contentOfDrawer is the inner content of our drawer
     * @param drawerLayout is the DrawerLayout from our xml layout file
     * @param toolbar      is the toolbar used in the action
     *
     */
    public void setUpDrawer(int contentOfDrawer, DrawerLayout drawerLayout,
                            final Toolbar toolbar, AccountManger accountManger){
       mDrawerLayout =drawerLayout;
       mdrawerContainer = getActivity().findViewById(contentOfDrawer);
       mAccountManger = accountManger;
        mToolbar = toolbar;
        /**
         * ActionBarDrawerToggle  class provides a handy way to tie together the functionality of
         * DrawerLayout and the framework ActionBar to implement the recommended design for navigation
         * drawers.
         * public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout,Toolbar toolbar,
         *           @StringRes int openDrawerContentDescRes,@StringRes int closeDrawerContentDescRes)
         * Construct a new ActionBarDrawerToggle with a Toolbar.
         * <p>
         * The given {@link Activity} will be linked to the specified {@link DrawerLayout} and
         * the Toolbar's navigation icon will be set to a custom drawable. Using this constructor
         * will set Toolbar's navigation click listener to toggle the drawer when it is clicked.
         * <p>
         * This drawable shows a Hamburger icon when drawer is closed and an arrow when drawer
         * is open. It animates between these two states as the drawer opens.
         * <p>
         * String resources must be provided to describe the open/close drawer actions for
         * accessibility services.
         * <p>
         * Please use {@link #ActionBarDrawerToggle(Activity, DrawerLayout, int, int)} if you are
         * setting the Toolbar as the ActionBar of your activity.
         *
         * @param activity                  The Activity hosting the drawer.
         * @param toolbar                   The toolbar to use if you have an independent Toolbar.
         * @param drawerLayout              The DrawerLayout to link to the given Activity's ActionBar
         * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
         *                                  for accessibility
         * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
         *                                  for accessibility
         */
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar,
                                     R.string.drawable_open_text, R.string.drawable_open_text){
                //override some of the methods that ActionBarDrawerToggle implemented from the
                // android.support.v4.widget.DrawerLayout.DrawerListener listener
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    Log.d(TAG,"if (mAccountManger.isKeepLogin(): "+ mAccountManger.isKeepLogin()+" && mAccountManger.isUserLogin()"+ mAccountManger.isUserLogin());
                    if ((mAccountManger.isKeepLogin()) && (mAccountManger.isUserLogin())) {
                       changeTitle(toolbar,"GOV JOBS: Dashboard");
                        mAccountManger.showFormattedUserDashBoard(mAccountManger.getUserID());
                        mIsUserLogin = true;
                    } else {
                        changeTitle(toolbar,"GOV JOBS: Login");
                        mAccountManger.showLoginPage();
                        mIsUserLogin = false;
                    }
                    /**
                     * public void invalidateOptionsMenu()
                     * Declare that the options menu has changed, so should be recreated(redraw).
                     * The {@link #onCreateOptionsMenu(Menu)} method will be called the next
                     * time it needs to be displayed.
                     */
                    getActivity().invalidateOptionsMenu();//redraw the action bar again
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    changeTitle(toolbar, "GOV JOBS");
                    /**
                     * public void invalidateOptionsMenu()
                     * Declare that the options menu has changed, so should be recreated(redraw).
                     * The {@link #onCreateOptionsMenu(Menu)} method will be called the next
                     * time it needs to be displayed.
                     */
                    getActivity().invalidateOptionsMenu();//redraw the action bar again
                }

            /** @param slideOffset The new offset of this drawer within its range, from 0-1 */
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset < 0.6) {
                    //if we dont use this 'if' condition,when the slideOffset value is 1 which means
                    //the drawer is fully opened,the toolbar.setAlpha(1 - slideOffset) will be
                    //toolbar.setAlpha(1 - 1) which is equal toolbar.setAlpha(0). That means our
                    //toolbar will be completely hidden, And we dont want that. we want the toolbar
                    //to have little visible when the drawer is fully opened but just not that much.
                    //that is why we keep substracting the slideOffset from 1 and give as opacity of
                    //the toolbar as long as the slideOffset value <0.6.that means,when the drawer
                    //is fully opened,we want our toolbar to have an opacity of (1-0.6= 0.4).

                    /**
                     * setAlpha(float numb)
                     * <p>Sets the opacity of the view. This is a value from 0 to 1, where 0 means
                     * the view is completely transparent and 1 means the view is completely opaque.</p>
                     */
                    //we use (1-slideOffset) cuz we want the action bar to be fully visible when drawer is closed
                    toolbar.setAlpha(1 - slideOffset);
                }
            }

        };


        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);//dont forget to set the drawer listener to our DrawerLayout
        mDrawerLayout.post(new Runnable() {

            @Override
            public void run() {
                /**
                 * public void syncState()
                 * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
                 *
                 * <p>This should be called from your <code>Activity</code>'s
                 * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
                 * the DrawerLayout's instance state has been restored, and any other time when the state
                 * may have diverged in such a way that the ActionBarDrawerToggle was not notified.
                 * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
                 */
                mActionBarDrawerToggle.syncState();
            }
        });
      mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }


    /**
     * changing the title of our toolbar(actionbar)
     * @param toolbar whose titlre has to be changed
     * @param newTitle is the new title to be put in place
     */
    private void changeTitle(Toolbar toolbar,String newTitle) {
        //getting our actionbar, and set its title to 's'
        toolbar.setTitle(newTitle);//make sure you use the support library getSupportActionBar()
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*
        *this wll make sure your activity get back the state of the drawer the way it was before
        * orientation changed.
        */
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

}
