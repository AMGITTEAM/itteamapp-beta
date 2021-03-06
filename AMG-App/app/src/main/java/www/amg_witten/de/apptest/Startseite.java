package www.amg_witten.de.apptest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class Startseite extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int login;
    public static String benutzername;
    public static String passwort;
    public static SharedPreferences prefs;

    public static int theme;
    public static int barColor;
    public static int textColor;
    public static int navigationType;
    public static boolean requiresRecreate;


    public static final boolean KURSSPRECHER_ENABLED = true;

    private boolean shouldExecResume = false;
    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecResume)
            Methoden.onResumeFillIn(this);
        else
            shouldExecResume = true;
    }
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Methoden methoden = new Methoden();
        methoden.makeTheme(this);
        super.onCreate(savedInstanceState);
        System.out.println(login);

        methoden.onCreateFillIn(this,this,0, R.layout.startseite);

        if(!prefs.getBoolean("kalenderAusblenden",false)){
            ((WebView)findViewById(R.id.webViewStartseiteCalendar)).getSettings().setJavaScriptEnabled(true);
            ((WebView)findViewById(R.id.webViewStartseiteCalendar)).getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            ((WebView)findViewById(R.id.webViewStartseiteCalendar)).loadUrl("https://calendar.google.com/calendar/embed?title=Demn%C3%A4chst%20am%20AMG&showPrint=0&showTabs=0&showCalendars=0&showNav=0&showDate=0&showTz=0&mode=AGENDA&height=500&wkst=1&bgcolor=%23FFFFFF&src=lvcbajbvce91hrj2cg531ess60%40group.calendar.google.com&color=%235229A3&ctz=Europe%2FBerlin");
        }
        else {
            findViewById(R.id.webViewStartseiteCalendar).setVisibility(View.GONE);
        }
        if(theme == R.style.DarkTheme){
            ((ImageView)findViewById(R.id.startseite_logo)).setImageDrawable(getResources().getDrawable(R.drawable.ausweis_logo_neu));
        }
        else {
            ((ImageView)findViewById(R.id.startseite_logo)).setImageDrawable(getResources().getDrawable(R.drawable.ausweis_logo_neu));
        }

        checkForFirstRun();
    }

    public static void initVars(Activity activity){
        prefs = activity.getSharedPreferences("Prefs", MODE_PRIVATE);
        login = prefs.getInt("login",0); //0=Nicht eingeloggt, 1=Schüler, 2=Lehrer, 3=IT-Team, 100=Google-Tester
        benutzername = prefs.getString("loginUsername","");
        passwort = prefs.getString("loginPassword","");
        navigationType = prefs.getInt("navigationType",0);
    }

    private void checkForFirstRun(){
        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        int savedVersionCode = prefs.getInt("version_code", -1);

        // Check for first run or upgrade
        if (savedVersionCode == -1) {
            prefs.edit().putInt("version_code", currentVersionCode).apply();
            HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
            HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
        } else if (currentVersionCode > savedVersionCode) {
            prefs.edit().putInt("version_code", currentVersionCode).apply();
            if(savedVersionCode<1){
                HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
                HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
            }
            if(savedVersionCode<6){
                startActivity(new Intent(this, Login.class));
            }
            if(savedVersionCode<8){
                prefs.edit().putBoolean("vertretungsplanIconsEnabled",true).apply();
            }
            if(savedVersionCode<23){
                String montag = prefs.getString("stundenplanMontag","").replaceAll("\\|\\| \\|\\| \\|\\| ","|| || || || ");
                prefs.edit().putString("stundenplanMontag",montag).apply();
                String dienstag = prefs.getString("stundenplanDienstag","").replaceAll("\\|\\| \\|\\| \\|\\| ","|| || || || ");
                prefs.edit().putString("stundenplanDienstag",dienstag).apply();
                String mittwoch = prefs.getString("stundenplanMittwoch","").replaceAll("\\|\\| \\|\\| \\|\\| ","|| || || || ");
                prefs.edit().putString("stundenplanMittwoch",mittwoch).apply();
                String donnerstag = prefs.getString("stundenplanDonnerstag","").replaceAll("\\|\\| \\|\\| \\|\\| ","|| || || || ");
                prefs.edit().putString("stundenplanDonnerstag",donnerstag).apply();
                String freitag = prefs.getString("stundenplanFreitag","").replaceAll("\\|\\| \\|\\| \\|\\| ","|| || || || ");
                prefs.edit().putString("stundenplanFreitag",freitag).apply();
            }
            if(savedVersionCode<37){
                startActivity(new Intent(this,Login.class));
            }

            AlertDialog.Builder builder;
            if(Startseite.theme == R.style.DarkTheme){
                builder = new AlertDialog.Builder(this,R.style.DarkDialog);
            }
            else {
                builder = new AlertDialog.Builder(this);
            }
            TextView textView = new TextView(this);
            textView.setText(R.string.changelog);
            textView.setTextColor(getResources().getColor(Startseite.textColor));
            float dpi = getResources().getDisplayMetrics().density;
            textView.setPadding((int)(19*dpi), (int)(5*dpi), (int)(14*dpi), (int)(5*dpi));
            builder.setView(textView);
            builder.setPositiveButton(getString(R.string.startseite_changelog_positive), null)
                    .setTitle(getString(R.string.startseite_changelog_title));
            builder.create().show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            System.exit(0);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_startseite,this);
        return true;
    }
}
