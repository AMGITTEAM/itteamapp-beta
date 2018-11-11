package www.amg_witten.de.apptest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.IOException;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;

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
                final List<String> realEintraege = new ArrayList<>();
                try {

                    Authenticator.setDefault(new MyAuthenticator(context));
                    String next = "001.htm";
                    urlEndings.add(next);
                    String main = "https://www.amg-witten.de/fileadmin/VertretungsplanSUS/Heute/subst_" + next;
                    Vertretungsplan.getAllEndings(main,urlEndings);

                    Vertretungsplan.getTables(main,urlEndings,tables);

                    Vertretungsplan.getKlassenList(tables,klassen);

                    Vertretungsplan.getOnlyRealKlassenList(tables,realEintraege);

                    final List<VertretungModel> vertretungModels = new ArrayList<>();

                    List<VertretungModel> fertigeMulti = new ArrayList<>();

                    for (String s : realEintraege) {
                        Vertretungsplan.tryMatcher(s,fertigeMulti,vertretungModels);
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
                if(android.os.Build.VERSION.SDK_INT>=26){
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationChannel mChannel = new NotificationChannel("vertretung_notification_amgapp", "AMGApp-Vertretungsplan-Benachrichtigung", NotificationManager.IMPORTANCE_LOW);

                    mChannel.setDescription("Benachrichtigungs-Kanal für die Benachrichtigung des Vertretungsplans für Heute der AMGApp");

                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);

                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                    mNotificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,NotificationCompat.CATEGORY_REMINDER).setSmallIcon(R.mipmap.ic_launcher);

                mBuilder.setContentTitle("Vertretung für Klasse "+prefs.getString("klasse",""));
                mBuilder.setContentText(text);
                mBuilder.setChannelId("vertretung_notification_amgapp");

                mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                mBuilder.setContentIntent(onOpen).setAutoCancel(true).setCategory(NotificationCompat.CATEGORY_REMINDER).setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0,mBuilder.build());
            }
        }).start();


    }
}