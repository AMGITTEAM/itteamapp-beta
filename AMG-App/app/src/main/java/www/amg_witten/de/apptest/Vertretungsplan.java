package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vertretungsplan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Activity thise = this;
    private static String date = "";
    private static String klasse = "";

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

        Intent thisIntent = getIntent();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,thisIntent.getIntExtra("navID",0),R.layout.vertretungsplan);

        date = thisIntent.getStringExtra("Date");
        setTitle(thisIntent.getStringExtra("Title"));

        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        klasse = prefs.getString("klasse","");

        new Thread(new Runnable() {
            @Override
            public void run() {
                action(thise);
            }
        }).start();
    }

    public static void action(final Activity thise){
        System.out.println(klasse);
        final List<String> urlEndings = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        final List<String> klassen = new ArrayList<>();
        final File lFile = new File(thise.getFilesDir() + "/Vertretungsplan"+date+".htm");
        try {
            Looper.prepare();
            final ProgressDialog pDialog = new ProgressDialog(thise);

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setTitle("Lädt...");
                    pDialog.setMessage("Dateien werden gezählt...");
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setProgress(0);
                    pDialog.show();
                }
            });

            Authenticator.setDefault(new MyAuthenticator());
            boolean exit=false;
            String next = "001.htm";
            urlEndings.add(next);
            while(!exit) {
                URL mainUrl = new URL("https://www.amg-witten.de/fileadmin/VertretungsplanSUS/"+date+"/subst_"+next);

                BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
                StringBuilder full = new StringBuilder();
                String str;
                while ((str = in.readLine()) != null) {
                    full.append(str);
                }
                in.close();

                String head = onlyElement(full.toString(),"head");
                String contentMeta = onlyArgumentOfElement(head,"meta http-equiv=\"refresh\"","content");
                String nextURL = contentMeta.split("15; URL=subst_")[1];
                next=nextURL;
                if(next.equals("001.htm")) {
                    exit=true;
                }
                else {
                    urlEndings.add(nextURL);
                }
            }

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(urlEndings.size());
                    pDialog.setMessage("Dateien werden heruntergeladen...");
                }
            });

            for (int i=0;i<urlEndings.size();i++) {
                URL mainUrl = new URL("https://www.amg-witten.de/fileadmin/VertretungsplanSUS/"+date+"/subst_"+urlEndings.get(i));
                BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
                StringBuilder full = new StringBuilder();
                String str;
                while ((str = in.readLine()) != null) {
                    full.append(str);
                }
                in.close();

                String body = onlyElement(full.toString(),"body");
                String center = onlyElement(body,"center");
                String table = onlyElement(center,"table"," class=\"mon_list\" ");
                tables.add(table);
                pDialog.setProgress(i+1);
            }

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(urlEndings.size());
                    pDialog.setMessage("Dateien werden eingelesen...");
                }
            });

            for(int i=0; i<tables.size(); i++){
                String[] klassenArrayUnfertig = tables.get(i).split("td class=\"list inline_header\" colspan=\"8\"");
                for(int ie = 0; ie<klassenArrayUnfertig.length; ie++){
                    klassenArrayUnfertig[ie] = klassenArrayUnfertig[ie].replaceFirst(">","");
                }
                for (int ie = 1; ie<klassenArrayUnfertig.length; ie++){
                    klassen.add(klassenArrayUnfertig[ie].split("</td>")[0].trim());
                }
                pDialog.setProgress(i+1);
            }

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(klassen.size());
                    pDialog.setMessage("Einträge werden überprüft...");
                }
            });

            final List<String> realEintraege = new ArrayList<>();

            for(int i=0; i<tables.size(); i++){
                String[] eintraegeArrayUnfertigZwei = tables.get(i).split("tr ");
                for (String eintraegeArrayUnfertigEin : eintraegeArrayUnfertigZwei) {
                    if (!(eintraegeArrayUnfertigEin.contains("class=\"list inline_header\"")||eintraegeArrayUnfertigEin.contains("class='list inline_header'")||eintraegeArrayUnfertigEin.contains("(Fach)"))) {
                        if(eintraegeArrayUnfertigEin.length()!=1){
                            realEintraege.add(eintraegeArrayUnfertigEin);
                        }
                    }
                    else {
                    }
                }
                pDialog.setProgress(i+1);
            }

            final List<VertretungModel> vertretungModels = new ArrayList<>();

            int i=0;

            List<VertretungModel> fertigeMulti = new ArrayList<>();

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(realEintraege.size());
                    pDialog.setMessage("Einträge werden extrahiert...");
                }
            });

            for(String s : realEintraege){
                i++;
                Matcher matcher = Pattern.compile("<td class=\"list\"(?s)(.*?)</td>").matcher(s);

                List<String> allMatches = new ArrayList<>();
                while (matcher.find()) {
                    String match = matcher.group();
                    allMatches.add(match.replace("<td class=\"list\" align=\"center\">","").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\">","").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" >","").replace("<td class=\"list\">","").replace("</td>","").replace("<b>","").replace("</b>","").replace("<span style=\"color: #800000\">","").replace("<span style=\"color: #0000FF\">","").replace("<span style=\"color: #010101\">","").replace("<span style=\"color: #008040\">","").replace("</span>","").replace("&nbsp;","").replaceFirst(">",""));
                }

                VertretungModel model = new VertretungModel(allMatches.get(0),allMatches.get(1),allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7));


                Matcher matcherabcd = Pattern.compile("0\\dabcd").matcher(allMatches.get(1));
                Matcher matcherab = Pattern.compile("0\\dab").matcher(allMatches.get(1));
                Matcher matcherac = Pattern.compile("0\\dac").matcher(allMatches.get(1));
                Matcher matcherad = Pattern.compile("0\\dad").matcher(allMatches.get(1));
                Matcher matcherba = Pattern.compile("0\\dba").matcher(allMatches.get(1));
                Matcher matcherbc = Pattern.compile("0\\dbc").matcher(allMatches.get(1));
                Matcher matcherbd = Pattern.compile("0\\dbd").matcher(allMatches.get(1));
                Matcher matcherca = Pattern.compile("0\\dca").matcher(allMatches.get(1));
                Matcher matchercb = Pattern.compile("0\\dcb").matcher(allMatches.get(1));
                Matcher matchercd = Pattern.compile("0\\dcd").matcher(allMatches.get(1));
                Matcher matcherda = Pattern.compile("0\\dda").matcher(allMatches.get(1));
                Matcher matcherdb = Pattern.compile("0\\ddb").matcher(allMatches.get(1));
                Matcher matcherdc = Pattern.compile("0\\ddc").matcher(allMatches.get(1));
                Matcher matcherabc = Pattern.compile("0\\dabc").matcher(allMatches.get(1));
                Matcher matcherabd = Pattern.compile("0\\dabd").matcher(allMatches.get(1));
                Matcher matcheracb = Pattern.compile("0\\dacb").matcher(allMatches.get(1));
                Matcher matcheracd = Pattern.compile("0\\dacd").matcher(allMatches.get(1));
                Matcher matcheradb = Pattern.compile("0\\dadb").matcher(allMatches.get(1));
                Matcher matcheradc = Pattern.compile("0\\dadc").matcher(allMatches.get(1));
                Matcher matcherbac = Pattern.compile("0\\dbac").matcher(allMatches.get(1));
                Matcher matcherbad = Pattern.compile("0\\dbad").matcher(allMatches.get(1));
                Matcher matcherbca = Pattern.compile("0\\dbca").matcher(allMatches.get(1));
                Matcher matcherbcd = Pattern.compile("0\\dbcd").matcher(allMatches.get(1));
                Matcher matcherbda = Pattern.compile("0\\dbda").matcher(allMatches.get(1));
                Matcher matcherbdc = Pattern.compile("0\\dbcd").matcher(allMatches.get(1));
                Matcher matchercab = Pattern.compile("0\\dcab").matcher(allMatches.get(1));
                Matcher matchercad = Pattern.compile("0\\dcad").matcher(allMatches.get(1));
                Matcher matchercba = Pattern.compile("0\\dcba").matcher(allMatches.get(1));
                Matcher matchercbd = Pattern.compile("0\\dcbd").matcher(allMatches.get(1));
                Matcher matchercda = Pattern.compile("0\\dcda").matcher(allMatches.get(1));
                Matcher matchercdb = Pattern.compile("0\\dcdb").matcher(allMatches.get(1));
                Matcher matcherdab = Pattern.compile("0\\ddab").matcher(allMatches.get(1));
                Matcher matcherdac = Pattern.compile("0\\ddac").matcher(allMatches.get(1));
                Matcher matcherdba = Pattern.compile("0\\ddba").matcher(allMatches.get(1));
                Matcher matcherdbc = Pattern.compile("0\\ddbc").matcher(allMatches.get(1));
                Matcher matcherdca = Pattern.compile("0\\ddca").matcher(allMatches.get(1));
                Matcher matcherdcb = Pattern.compile("0\\ddcb").matcher(allMatches.get(1));

                if(matcherabcd.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherabcd.group();
                        String klassengang = match.replace("abcd","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherab.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherab.group();
                        String klassengang = match.replace("ab","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherac.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherac.group();
                        String klassengang = match.replace("ac","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherad.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherad.group();
                        String klassengang = match.replace("ad","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherba.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherba.group();
                        String klassengang = match.replace("ba","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbc.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherbc.group();
                        String klassengang = match.replace("bc","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbd.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherbd.group();
                        String klassengang = match.replace("bd","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherca.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherca.group();
                        String klassengang = match.replace("ca","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }

                else if(matchercb.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matchercb.group();
                        String klassengang = match.replace("cb","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }

                else if(matchercd.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherca.group();
                        String klassengang = match.replace("cd","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherda.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherda.group();
                        String klassengang = match.replace("da","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdb.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherdb.group();
                        String klassengang = match.replace("db","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdc.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherdc.group();
                        String klassengang = match.replace("dc","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherabc.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherabc.group();
                        String klassengang = match.replace("abc","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherabd.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcherabd.group();
                        String klassengang = match.replace("abd","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcheracb.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcheracb.group();
                        String klassengang = match.replace("acb","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcheracd.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcheracd.group();
                        String klassengang = match.replace("acd","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcheradb.matches()){
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        String match = matcheradb.group();
                        String klassengang = match.replace("adb","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcheradc.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcheradc.group();
                        String klassengang = match.replace("adc", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbac.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherbac.group();
                        String klassengang = match.replace("bac", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbad.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherbad.group();
                        String klassengang = match.replace("bad", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbca.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherbca.group();
                        String klassengang = match.replace("bca", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbcd.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherbcd.group();
                        String klassengang = match.replace("bcd", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbda.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherbda.group();
                        String klassengang = match.replace("bda", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherbdc.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherbdc.group();
                        String klassengang = match.replace("bdc", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matchercab.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matchercab.group();
                        String klassengang = match.replace("cab", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matchercad.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matchercad.group();
                        String klassengang = match.replace("cad", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matchercba.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matchercba.group();
                        String klassengang = match.replace("cba", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matchercbd.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matchercbd.group();
                        String klassengang = match.replace("cbd", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matchercda.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matchercda.group();
                        String klassengang = match.replace("cda", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matchercdb.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matchercdb.group();
                        String klassengang = match.replace("cdb", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdab.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherdab.group();
                        String klassengang = match.replace("dab", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdac.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherdac.group();
                        String klassengang = match.replace("dac", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdba.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherdba.group();
                        String klassengang = match.replace("dba", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdbc.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherdbc.group();
                        String klassengang = match.replace("dbc", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdca.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherdca.group();
                        String klassengang = match.replace("dca", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else if(matcherdcb.matches()) {
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti) {
                        if (existModel.toString().equals(model.toString())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        String match = matcherdcb.group();
                        String klassengang = match.replace("dcb", "");
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                        fertigeMulti.add(model);
                    }
                }
                else {
                    vertretungModels.add(model);
                }
                pDialog.setProgress(i);
            }

            thise.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog.setMax(klassen.size());
                    pDialog.setMessage("Einträge werden zusammengestellt...");

                    final List<ItemModel> data = new ArrayList<>();
                    final List<String> fertigeKlassen = new ArrayList<>();
                    for(int i=0; i<klassen.size(); i++){
                        if(!fertigeKlassen.contains(klassen.get(i))){
                            int rightRowsCount = 0;
                            for(int ie=0; ie<vertretungModels.size(); ie++){
                                if(vertretungModels.get(ie).getKlasse().equals(klassen.get(i))) {
                                    rightRowsCount++;
                                }
                            }
                            VertretungModel[] rightRows = new VertretungModel[rightRowsCount];
                            rightRowsCount=0;
                            for(int ie=0; ie<vertretungModels.size(); ie++){
                                if(vertretungModels.get(ie).getKlasse().equals(klassen.get(i))){
                                    rightRows[rightRowsCount] = vertretungModels.get(ie);
                                    rightRowsCount++;
                                }
                            }
                            data.add(new ItemModel(
                                    rightRows));
                            fertigeKlassen.add(klassen.get(i));
                        }
                        pDialog.setProgress(i+1);
                    }
                    pDialog.setMax(fertigeKlassen.size()+3);
                    pDialog.setMessage("Tabelle wird erstellt...");
                    try {
                        FileWriter fw = new FileWriter(lFile);
                        fw.write("<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "<head>\n" +
                                "<meta charset=\"UTF-8\">\n" +
                                "<title>Accordion</title>\n" +
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                                "<link href='http://fonts.googleapis.com/css?family=roboto:400,600,700' rel='stylesheet' type='text/css'>\n" +
                                "\n" +
                                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script> \n" +
                                "<script type = \"text/javascript\"> \n" +
                                "$(function() {\n" +
                                "\n" +
                                "\tvar $navLink = $('#accordion').find('li');\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "\t$navLink.on('click', function() {\n" +
                                "\t\tvar panelToShow = $(this).data('panel-id');\n" +
                                "\t\tvar $activeLink = $('#accordion').find('.active');\n" +
                                "\n" +
                                "\t\t// show new panel\n" +
                                "\t\t// .stop is used to prevent the animation from repeating if you keep clicking the same link\n" +
                                "\t\t$('.' + panelToShow).stop().slideDown();\n" +
                                "\t\t$('.' + panelToShow).addClass('active');\n" +
                                "\n" +
                                "\n" +
                                "\t\t// hide the previous panel \n" +
                                "\t\t$activeLink.stop().slideUp()\n" +
                                "\t\t.removeClass('active');\n" +
                                "\t});\n" +
                                "\n" +
                                "});\n" +
                                "\n" +
                                "    var xOffset = 30;\n" +
                                "    var yOffset = 10;\n" +
                                "    $(document).ready(function() {\n" +
                                "        $(\"body\").append(\"<div id='tooltip'>\");\n" +
                                "\n" +
                                "  var $navLink = $('#accordion').find('img');\n" +
                                "  console.log($navLink);\n" +
                                "  $navLink.on('click', function(e) {\n" +
                                "  console.log(\"CLICKED\");\n" +
                                "            e.preventDefault();\n" +
                                "            $(\"#tooltip\")\n" +
                                "                .text($(this).attr('title'))\n" +
                                "                .css(\"top\",(e.pageY - xOffset) + \"px\")\n" +
                                "                .css(\"left\",(e.pageX + yOffset) + \"px\")\n" +
                                "                .show().fadeIn(\"fast\");\n" +
                                "        })\n" +
                                "            .on('mouseout',function(){\n" +
                                "                $(\"#tooltip\").fadeOut(\"slow\").hide();\n" +
                                "            })\n" +
                                "    })\n" +
                                "</script>\n");
                        fw.flush();
                        pDialog.setProgress(1);
                        fw.write("\n" +
                                "<style>\n" +
                                "body {\n" +
                                "  background-color: #ccc;\n" +
                                "  margin: auto auto;\n" +
                                "  padding: 0;\n" +
                                "  width:100%;\n" +
                                "}\n" +
                                "/**/\n" +
                                "#accordion {\n" +
                                //"  width: 80%;\n" +
                                "  margin: 10px auto;\n" +
                                "  height: 50%;\n" +
                                "  position: relative;\n" +
                                "}\n" +
                                "\n" +
                                "#accordion ul {\n" +
                                "  text-align: center;\n" +
                                "  margin: 0;\n" +
                                "}\n" +
                                "\n" +
                                "#accordion ul li {\n" +
                                "  list-style-type: none;\n" +
                                "  cursor: pointer;\n" +
                                "  font-family: \"roboto\", sans-serif;\n" +
                                "  padding: 0.4em;\n" +
                                "  font-size: 1.4em;\n" +
                                "  color: white;\n" +
                                "  letter-spacing: 0.2em;\n" +
                                "  transition: 0.3s ease all;\n" +
                                "  text-shadow: -1px 0 grey, 0 1px grey, 1px 0 grey, 0 -1px grey;\n" +
                                "}\n" +
                                "\n" +
                                "#accordion ul li:hover { color: #ccc; }\n" +
                                "\n" +
                                "#accordion ul a { color: #333; }\n");
                        fw.flush();
                        fw.write("/**/\n" +
                                ".panels {\n" +
                                "padding: 0;\n" +
                                "}\n" +
                                "\n" +
                                " \n" +
                                ".panel {\n" +
                                "  display: none;\n" +
                                "   padding: 25px;\n" +
                                "  font-family: \"roboto\", sans-serif;\n" +
                                "  padding: 0.3em;\n" +
                                "  font-size: 1.0em;\n" +
                                "  color: white;\n" +
                                "  background-color: white;\n" +
                                "  color: #333;\n" +
                                "}\n" +
                                "@media only screen and (max-width:480px) and (orientation:portrait) {\n" +
                                "      nav { display:none;}\n" +
                                "\n" +
                                ".panel {\n" +
                                "  padding: 0.2em;\n" +
                                "  font-size: 0.7em;\n" +
                                " }   \n" +
                                " }\n" +
                                "\n" +
                                "    #tooltip{\n" +
                                "        position:absolute;\n" +
                                "        border:1px solid #222;\n" +
                                "        border-radius: 6px; \n" +
                                "        background:#444;\n" +
                                "        padding:3px 6px;\n" +
                                "        color:#fff;\n" +
                                "        font-family:verdana, sans-serif;\n" +
                                "        display:none;\n" +
                                "    }\n" +
                                "    \n" +
                                "   table {\n" +
                                "    border-collapse: collapse;\n" +
                                "    width:100%;" +
                                "}\n" +
                                "\n" +
                                "   table, td, th {\n" +
                                "    border: 1px solid black;\n" +
                                "} \n" +
                                "</style>");
                        fw.flush();
                        pDialog.setProgress(2);
                        fw.write("\t</head>\n" +
                                "\t<body>\n" +
                                "  <div class=\"container\">\n" +
                                "    <div id=\"accordion\">\n" +
                                "      <ul class=\"panels\">\n");
                        fw.flush();
                        for(int i=0;i<fertigeKlassen.size(); i++){
                            fw.write(data.get(i).getHTMLListItems(i, klasse));
                            fw.flush();
                            pDialog.setProgress(i+3);
                        }
                        fw.write("      </ul>\n" +
                                "    </div>\n" +
                                "   </div>\n" +
                                "  </body>\n" +
                                "</html>");
                        fw.close();
                        pDialog.setProgress(pDialog.getMax());
                    }
                    catch(IOException ignored){}

                    pDialog.setMax(1);
                    pDialog.setMessage("Datei wird geöffnet...");

                    WebView wv = thise.findViewById(R.id.webView);
                    wv.loadUrl("file:///"+lFile.getAbsolutePath());
                    wv.getSettings().setJavaScriptEnabled(true);
                    wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

                    pDialog.hide();
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String onlyElement(String full, String element, String params) {
        String partOne = full.split("<"+element+params+">")[1];
        return partOne.split("</"+element+">")[0];
    }

    public static String onlyElement(String full, String element) {
        return onlyElement(full,element,"");
    }

    public static String onlyArgumentOfElement(String full, String element,String argument) {
        String partOne = full.split("<"+element)[1];
        String partTwo = partOne.split(argument+"=\"")[1];
        return partTwo.split("\"")[0];
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
        return true;
    }
}