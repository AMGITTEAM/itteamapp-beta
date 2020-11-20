package www.amg_witten.de.apptest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotNavSonstiges extends AppCompatActivity
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
        methoden.onCreateFillIn(this,this,904, R.layout.botnav_sonstiges);

        ArrayList<String> arraylist = new ArrayList<>();
        arraylist.add(getString(R.string.nav_drawer_einstellungen));
        arraylist.add(getString(R.string.nav_drawer_feedback));
        if(Startseite.login > 0)
            arraylist.add("Logout");
        else
            arraylist.add(getString(R.string.nav_drawer_login));
        arraylist.add(getString(R.string.nav_drawer_info));

        ListView listView = findViewById(R.id.botnav_sonstiges_listview);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arraylist));

        final AppCompatActivity context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        context.startActivity(new Intent(context, Einstellungen.class));
                        break;
                    case 1:
                        context.startActivity(new Intent(context, Hilfe.class));
                        break;
                    case 2:
                        context.startActivity(new Intent(context, Login.class));
                        break;
                    case 3:
                        context.startActivity(new Intent(context, Info.class));
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.botnav_sonstiges,this);
        return true;
    }
}
