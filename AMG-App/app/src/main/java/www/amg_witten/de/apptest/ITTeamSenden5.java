package www.amg_witten.de.apptest;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

public class ITTeamSenden5 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean shouldExecResume = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecResume)
            Methoden.onResumeFillIn(this);
        else
            shouldExecResume = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        methoden.onCreateFillIn(this,this,null,R.layout.it_team_senden5);
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this, false, true))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,0,this);
        return true;
    }

    public void Wichtigkeit(View view) {
        switch(view.getId()){
            case R.id.W1:
                ITTeamSenden.wichtigkeit="1";
                break;
            case R.id.W2:
                ITTeamSenden.wichtigkeit="2";
                break;
            case R.id.W3:
                ITTeamSenden.wichtigkeit="3";
                break;
        }
        startActivity(new Intent(this,ITTeamSenden6.class));
    }
}
