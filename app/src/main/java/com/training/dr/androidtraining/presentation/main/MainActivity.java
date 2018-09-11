package com.training.dr.androidtraining.presentation.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.training.dr.androidtraining.BuildConfig;
import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.api.ApiMethods;
import com.training.dr.androidtraining.data.models.User;
import com.training.dr.androidtraining.data.services.ApiGetService;
import com.training.dr.androidtraining.domain.workers.LogoutWorker;
import com.training.dr.androidtraining.presentation.common.dialogs.LogOutDialog;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;
import com.training.dr.androidtraining.presentation.common.events.OnResultDialog;
import com.training.dr.androidtraining.presentation.main.adapters.BookFragmentPageAdapter;
import com.training.dr.androidtraining.presentation.main.fragments.PrefsFragment;
import com.training.dr.androidtraining.presentation.main.fragments.UserInfoFragment;
import com.training.dr.androidtraining.ulils.FragmentsUtils;
import com.training.dr.androidtraining.ulils.Navigator;
import com.training.dr.androidtraining.ulils.SPreferences;
import com.training.dr.androidtraining.ulils.Utils;
import com.training.dr.androidtraining.ulils.db.DataBaseUtils;
import com.training.dr.androidtraining.ulils.image.ImageLoadingManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentLoadedListener, OnResultDialog {

    @BindView(R.id.drawer_layout)
    private DrawerLayout drawer;

    @BindView(R.id.activity_main_tab_layout)
    private TabLayout tabLayout;

    @BindView(R.id.activity_main_toolbar)
    private Toolbar toolbar;

    private BroadcastReceiver br;
    private IntentFilter intentFilter;
    private FragmentManager fragmentManager;
    private View headerView;

    private ProgressDialog logOutProgress;
    private String tag;
    private DialogFragment logoutDialog;
    private MenuItem searchItem;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //     setupWindowAnimations();
        logoutDialog = new LogOutDialog();
        fragmentManager = getSupportFragmentManager();
        tag = getResources().getString(R.string.book_list_fragment_title);
        initProgress();
        initViews();
        loadUser();
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide();
            slideTransition.setSlideEdge(Gravity.START);
            slideTransition.setDuration(500);
            getWindow().setReenterTransition(slideTransition);
            getWindow().setExitTransition(slideTransition);
        }
    }

    private void initProgress() {
        logOutProgress = new ProgressDialog(this);
        logOutProgress.setTitle(getString(R.string.log_out));
        logOutProgress.setCancelable(false);
        logOutProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void initViews() {
        toolbar.setTitle(tag);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadUser() {
        SharedPreferences sp = SPreferences.getInstance().getSharedPreferences();
        user = getUserFromPreferences(sp);
        if (user.getGoodreadId() == 0) {
            startDownloadService();
        } else {
            initHeaderViews();
            initPager();
        }
        initReciever(sp);
    }

    private void startDownloadService() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ApiGetService.class);
        intent.putExtra("url", BuildConfig.BASE_URL + ApiMethods.CURRENT_LOGGED_IN_USER);
        startService(intent);
    }

    private void initReciever(final SharedPreferences sp) {
        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(Utils.PARAM_STATUS, 0);
                if (status == Utils.STATUS_USER_INIT_FINISH) {
                    user = getUserFromPreferences(sp);
                    initHeaderViews();
                    initPager();
                }
            }
        };
        intentFilter = new IntentFilter(Utils.BROADCAST_ACTION);
        registerReceiver(br, intentFilter);
    }

    private void initHeaderViews() {
        initNavHeader();
        initNavHeaderText();
        initNavHeaderImage();
    }

    private void initNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationView navigationViewBottom = findViewById(R.id.activity_main_drawer_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
    }

    private void initNavHeaderText() {
        TextView nameView = headerView.findViewById(R.id.activity_main_header_name_view);
        nameView.setText(user.getName());
    }

    private void initNavHeaderImage() {
        ImageView imageView = headerView.findViewById(R.id.activity_main_header_image);
        ImageLoadingManager.startBuild()
                .imageUrl(user.getAvatarUrl())
                .placeholder(R.drawable.book_image_paceholder)
                .transform(800, 800)
                .rounded(true)
                .load(imageView);

        imageView.setOnClickListener(v -> {
            UserInfoFragment userInfoFragment = UserInfoFragment.getInstance(user);
            hideSearchAndTabLayout();
            FragmentsUtils.addFragment(
                    fragmentManager,
                    R.id.frag_cont,
                    userInfoFragment,
                    true,
                    true,
                    tag);
            drawer.closeDrawer(GravityCompat.START);
        });
    }

    private void initPager() {
        ViewPager pager = findViewById(R.id.activity_main_view_pager);
        FragmentStatePagerAdapter pagerAdapter = new BookFragmentPageAdapter(
                fragmentManager,
                user.getGoodreadId(),
                this);
        pager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.activity_main_tab_layout);
        tabLayout.setupWithViewPager(pager);
    }

    private User getUserFromPreferences(SharedPreferences sp) {
        User user = new User();
        user.setGoodreadId(sp.getInt(DataBaseUtils.USER_GOODREAD_ID, 0));
        user.setName(sp.getString(DataBaseUtils.USER_NAME, ""));
        user.setAvatarUrl(sp.getString(DataBaseUtils.USER_AVATAR_URL, ""));
        return user;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int entryCount = fragmentManager.getBackStackEntryCount();
            if (entryCount > 0) {
                toolbar.setTitle(FragmentsUtils.getTopEntryName(fragmentManager));
                fragmentManager.popBackStack();
                if (entryCount == 1) {
                    showSearchAndTabLayout();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            toolbar.setTitle(tag);
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            showSearchAndTabLayout();
        } else if (id == R.id.nav_logout) {
            logoutDialog.show(fragmentManager, null);
        } else if (id == R.id.nav_preferences) {
            PrefsFragment prefsFragment = PrefsFragment.newInstance();
            FragmentsUtils.addFragment(
                    fragmentManager,
                    R.id.frag_cont,
                    prefsFragment,
                    true,
                    true,
                    tag);

            hideSearchAndTabLayout();
        } else if (id == R.id.nav_favorited) {
            Navigator.goToFavoredBooksScreen(this);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideSearchAndTabLayout() {
        searchItem.setVisible(false);
        tabLayout.setVisibility(View.GONE);
    }

    private void showSearchAndTabLayout() {
        searchItem.setVisible(true);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void logOut() {
        logOutProgress.show();
        new LogoutWorker(this, getContentResolver()).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
        logOutProgress.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, intentFilter);
    }

    @Override
    public void onFragmentLoaded(String toolbarTitle) {
        toolbar.setTitle(toolbarTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.acrivity_main_menu, menu);
        searchItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Navigator.goToSearchScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogRespond(DialogInterface dialogInterface, int result) {
        if (result == LogOutDialog.RESULT_YES) {
            logOut();
        }
    }

}
