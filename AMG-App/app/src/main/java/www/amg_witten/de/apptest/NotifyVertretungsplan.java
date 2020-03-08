package www.amg_witten.de.apptest;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Calendar;
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
                    String main = "https://sus.amg-witten.de/Heute/subst_" + next;
                    Vertretungsplan.getAllEndings(main,urlEndings);

                    String[] stands = Vertretungsplan.getTables(main,urlEndings,tables);

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
                    }
                    if(!prefs.getString("lastRefresh","").equals(stands[0])){
                        if(data == null){
                            text = context.getString(R.string.notify_vertretungsplan_nichts);
                        }
                        else {
                            text = context.getString(R.string.notify_vertretungsplan_etwas);
                        }

                    }
                    else {
                        Calendar currTime = Calendar.getInstance();
                        int hour = currTime.get(Calendar.HOUR_OF_DAY);
                        if (hour == 7) {
                            Intent alarmIntent = new Intent(context, NotifyVertretungsplan.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, currTime.get(Calendar.HOUR_OF_DAY));
                            calendar.set(Calendar.MINUTE, currTime.get(Calendar.MINUTE)+5);
                            calendar.set(Calendar.SECOND, 1);

                            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, pendingIntent);
                        }
                    }

                    prefs.edit().putString("lastRefresh",stands[0]).apply();
                }
                catch(IOException ignored){}
                Intent onOpenIntent = new Intent(context,Vertretungsplan.class);
                onOpenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                onOpenIntent.putExtra("Date","Heute");
                onOpenIntent.putExtra("Title",context.getString(R.string.vertretungsplan_title_heute));
                onOpenIntent.putExtra("navID",1);
                PendingIntent onOpen = PendingIntent.getActivity(context,0,onOpenIntent,0);
                if(android.os.Build.VERSION.SDK_INT>=26){
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationChannel mChannel = new NotificationChannel("vertretung_notification_amgapp", context.getString(R.string.notify_vertretungsplan_channel_title), NotificationManager.IMPORTANCE_LOW);

                    mChannel.setDescription(context.getString(R.string.notify_vertretungsplan_channel_description));

                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);

                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                    mNotificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,NotificationCompat.CATEGORY_REMINDER).setSmallIcon(R.mipmap.ic_launcher);

                mBuilder.setContentTitle(context.getString(R.string.notify_vertretungsplan_title,prefs.getString("klasse","")));
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