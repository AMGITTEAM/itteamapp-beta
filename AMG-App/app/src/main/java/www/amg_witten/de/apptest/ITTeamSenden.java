package www.amg_witten.de.apptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class ITTeamSenden extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static String gebaeude;
    public static String etage;
    public static String raum;
    public static String fehler;
    public static String wichtigkeit;
    public static String beschreibung;
    public static boolean ueberschreiben=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,4,R.layout.it_team_senden);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, Startseite.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_it_team_senden,this);
        return true;
    }

    public void gebaeude(View view) {
        switch (view.getId()){
            case R.id.H:
                gebaeude="H";
                startActivity(new Intent(this,ITTeamSenden2.class));
                return;
            case R.id.A:
                gebaeude="A";
                startActivity(new Intent(this,ITTeamSenden2.class));
                return;
            case R.id.N:
                gebaeude="N";
                startActivity(new Intent(this,ITTeamSenden2.class));
                return;
            case R.id.none:
                gebaeude="Ohne";
                etage="Ohne";
                raum="Ohne";
                startActivity(new Intent(this,ITTeamSenden4.class));
        }
    }
}
