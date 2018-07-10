package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Methoden extends AppCompatActivity {
    public boolean onNavigationItemSelectedFillIn(MenuItem item, int currentNavId, Activity currentActivity){
        int id = item.getItemId();

        if(id==R.id.nav_startseite){
            if(id!=currentNavId){
                Intent intent = new Intent(currentActivity, Startseite.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                currentActivity.startActivity(intent);
            }
        }
        else if(id == R.id.nav_vertret_heute){
            if(id!=currentNavId){
                currentActivity.startActivity(new Intent(currentActivity,VertretungsplanHeute.class));
            }
        }
        else if(id == R.id.nav_vertret_morgen){
            if(id!=currentNavId){
                currentActivity.startActivity(new Intent(currentActivity,VertretungsplanFolgetag.class));
            }
        }
        else if (id == R.id.nav_it_team_senden) {
            if(id!=currentNavId){
                currentActivity.startActivity(new Intent(currentActivity,ITTeamSenden.class));
            }
        }
        else if(id==R.id.nav_login){
            if(id!=currentNavId){
                currentActivity.startActivity(new Intent(currentActivity,Login.class));
            }
        }
        else if(id==R.id.nav_it_team_holen){
            if(id!=currentNavId){
                currentActivity.startActivity(new Intent(currentActivity,ITTeamHolen.class));
            }
        }

        DrawerLayout drawer = (DrawerLayout) currentActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onCreateFillIn(Activity currentActivity, NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener, Integer navigationNr){
        NavigationView navigationView = (NavigationView) currentActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        if(navigationNr!=null){
            if(navigationNr!=900){
                navigationView.getMenu().getItem(navigationNr).setChecked(true);
            } else {
                navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setChecked(true);
            }
        }

        if(Startseite.login>0){
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setTitle("Logout");
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
    }
}


class MyAuthenticator extends Authenticator {

    protected PasswordAuthentication getPasswordAuthentication() {
        String username = "Schueler";
        String password = "WirsinddasAMG18";
        return new PasswordAuthentication(username, password.toCharArray());
    }
}