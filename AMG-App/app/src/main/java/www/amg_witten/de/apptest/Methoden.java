package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
            case R.id.nav_it_team_senden:
                if (id != currentNavId) {
                    currentActivity.startActivity(new Intent(currentActivity, ITTeamSenden.class));
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

        DrawerLayout drawer = currentActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    void onCreateFillIn(Activity currentActivity, NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener, Integer navigationNr, int layout){
        NavigationView navigationView = currentActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        if(navigationNr!=null){
            switch (navigationNr) {
                case 900:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 4).setChecked(true);
                    break;
                case 901:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 3).setChecked(true);
                    break;
                case 902:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 2).setChecked(true);
                    break;
                case 903:
                    navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setChecked(true);
                    break;
                default:
                    navigationView.getMenu().getItem(navigationNr).setChecked(true);
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
        }
        if(Startseite.login==1){
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
        }
        if(Startseite.login<=2){
            navigationView.getMenu().getItem(4).setVisible(false);
        }

        ViewStub stub = currentActivity.findViewById(R.id.all_content);
        stub.setLayoutResource(layout);
        stub.inflate();
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