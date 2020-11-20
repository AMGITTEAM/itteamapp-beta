package www.amg_witten.de.apptest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class StundenplanEdit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;
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
        methoden.onCreateFillIn(this,this,3,R.layout.stundenplan_edit);

        LinkedHashSet<String> stundenplanMontag = Stundenplan.loadStundenplanOrdered("Montag");
        LinkedHashSet<String> stundenplanDienstag = Stundenplan.loadStundenplanOrdered("Dienstag");
        LinkedHashSet<String> stundenplanMittwoch = Stundenplan.loadStundenplanOrdered("Mittwoch");
        LinkedHashSet<String> stundenplanDonnerstag = Stundenplan.loadStundenplanOrdered("Donnerstag");
        LinkedHashSet<String> stundenplanFreitag = Stundenplan.loadStundenplanOrdered("Freitag");

        List<String> faecherNamen = new ArrayList<>();
        final List<String> faecherComponent = new ArrayList<>();

        try {
            faecherComponent.addAll(stundenplanMontag);
        }
        catch(Exception ignored){}
        try {
            faecherComponent.addAll(stundenplanDienstag);
        }
        catch(Exception ignored){}
        try {
            faecherComponent.addAll(stundenplanMittwoch);
        }
        catch(Exception ignored){}
        try {
            faecherComponent.addAll(stundenplanDonnerstag);
        }
        catch(Exception ignored){}
        try {
            faecherComponent.addAll(stundenplanFreitag);
        }
        catch(Exception ignored){}

        for(String stunde : faecherComponent) {
            if(stunde!=null){
                System.out.println(stunde);
                System.out.println(Arrays.deepToString(stunde.split("\\|\\|")));
                if(!faecherNamen.contains(stunde.split("\\|\\|")[4])){
                    faecherNamen.add(stunde.split("\\|\\|")[4]);
                }
            }
        }

        String[] faecherNamenArray = Arrays.copyOf(faecherNamen.toArray(),faecherNamen.size(),String[].class);

        if(faecherNamen.toArray()==null){
            faecherNamenArray = new String[0];
        }

        ((EditText)findViewById(R.id.stundenplan_edit_fach)).setText(getIntent().getExtras().getString("fach").trim());
        ((AutoCompleteTextView)findViewById(R.id.stundenplan_edit_fachName)).setText(getIntent().getExtras().getString("fachName").trim());
        ((AutoCompleteTextView)findViewById(R.id.stundenplan_edit_fachName)).setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,faecherNamenArray));
        ((EditText)findViewById(R.id.stundenplan_edit_lehrer)).setText(getIntent().getExtras().getString("lehrer").trim());
        ((EditText)findViewById(R.id.stundenplan_edit_raum)).setText(getIntent().getExtras().getString("raum").trim());

        ((AutoCompleteTextView)findViewById(R.id.stundenplan_edit_fachName)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selText = ""+((androidx.appcompat.widget.AppCompatTextView)view).getText();
                String fachCompPos = "";
                for(String stunde : faecherComponent) {
                    if(stunde.split("\\|\\|")[4].equals(selText)){
                        fachCompPos = stunde;
                    }
                }
                ((EditText)findViewById(R.id.stundenplan_edit_lehrer)).setText(fachCompPos.split("\\|\\|")[2]);
                ((EditText)findViewById(R.id.stundenplan_edit_fach)).setText(fachCompPos.split("\\|\\|")[1]);
                ((EditText)findViewById(R.id.stundenplan_edit_raum)).setText(fachCompPos.split("\\|\\|")[3]);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this, false, true))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
        return true;
    }

    public void Fertig(View view) {
        String fach = ((EditText)findViewById(R.id.stundenplan_edit_fach)).getText().toString();
        String fachName = ((EditText)findViewById(R.id.stundenplan_edit_fachName)).getText().toString();
        String lehrer = ((EditText)findViewById(R.id.stundenplan_edit_lehrer)).getText().toString();
        String raum = ((EditText)findViewById(R.id.stundenplan_edit_raum)).getText().toString();
        boolean fehler=false;
        if(fach.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_fach));
        }
        if(fachName.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_fachName));
        }
        if(lehrer.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_lehrer));
        }
        if(raum.equals("")){
            fehler=true;
            blink((EditText)findViewById(R.id.stundenplan_edit_raum));
        }
        if(!fehler) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("fach",fach);
            returnIntent.putExtra("lehrer",lehrer);
            returnIntent.putExtra("raum",raum);
            returnIntent.putExtra("fachName",fachName);
            setResult(0,returnIntent);
            finish();
        }
    }

    private void blink(final EditText view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)view.getParent()).setBackground(ContextCompat.getDrawable(context,R.drawable.border));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LinearLayout)view.getParent()).setBackground(null);
                    }
                });
            }
        }).start();
    }

    public void Loeschen(View view) {
        setResult(1);
        finish();
    }
}
