package www.amg_witten.de.apptest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

class VertretungModel {

    private String Stunde;
    private String Klasse;
    private String Art;
    private String Fach;
    private String ErsatzFach;
    private String Vertretungslehrer;
    private String Raum;
    private String Hinweise;

    private Context context;

    VertretungModel(String Stunde, String Klasse, String Art, String Fach, String ErsatzFach, String Vertretungslehrer, String Raum, String Hinweise, Context context){
        this.Stunde = Stunde;
        this.Klasse = Klasse;
        this.Art = Art;
        this.Fach = Fach;
        this.ErsatzFach = ErsatzFach;
        this.Vertretungslehrer = Vertretungslehrer;
        this.Raum = Raum;
        this.Hinweise = Hinweise;
        this.context = context;
    }

    TableRow getTableRow (){
        TableRow row = new TableRow(context);
        TextView stunde = new TextView(context);
        TextView klasse = new TextView(context);
        TextView art = new TextView(context);
        TextView fach = new TextView(context);
        TextView ersatzFach = new TextView(context);
        TextView vertretungslehrer = new TextView(context);
        TextView raum = new TextView(context);
        TextView hinweise = new TextView(context);

        stunde.setText(Stunde);
        klasse.setText(Klasse);
        art.setText(Art);
        fach.setText(Fach);
        ersatzFach.setText(ErsatzFach);
        vertretungslehrer.setText(Vertretungslehrer);
        raum.setText(Raum);
        hinweise.setText(Hinweise);

        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setColor(Color.BLACK);

        stunde.setBackground(border);
        klasse.setBackground(border);
        art.setBackground(border);
        fach.setBackground(border);
        ersatzFach.setBackground(border);
        vertretungslehrer.setBackground(border);
        raum.setBackground(border);
        hinweise.setBackground(border);

        stunde.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        klasse.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        art.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
        fach.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        ersatzFach.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        vertretungslehrer.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        raum.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        hinweise.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));

        stunde.setGravity(Gravity.CENTER);
        klasse.setGravity(Gravity.CENTER);
        art.setGravity(Gravity.CENTER);
        fach.setGravity(Gravity.CENTER);
        ersatzFach.setGravity(Gravity.CENTER);
        vertretungslehrer.setGravity(Gravity.CENTER);
        raum.setGravity(Gravity.CENTER);
        hinweise.setGravity(Gravity.CENTER);

        row.addView(stunde);
        row.addView(klasse);
        row.addView(art);
        row.addView(fach);
        row.addView(ersatzFach);
        row.addView(vertretungslehrer);
        row.addView(raum);
        row.addView(hinweise);
        return row;
    }

    String getStunde(){
        return Stunde;
    }

    String getKlasse(){
        return Klasse;
    }

    String getArt(){
        return Art;
    }

    String getFach(){
        return Fach;
    }

    String getErsatzFach(){
        return ErsatzFach;
    }

    String getVertretungslehrer(){
        return Vertretungslehrer;
    }

    String getRaum(){
        return Raum;
    }

    String getHinweise(){
        return Hinweise;
    }

    public String toString(){
        return "Stunde: "+Stunde+"; Klasse: "+Klasse+"; Art: "+Art+"; Fach: "+Fach+"; Ersatzfach: "+ErsatzFach+"; Vertretungslehrer: "+Vertretungslehrer+"; Raum: "+Raum+"; Hinweise: "+Hinweise;
    }

}
