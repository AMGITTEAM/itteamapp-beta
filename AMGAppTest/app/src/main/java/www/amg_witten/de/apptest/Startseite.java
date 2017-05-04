package www.amg_witten.de.apptest;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Startseite extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int login;
    public static String benutzername;
    public static String ip="amgitt.de";
    public static int port=18732;
    public static int timeout=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startseite_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        try {
            File file = new File(this.getFilesDir(), "Login.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String permission = br.readLine();
            login = Integer.parseInt(permission); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team
            benutzername = br.readLine();
            br.close();
        } catch (Exception e){
            e.printStackTrace();
            login=0;
        }
        System.out.println(login);

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            System.exit(0);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Methoden methoden = new Methoden();
        return methoden.onNavigationItemSelectedFillIn(item,R.id.nav_startseite,this);
    }
}
