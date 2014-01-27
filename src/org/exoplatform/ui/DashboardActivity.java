package org.exoplatform.ui;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
//import greendroid.widget.ActionBarItem;
//import greendroid.widget.ActionBarItem.Type;
//import greendroid.widget.LoaderActionBarItem;

import java.util.ArrayList;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.exoplatform.poc.tabletversion.R;
import org.exoplatform.controller.dashboard.DashboardItemAdapter;
import org.exoplatform.controller.dashboard.DashboardLoadTask;
import org.exoplatform.model.GadgetInfo;
import org.exoplatform.model.SocialActivityInfo;
import org.exoplatform.singleton.AccountSetting;
import org.exoplatform.ui.setting.SettingActivity;
import org.exoplatform.utils.ExoConnectionUtils;
import org.exoplatform.utils.ExoConstants;
import org.exoplatform.widget.ConnTimeOutDialog;
import org.exoplatform.widget.ConnectionErrorDialog;
//import org.exoplatform.widget.MyActionBar;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.exoplatform.widget.WarningDialog;

public class DashboardActivity extends ActionBarActivity implements DashboardLoadTask.AsyncTaskListener {
    //extends MyActionBar {

  /**=== Result of loading dashboard ===*/
  public static final int         RESULT_OK      = 1;

  public static final int         RESULT_ERROR   = 0;

  public static final int         RESULT_TIMEOUT = -1;


  private static final String     ACCOUNT_SETTING = "account_setting";

  private ListView                listView;

  private View                    empty_stub;

  //private String                  title;

  private Menu mOptionsMenu;

  private DashboardLoadTask       mLoadTask;


  private static final String TAG = "eXo____DashboardActivity____";


  //public static DashboardActivity dashboardActivity;


  //private LoaderActionBarItem     loaderItem;

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    setTitle(getString(R.string.Dashboard));
    //setActionBarContentView(R.layout.dashboard_layout);
    setContentView(R.layout.dashboard_layout);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //getActionBar().setType(greendroid.widget.ActionBar.Type.Normal);
    //addActionBarItem(Type.Refresh);
    //getActionBar().getItem(0).setDrawable(R.drawable.action_bar_icon_refresh);

    listView = (ListView) findViewById(R.id.dashboard_listview);
    listView.setCacheColorHint(Color.TRANSPARENT);
    listView.setFadingEdgeLength(0);
    listView.setScrollbarFadingEnabled(true);
    listView.setDivider(null);
    // listView.setDividerHeight(-1);

    if (bundle != null) {
      AccountSetting accountSetting = bundle.getParcelable(ACCOUNT_SETTING);
      AccountSetting.getInstance().setInstance(accountSetting);
      ArrayList<String> cookieList = AccountSetting.getInstance().cookiesList;
      ExoConnectionUtils.setCookieStore(ExoConnectionUtils.cookiesStore, cookieList);
    }
    //loaderItem = (LoaderActionBarItem) getActionBar().getItem(0);

    //onLoad(loaderItem);
    startLoadingApps();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.i(TAG, "onCreateOptionsMenu");
    getMenuInflater().inflate(R.menu.home, menu);
    mOptionsMenu = menu;
    menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
    menu.findItem(R.id.menu_sign_out).setVisible(false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;

      case R.id.menu_refresh:
        startLoadingApps();
        return true;

      case R.id.menu_settings:
        redirectToSetting();
        break;

    }

