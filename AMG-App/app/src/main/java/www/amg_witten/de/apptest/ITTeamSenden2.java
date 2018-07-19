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

public class ITTeamSenden2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);
        View u = findViewById(R.id.U);
        View z = findViewById(R.id.Z);
        switch (ITTeamSenden.gebaeude){
            case "H":
                u.setVisibility(View.INVISIBLE);
                z.setVisibility(View.INVISIBLE);
                break;
            case "A":
                break;
            case "N":
                z.setVisibility(View.INVISIBLE);
                break;
            default:
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
                System.exit(0);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,null,R.layout.it_team_senden2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,0,this);
        return true;
    }

    public void Etage(View view) {
        startActivity(new Intent(this,ITTeamSenden3.class));
        switch (view.getId()){
            case R.id.E0:
                System.out.println("0");
                ITTeamSenden.etage="0";
                return;
            case R.id.E1:
                System.out.println("1");
                ITTeamSenden.etage="1";
                return;
            case R.id.E2:
                System.out.println("2");
                ITTeamSenden.etage="2";
                return;
            case R.id.Z:
                System.out.println("Z");
                ITTeamSenden.etage="Z";
                return;
            case R.id.U:
                System.out.println("U");
                ITTeamSenden.etage="U";
                return;
            default:
                System.out.println("Nicht gefunden: "+view.getId());
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
                System.exit(0);
        }
    }
}
