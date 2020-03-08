package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.view.ViewStub;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

class Methoden {
    void onNavigationItemSelectedFillIn(MenuItem item, int currentNavId, Activity currentActivity){
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_startseite:
                if (id != currentNavId) {
                    Intent intent = new Intent(currentActivity, Startseite.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    currentActivity.startActivity(intent);
                }
                break;
            case R.id.nav_vertretungsplan:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, Vertretungsplan.class));
                }
                break;
            case R.id.nav_stundenplan:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, Stundenplan.class));
                }
                break;
            case R.id.nav_schwarzes_brett:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, SchwarzesBrett.class));
                }
                break;
            case R.id.nav_it_team_senden:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, ITTeamSenden.class));
                }
                break;
            case R.id.nav_feedback_holen:
                if(id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, FeedbackHolen.class));
                }
                break;
            case R.id.nav_login:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, Login.class));
                }
                break;
            case R.id.nav_info:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, Info.class));
                }
                break;
            case R.id.nav_einstellungen:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, Einstellungen.class));
                }
                break;
            case R.id.nav_feedback:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, Hilfe.class));
                }
                break;
            case R.id.nav_it_team_holen:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, ITTeamHolen.class));
                }
                break;
        }

        DrawerLayout drawer = currentActivity.findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    void onCreateFillIn(AppCompatActivity currentActivity, NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener, Integer navigationNr, int layout){
        Startseite.initVars(currentActivity);
        Toolbar toolbar = currentActivity.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(currentActivity.getResources().getColor(Startseite.barColor));
        currentActivity.setSupportActionBar(toolbar);

        DrawerLayout drawer = currentActivity.findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(currentActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = currentActivity.findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        navigationView.setItemTextColor(ColorStateList.valueOf(currentActivity.getResources().getColor(Startseite.textColor)));
        if(Startseite.theme == R.style.DarkTheme){
            navigationView.setBackgroundColor(currentActivity.getResources().getColor(R.color.darkBar));
            navigationView.setItemBackground(currentActivity.getResources().getDrawable(R.drawable.nav_menu_dark));
        }

        if(navigationNr!=null){
            switch (navigationNr) {
                case 900:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 4).setCheckable(true).setChecked(true);
                    break;
                case 901:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 3).setCheckable(true).setChecked(true);
                    break;
                case 902:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 2).setCheckable(true).setChecked(true);
                    break;
                case 903:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setCheckable(true).setChecked(true);
                    break;
                default:
                    navigationView.getMenu().getItem(navigationNr).setCheckable(true).setChecked(true);
                    break;
            }
        }

        if(Startseite.login>0){
            navigationView.getMenu().getItem(navigationView.getMenu().size()-4).setTitle("Logout");
        }
        if(Startseite.login<1){
            navigationView.getMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
        }
        if(Startseite.login==1){
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
        }
        if(Startseite.login<=2){
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);
        }

        ViewStub stub = currentActivity.findViewById(R.id.all_content);
        stub.setLayoutResource(layout);
        stub.inflate();
    }

    public void makeTheme(Activity activity){
        boolean darkMode = (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if(!darkMode){
            darkMode = activity.getSharedPreferences("Prefs", Context.MODE_PRIVATE).getBoolean("dunklesDesign",false);
        }

        Startseite.theme = darkMode  ? R.style.DarkTheme : R.style.AppTheme_NoActionBar;

        initThemeDependentVars();

        activity.setTheme(Startseite.theme);
    }

    public static void initThemeDependentVars(){
        switch(Startseite.theme){
            case R.style.DarkTheme:
                Startseite.barColor = R.color.darkBar;
                Startseite.textColor = R.color.darkTextColor;
                break;
            case R.style.AppTheme_NoActionBar:
                Startseite.barColor = R.color.colorPrimary;
                Startseite.textColor = R.color.black;
                break;
        }
    }
}


class MyAuthenticator extends Authenticator {

    final private String password;

    MyAuthenticator(Context context){
        SharedPreferences prefs = context.getSharedPreferences("Prefs",Context.MODE_PRIVATE);
        password = prefs.getString("passwordVertretungsplanSchueler","");
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        String username = "Schueler";
        return new PasswordAuthentication(username, password.toCharArray());
    }
}