    return super.onOptionsItemSelected(item);
  }


  /**
   * Change state of refresh icon on action bar
   *
   * @param refreshing
   */
  public void setRefreshActionButtonState(boolean refreshing) {
    Log.i(TAG, "setRefreshActionButtonState: " + refreshing);

    if (mOptionsMenu == null) return ;
    final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
    Log.i(TAG, "setRefreshActionButtonState - refreshItem: " + refreshItem);
    if (refreshItem == null) return ;

    //boolean currentState = refreshItem.getActionView() != null;

    if (refreshing)
      refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
    else {
      refreshItem.setActionView(null);
      //supportInvalidateOptionsMenu();
    }
  }

  private void redirectToSetting() {
    Intent next = new Intent(this, SettingActivity.class);
    next.putExtra(ExoConstants.SETTING_TYPE, SettingActivity.PERSONAL_TYPE);
    startActivity(next);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(ACCOUNT_SETTING, AccountSetting.getInstance());
  }

  /** TODO replace this function
  public void onLoad(LoaderActionBarItem loader) {
    if (ExoConnectionUtils.isNetworkAvailableExt(this)) {
      if (mLoadTask == null || mLoadTask.getStatus() == DashboardLoadTask.Status.FINISHED) {
        //mLoadTask = (DashboardLoadTask) new DashboardLoadTask(this, loader).execute();
        mLoadTask = (DashboardLoadTask) new DashboardLoadTask(this).execute();
      }
    } else {
      new ConnectionErrorDialog(this).show();
    }
  }
  **/

  // replace onLoad()
  public void startLoadingApps() {
    if (!ExoConnectionUtils.isNetworkAvailableExt(this)) {
      new ConnectionErrorDialog(this).show();
      return ;
    }

    if (mLoadTask == null || mLoadTask.getStatus() == DashboardLoadTask.Status.FINISHED) {
      setRefreshActionButtonState(true);
      mLoadTask = new DashboardLoadTask();
      mLoadTask.setListener(this);
      mLoadTask.execute();
    }
  }

  @Override
  public void onLoadingAppsFinished(int result, int dashboardSize,
                                    ArrayList<GadgetInfo> gadgetInfos, String dashboardError) {
    setRefreshActionButtonState(false);

    if (result == RESULT_OK) {
      if (dashboardSize == 0)  setEmptyView(View.VISIBLE);
      else {
        if (gadgetInfos.size() > 0) {
          setAdapter(gadgetInfos);
          setEmptyView(View.GONE);
        } else
          setEmptyView(View.VISIBLE);
      }

    } else if (result == RESULT_ERROR) {
      setEmptyView(View.VISIBLE);

      new WarningDialog(this, getString(R.string.Warning), getString(R.string.GadgetsCannotBeRetrieved),
          getString(R.string.OK)).show();
    } else if (result == RESULT_TIMEOUT) {
      new ConnTimeOutDialog(this, getString(R.string.Warning), getString(R.string.OK)).show();
    }

    if (dashboardError.length() > 0) {
      new WarningDialog(this, getString(R.string.Warning),
          "Apps: " + dashboardError + getString(R.string.GadgetsCannotBeRetrieved),
          getString(R.string.OK)).show();
    }
  }

  public void onCancelLoad() {
    if (mLoadTask != null && mLoadTask.getStatus() == DashboardLoadTask.Status.RUNNING) {
      mLoadTask.cancel(true);
      mLoadTask = null;
    }
  }

  public void setAdapter(ArrayList<GadgetInfo> result) {
    listView.setAdapter(new DashboardItemAdapter(this, result));
  }

  public void setEmptyView(int status) {
    if (empty_stub == null) {
      initStubView();
    }
    empty_stub.setVisibility(status);

  }

  private void initStubView() {
    empty_stub = ((ViewStub) findViewById(R.id.dashboard_empty_stub)).inflate();
    ImageView emptyImage = (ImageView) empty_stub.findViewById(R.id.empty_image);
    emptyImage.setBackgroundResource(R.drawable.icon_for_no_gadgets);
    TextView emptyStatus = (TextView) empty_stub.findViewById(R.id.empty_status);
    emptyStatus.setText(getString(R.string.EmptyDashboard));
  }

  /**
  @Override
  public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
    switch (position) {

    case -1:
      finish();
      break;
    case 0:
      loaderItem = (LoaderActionBarItem) item;
      onLoad(loaderItem);

      break;

    default:

    }
    return true;

  }
  **/

  @Override
  public void finish() {
    super.finish();
  }

  @Override
  public void onBackPressed() {
    onCancelLoad();
    finish();
  }

}
