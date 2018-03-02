package cacagdas.mynoteassistant.com;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class NoteAddActivity extends AppCompatActivity {

    Switch switchLocation;
    View mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        switchLocation = (Switch) findViewById(R.id.switchLocation);
        mapFragment = findViewById(R.id.mapFragment);
/**
        FragmentManager fm = getSupportFragmentManager();
        if (switchLocation.isChecked()){
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(mapFragment)
                    .commit();
        }
        else {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(mapFragment)
                    .commit();
        } */

        mapFragment.setVisibility(View.GONE);

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    mapFragment.setVisibility(View.VISIBLE);
                }
                else {
                    mapFragment.setVisibility(View.GONE);
                }
            }
        });
    }
}
