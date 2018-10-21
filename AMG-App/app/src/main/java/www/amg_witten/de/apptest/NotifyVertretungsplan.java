package www.amg_witten.de.apptest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class NotifyVertretungsplan extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = "";



                SharedPreferences prefs = context.getSharedPreferences("Prefs", MODE_PRIVATE);
                String klasse = prefs.getString("klasse","");

                System.out.println(klasse);
                final List<String> urlEndings = new ArrayList<>();
                List<String> tables = new ArrayList<>();
                final List<String> klassen = new ArrayList<>();
                try {

                    Authenticator.setDefault(new MyAuthenticator());
                    boolean exit = false;
                    String next = "001.htm";
                    urlEndings.add(next);
                    while (!exit) {
                        URL mainUrl = new URL("https://www.amg-witten.de/fileadmin/VertretungsplanSUS/Heute/subst_" + next);

                        BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
                        StringBuilder full = new StringBuilder();
                        String str;
                        while ((str = in.readLine()) != null) {
                            full.append(str);
                        }
                        in.close();

                        String head = Vertretungsplan.onlyElement(full.toString(), "head");
                        String contentMeta = Vertretungsplan.onlyArgumentOfElement(head, "meta http-equiv=\"refresh\"", "content");
                        System.out.println(contentMeta);
                        String nextURL = contentMeta.split("URL=subst_")[1];
                        next = nextURL;
                        if (next.equals("001.htm")) {
                            exit = true;
                        } else {
                            urlEndings.add(nextURL);
                        }
                    }

                    for (int i = 0; i < urlEndings.size(); i++) {
                        URL mainUrl = new URL("https://www.amg-witten.de/fileadmin/VertretungsplanSUS/Heute/subst_" + urlEndings.get(i));
                        BufferedReader in = new BufferedReader(new InputStreamReader(mainUrl.openStream()));
                        StringBuilder full = new StringBuilder();
                        String str;
                        while ((str = in.readLine()) != null) {
                            full.append(str);
                        }
                        in.close();

                        String body = Vertretungsplan.onlyElement(full.toString(), "body");
                        String center = Vertretungsplan.onlyElement(body, "center");
                        String table = Vertretungsplan.onlyElement(center, "table", " class=\"mon_list\" ");
                        tables.add(table);
                    }

                    for (int i = 0; i < tables.size(); i++) {
                        String[] klassenArrayUnfertig = tables.get(i).split("td class=\"list inline_header\" colspan=\"8\"");
                        for (int ie = 0; ie < klassenArrayUnfertig.length; ie++) {
                            klassenArrayUnfertig[ie] = klassenArrayUnfertig[ie].replaceFirst(">", "");
                        }
                        for (int ie = 1; ie < klassenArrayUnfertig.length; ie++) {
                            klassen.add(klassenArrayUnfertig[ie].split("</td>")[0].trim());
                        }
                    }

                    final List<String> realEintraege = new ArrayList<>();

                    for (int i = 0; i < tables.size(); i++) {
                        String[] eintraegeArrayUnfertigZwei = tables.get(i).split("tr ");
                        for (String eintraegeArrayUnfertigEin : eintraegeArrayUnfertigZwei) {
                            if (!(eintraegeArrayUnfertigEin.contains("class=\"list inline_header\"") || eintraegeArrayUnfertigEin.contains("class='list inline_header'") || eintraegeArrayUnfertigEin.contains("(Fach)"))) {
                                if (eintraegeArrayUnfertigEin.length() != 1) {
                                    realEintraege.add(eintraegeArrayUnfertigEin);
                                }
                            }
                        }
                    }

                    final List<VertretungModel> vertretungModels = new ArrayList<>();

                    List<VertretungModel> fertigeMulti = new ArrayList<>();

                    for (String s : realEintraege) {
                        Matcher matcher = Pattern.compile("<td class=\"list\"(?s)(.*?)</td>").matcher(s);

                        List<String> allMatches = new ArrayList<>();
                        while (matcher.find()) {
                            String match = matcher.group();
                            allMatches.add(match.replace("<td class=\"list\" align=\"center\">", "").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\">", "").replace("<td class=\"list\" align=\"center\" style=\"background-color: #FFFFFF\" >", "").replace("<td class=\"list\">", "").replace("</td>", "").replace("<b>", "").replace("</b>", "").replace("<span style=\"color: #800000\">", "").replace("<span style=\"color: #0000FF\">", "").replace("<span style=\"color: #010101\">", "").replace("<span style=\"color: #008040\">", "").replace("</span>", "").replace("&nbsp;", "").replaceFirst(">", ""));
                        }

                        VertretungModel model = new VertretungModel(allMatches.get(0), allMatches.get(1), allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7));


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

                        if (matcherabcd.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherabcd.group();
                                String klassengang = match.replace("abcd", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherab.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherab.group();
                                String klassengang = match.replace("ab", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherac.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherac.group();
                                String klassengang = match.replace("ac", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherad.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherad.group();
                                String klassengang = match.replace("ad", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherba.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherba.group();
                                String klassengang = match.replace("ba", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherbc.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherbc.group();
                                String klassengang = match.replace("bc", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherbd.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherbd.group();
                                String klassengang = match.replace("bd", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherca.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherca.group();
                                String klassengang = match.replace("ca", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matchercb.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matchercb.group();
                                String klassengang = match.replace("cb", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matchercd.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherca.group();
                                String klassengang = match.replace("cd", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherda.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherda.group();
                                String klassengang = match.replace("da", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherdb.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherdb.group();
                                String klassengang = match.replace("db", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherdc.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherdc.group();
                                String klassengang = match.replace("dc", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherabc.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherabc.group();
                                String klassengang = match.replace("abc", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcherabd.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcherabd.group();
                                String klassengang = match.replace("abd", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcheracb.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcheracb.group();
                                String klassengang = match.replace("acb", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcheracd.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcheracd.group();
                                String klassengang = match.replace("acd", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "c", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcheradb.matches()) {
                            boolean found = false;
                            for (VertretungModel existModel : fertigeMulti) {
                                if (existModel.toString().equals(model.toString())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                String match = matcheradb.group();
                                String klassengang = match.replace("adb", "");
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "a", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "d", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                vertretungModels.add(new VertretungModel(allMatches.get(0), klassengang + "b", allMatches.get(2), allMatches.get(3), allMatches.get(4), allMatches.get(5), allMatches.get(6), allMatches.get(7)));
                                fertigeMulti.add(model);
                            }
                        } else if (matcheradc.matches()) {
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
                        } else if (matcherbac.matches()) {
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
                        } else if (matcherbad.matches()) {
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
                        } else if (matcherbca.matches()) {
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
                        } else if (matcherbcd.matches()) {
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
                        } else if (matcherbda.matches()) {
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
                        } else if (matcherbdc.matches()) {
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
                        } else if (matchercab.matches()) {
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
                        } else if (matchercad.matches()) {
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
                        } else if (matchercba.matches()) {
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
                        } else if (matchercbd.matches()) {
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
                        } else if (matchercda.matches()) {
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
                        } else if (matchercdb.matches()) {
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
                        } else if (matcherdab.matches()) {
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
                        } else if (matcherdac.matches()) {
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
                        } else if (matcherdba.matches()) {
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
                        } else if (matcherdbc.matches()) {
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
                        } else if (matcherdca.matches()) {
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
                        } else if (matcherdcb.matches()) {
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
                        } else {
                            vertretungModels.add(model);
                        }
                    }

                    VertretungModelArrayModel data = null;
                    for(int ie=0; ie<vertretungModels.size(); ie++){
                        if(vertretungModels.get(ie).getKlasse().equals(klasse)){
                            System.out.println("TRUE");
                            int rightRowsCount = 0;
                            for(int iee=0; iee<vertretungModels.size(); iee++){
                                if(vertretungModels.get(iee).getKlasse().equals(klasse)) {
                                    rightRowsCount++;
                                }
                            }
                            VertretungModel[] rightRows = new VertretungModel[rightRowsCount];
                            rightRowsCount=0;
                            for(int iee=0; iee<vertretungModels.size(); iee++){
                                if(vertretungModels.get(iee).getKlasse().equals(klasse)){
                                    rightRows[rightRowsCount] = vertretungModels.get(iee);
                                    rightRowsCount++;
                                }
                            }
                            data = (new VertretungModelArrayModel(rightRows,klasse));
                        }
                        System.out.println(vertretungModels.get(ie).getKlasse());
                    }
                    if(data == null){
                        text = "Heute nichts!";
                    }
                    else {
                        text = "Heute ändert sich etwas.";
                    }
                }
                catch(IOException ignored){}
                Intent onOpenIntent = new Intent(context,Vertretungsplan.class);
                onOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                onOpenIntent.putExtra("Date","Heute");
                onOpenIntent.putExtra("Title","Heutiger Vertretungsplan");
                onOpenIntent.putExtra("navID",1);
                PendingIntent onOpen = PendingIntent.getActivity(context,0,onOpenIntent,0);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,NotificationCompat.CATEGORY_REMINDER).setSmallIcon(R.mipmap.ic_launcher);

                mBuilder.setContentTitle("Vertretung für Klasse "+prefs.getString("klasse",""));
                mBuilder.setContentText(text);

                mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                mBuilder.setContentIntent(onOpen).setAutoCancel(true).setCategory(NotificationCompat.CATEGORY_REMINDER).setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0,mBuilder.build());
            }
        }).start();


    }
}