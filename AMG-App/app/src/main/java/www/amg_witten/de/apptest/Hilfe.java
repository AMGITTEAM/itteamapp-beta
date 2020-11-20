package www.amg_witten.de.apptest;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class Hilfe extends AppCompatActivity
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
        methoden.onCreateFillIn(this,this,902,R.layout.hilfe);
    }

    @Override
    public void onBackPressed() {
        if(Methoden.onBackPressedFillIn(this))
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Methoden methoden = new Methoden();
        methoden.onNavigationItemSelectedFillIn(item,R.id.nav_feedback,this);
        return true;
    }

    public void regenerateVertretungsplanIcons(View view) {
        HTMLIcons.writeToFile(HTMLIcons.getTimePNGBase(),"time.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getBookPNGBase(), "book.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getGroupPNGBase(),"group.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getLightbulbPNGBase(),"lightbulb.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getUserPNGBase(),"user.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getBookEditPNGBase(),"book_edit.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getBulletErrorPNGBase(),"bullet_error.png",this);
        HTMLIcons.writeToFile(HTMLIcons.getDoorOpenPNGBase(),"door_open.png",this);
    }

    public void startFeedback(View view) {
        Intent intent = new Intent(this, Feedback.class);
        startActivity(intent);
    }
}
