package www.amg_witten.de.apptest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TableLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VertretungsplanHeute extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Activity thise = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertretungsplan_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Methoden methoden = new Methoden();
        methoden.onCreateFillIn(this,this,1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                action(thise);
            }
        }).start();
    }

    public static void action(final Activity thise){
        final List<String> urlEndings = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        final List<String> klassen = new ArrayList<>();
        final File lFile = new File(Environment.getExternalStorageDirectory() + "/Download/Vertretungsplan.htm");
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
                URL mainUrl = new URL("https://www.amg-witten.de/fileadmin/VertretungsplanSUS/Heute/subst_"+next);

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
                URL mainUrl = new URL("https://www.amg-witten.de/fileadmin/VertretungsplanSUS/Heute/subst_"+urlEndings.get(i));
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
                            System.out.println("JA: "+eintraegeArrayUnfertigEin);
                        }
                    }
                    else {
                        System.out.println("NEIN: "+eintraegeArrayUnfertigEin);
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
                    allMatches.add(match.replace("<td class=\"list\" align=\"center\">","").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\">","").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" >","").replace("<td class=\"list\">","").replace("</td>","").replace("<b>","").replace("</b>","").replace("<span style=\"color: #800000\">","").replace("<span style=\"color: #0000FF\">","").replace("<span style=\"color: #010101\">","").replace("</span>","").replace("&nbsp;","").replaceFirst(">",""));
                }

                System.out.println("S: "+s);

                VertretungModel model = new VertretungModel(allMatches.get(0),allMatches.get(1),allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7), thise);

                System.out.println(i+". Model: "+model);
                Matcher matcher1 = Pattern.compile("0\\dabcd").matcher(allMatches.get(1));

                if(matcher1.matches()){
                    System.out.println("ABCD");
                    boolean found = false;
                    for (VertretungModel existModel : fertigeMulti){
                        if(existModel.toString().equals(model.toString())){
                            found=true;
                        }
                    }
                    if(!found){
                        System.out.println("NOCH NICHT AUFGENOMMEN");
                        String match = matcher1.group();
                        String klassengang = match.replace("abcd","");
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"a",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7), thise));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"b",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7), thise));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"c",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7), thise));
                        vertretungModels.add(new VertretungModel(allMatches.get(0),klassengang+"d",allMatches.get(2),allMatches.get(3),allMatches.get(4),allMatches.get(5),allMatches.get(6),allMatches.get(7), thise));
                        fertigeMulti.add(model);
                    }
                }
                else {

                    //String[] alle = StringUtils.substringsBetween(s, "<span style=\"color: #\">","</td>");
                    vertretungModels.add(model);
                    //break; //FIXME
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
                    for(int i=0; i<klassen.size(); i++){ //FIXME
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
                            //break; //FIXME
                        }
                        pDialog.setProgress(i+1);
                    }
                    pDialog.setMax(fertigeKlassen.size()+3);
                    pDialog.setMessage("Einträge werden ?????????????????????????????????????????????????????????...");
                    try {
                        FileWriter fw = new FileWriter(lFile);
                        fw.write("<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "  <head>\n" +
                                "    <title>Slider-Test</title>\n" +
                                "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
                                "    <script>\n" +
                                "$(function() {\n" +
                                "\n" +
                                "  var $navLink = $('#accordion').find('li');\n" +
                                "\n" +
                                "  $navLink.on('click', function() {\n" +
                                "    var panelToShow = $(this).data('panel-id');\n" +
                                "    var $activeLink = $('#accordion').find('.active');\n" +
                                "\n" +
                                "    // show new panel\n" +
                                "    // .stop is used to prevent the animation from repeating \n" +
                                "    // if you keep clicking the same link\n" +
                                "    $('.' + panelToShow).stop().slideDown();\n" +
                                "    $('.' + panelToShow).addClass('active');\n" +
                                "\n" +
                                "\n" +
                                "    // hide the previous panel \n" +
                                "    $activeLink.stop().slideUp()\n" +
                                "    .removeClass('active');\n" +
                                "\n" +
                                "  });\n" +
                                "\n" +
                                "});\n" +
                                "    </script>");
                        fw.flush();
                        pDialog.setProgress(1);
                        fw.write("\t\t<style>\n" +
                                "#accordion ul {\n" +
                                "  text-align: center;\n" +
                                "  margin: 0;\n" +
                                /*"  width: 80%;\n" +
                                "  margin: 150px auto;\n" +
                                "  height: 50%;\n" +*/ //TODO Papa fragen
                                "}\n" +
                                "\n" +
                                "\n" +
                                "#accordion ul {\n" +
                                "  text-align: center;\n" +
                                "  margin: 0;\n" +
                                "}\n" +
                                "#accordion ul li {\n" +
                                "  list-style-type: none;\n" +
                                "  cursor: default;\n" +
                                "  padding: 0.4em;\n" +
                                "  font-size: 1.4em;\n" +
                                "  color: black;\n" +
                                "  letter-spacing: 0.07em;\n" +
                                "  transition: 0.3s ease all;\n" +
                                "  text-shadow: -1px 0 grey, 0 1px grey, 1px 0 grey, 0 -1px grey;\n" +
                                "}\n" +
                                "\n" +
                                "\n" +
                                "table {\n" +
                                "    border-collapse: collapse;\n" +
                                "}\n" +
                                "\n" +
                                "table, td, th {\n" +
                                "    border: 1px solid black;\n" +
                                "}\n" +
                                "#accordion ul li { background-color: #3498db; }" +
                                "\n" +
                                "\n" +
                                "#accordion ul li:hover { color: #ccc; }\n" +
                                "\n" +
                                "#accordion ul a { color: #333; }\n" +
                                "\n" +
                                ".panel {\n" +
                                "  display: none;\n" +
                                //"  padding: 25px;\n" +
                                //"  margin: 10px;\n" +
                                "  font-family: \"roboto\", sans-serif;\n" +
                                //"  padding: 0.4em;\n" +
                                "  font-size: 1.0em;\n" + //1.4
                                "  color: white;\n" +
                                //"  letter-spacing: 0.2em;\n" +
                                "  background-color: white;\n" +
                                "  color: #333;\n" +
                                "}\n" +
                                "@media (min-width: 400px) {\n" +
                                "\n" +
                                "#accordion {\n" +
                                "  width: 100vw;\n" +
                                "  height: 100vh;\n" +
                                "  margin: 10px 0 0 0;\n" +
                                "}\n" +
                                "}\n" +
                                "@media (max-width: 399px) {\n" +
                                "\n" +
                                "#accordion {\n" +
                                "  width: 100vw;\n" +
                                "  height: 100vh;\n" +
                                "  margin: 10px 0 0 -10px;\n" +
                                "}\n" +
                                "}\n" +
                                "    </style>");
                        fw.flush();
                        pDialog.setProgress(2);
                        fw.write("\t</head>\n" +
                                "\t<body>\n" +
                                "  <div class=\"container\">\n" +
                                "    <div id=\"accordion\">\n" +
                                "      <ul class=\"panels\" style=\"margin-left: -10%;\">\n");
                        fw.flush();
                        for(int i=0;i<fertigeKlassen.size(); i++){
                            fw.write(data.get(i).getHTMLListItems(i));
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

                    WebView wv = (WebView)thise.findViewById(R.id.webView);
                    wv.loadUrl("file:///"+lFile.getAbsolutePath());
                    wv.getSettings().setJavaScriptEnabled(true);
                    wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

                    pDialog.hide();
                    System.out.println("FERTIG");
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        Methoden methoden = new Methoden();
        return methoden.onNavigationItemSelectedFillIn(item,R.id.nav_login,this);
    }
}