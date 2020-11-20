package www.amg_witten.de.apptest;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SchwarzesBrettDetails extends AppCompatActivity
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
        methoden.onCreateFillIn(this,this,null,R.layout.schwarzes_brett_details);

        String readLine = getIntent().getStringExtra("readLine");

        ((TextView)findViewById(R.id.title)).setText(readLine.split("\nTitel: \"")[1].split("\"\nInhalt: ")[0]);
        TextView content = findViewById(R.id.content);
        String contentString = readLine.split("\nInhalt: \"")[1].split("\"\nDatum: ")[0].replaceAll("\n","<br/>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            content.setText(Html.fromHtml(contentString, Html.FROM_HTML_MODE_COMPACT));
        } else {
            content.setText(Html.fromHtml(contentString));
        }
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this, false, true))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,0,this);
        return true;
    }

}
