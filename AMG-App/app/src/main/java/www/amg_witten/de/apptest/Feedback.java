package www.amg_witten.de.apptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

public class Feedback extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String type = "";
    private String description = "";
    private EditText descriptionText = null;
    private Spinner typeSpinner = null;
    private final String[] types = new String[4];
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
        methoden.onCreateFillIn(this,this,902,R.layout.feedback);

        types[0] = getString(R.string.feedback_type_none);
        types[1] = getString(R.string.feedback_type_bug);
        types[2] = getString(R.string.feedback_type_idea);
        types[3] = getString(R.string.feedback_type_other);
        descriptionText = findViewById(R.id.feedback_description);
        typeSpinner = findViewById(R.id.feedback_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this, false, true))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_feedback,this);
        return true;
    }

    private void Start() {
        Intent intent = new Intent(this, Startseite.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void Senden(View view){
        description = descriptionText.getText().toString();
        description+="\nVersion: "+BuildConfig.VERSION_NAME;
        description+="\nAndroidApi: "+Build.VERSION.SDK_INT;
        type = types[typeSpinner.getSelectedItemPosition()];
        final Activity ac = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=Feedback&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+type+"&gebaeude="+description+"&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    url = url.replaceAll("\n","%30");
                    URL oracle = new URL(url);
                    System.out.println(oracle);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_success),Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_failure,e.getMessage()),Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        Start();
    }

    private void askCompleteDebug(){
        AlertDialog.Builder builder;
        if(Startseite.theme == R.style.DarkTheme){
            builder = new AlertDialog.Builder(this,R.style.DarkDialog);
        }
        else {
            builder = new AlertDialog.Builder(this);
        }
        TextView textView = new TextView(this);
        textView.setText(R.string.feedback_ask_completeDebug);
        textView.setTextColor(getResources().getColor(Startseite.textColor));
        float dpi = getResources().getDisplayMetrics().density;
        textView.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
        builder.setView(textView);
        builder.setPositiveButton(getString(R.string.feedback_completeDebug_positiveButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doCompleteDebug();
                    }
                })
                .setNegativeButton(getString(R.string.feedback_completeDebug_negativeButton),null)
                .setTitle(getString(R.string.feedback_completeDebug_title));
        builder.create().show();
    }

    private void doCompleteDebug(){
        final Context context = this;
        description = "KOMPLETT-DEBUG";
        description+="\nVersion: "+BuildConfig.VERSION_NAME;
        description+="\nAndroidApi: "+Build.VERSION.SDK_INT;
        Map<String, ?> map = Startseite.prefs.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()){
            description+="\n"+entry.getKey()+": "+entry.getValue();
        }
        final String id = UUID.randomUUID().toString();
        description+="\nID: "+id;
        type = types[typeSpinner.getSelectedItemPosition()];
        final Activity ac = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("WORKING");
                try {
                    description = URLEncoder.encode(description,"utf-8");
                    String url = "https://amgitt.de/AMGAppServlet/amgapp?requestType=Feedback&request=&username="+Startseite.prefs.getString("loginUsername","")+"&password="+Startseite.prefs.getString("loginPassword","")+"&datum="+type+"&gebaeude="+description+"&etage=&raum=&wichtigkeit=&fehler=&beschreibung=&status=&bearbeitetVon=";
                    url = url.replaceAll(" ","%20");
                    url = url.replaceAll("\n","%30");
                    URL oracle = new URL(url);
                    System.out.println(oracle);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(oracle.openStream()));

                    while (!(in.readLine()).equals("<body>")){}
                    in.readLine();
                    System.out.println(in.readLine());
                    in.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_success),Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builder;
                            if(Startseite.theme == R.style.DarkTheme){
                                builder = new AlertDialog.Builder(context,R.style.DarkDialog);
                            }
                            else {
                                builder = new AlertDialog.Builder(context);
                            }

                            TextView textView = new TextView(context);
                            textView.setText(getString(R.string.feedback_full_id,id));
                            textView.setTextColor(getResources().getColor(Startseite.textColor));
                            float dpi = getResources().getDisplayMetrics().density;
                            textView.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
                            textView.setTextIsSelectable(true);
                            builder.setView(textView)
                                    .setPositiveButton(getString(R.string.feedback_completeDebug_positiveButton), null)
                                    .setTitle(getString(R.string.feedback_completeDebug_title));
                            builder.create().show();
                        }
                    });
                } catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ac,getString(R.string.feedback_send_failure,e.getMessage()),Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_complete_debug) {
            askCompleteDebug();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
