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

public class ITTeamSenden3 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        methoden.onCreateFillIn(this,this,null,R.layout.it_team_senden3);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,0,this);
        return true;
    }

    public void raum(View view) {
        switch (view.getId()){
            case R.id.R01:
                ITTeamSenden.raum="01";
                break;
            case R.id.R02:
                ITTeamSenden.raum="02";
                break;
            case R.id.R03:
                ITTeamSenden.raum="03";
                break;
            case R.id.R04:
                ITTeamSenden.raum="04";
                break;
            case R.id.R05:
                ITTeamSenden.raum="05";
                break;
            case R.id.R06:
                ITTeamSenden.raum="06";
                break;
            case R.id.R07:
                ITTeamSenden.raum="07";
                break;
            case R.id.R08:
                ITTeamSenden.raum="08";
                break;
            case R.id.R09:
                ITTeamSenden.raum="09";
                break;
            case R.id.R10:
                ITTeamSenden.raum="10";
                break;
            case R.id.R11:
                ITTeamSenden.raum="11";
                break;
            case R.id.R12:
                ITTeamSenden.raum="12";
                break;
            case R.id.R13:
                ITTeamSenden.raum="13";
                break;
            case R.id.R14:
                ITTeamSenden.raum="14";
                break;
        }

        startActivity(new Intent(this,ITTeamSenden4.class));
    }
}
