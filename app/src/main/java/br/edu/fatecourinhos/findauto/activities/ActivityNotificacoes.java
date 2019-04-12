package br.edu.fatecourinhos.findauto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;

public class ActivityNotificacoes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Switch mSwitch = (Switch) findViewById(R.id.switchNotificacoes);

        final MyPreferenceManager mPM = new MyPreferenceManager(this);
        mSwitch.setChecked(mPM.getEnableNotifications());

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPM.setEnableNotifications(isChecked);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
