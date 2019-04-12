package br.edu.fatecourinhos.findauto.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.edu.fatecourinhos.findauto.MainActivity;
import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try {
                    String idUser = new MyPreferenceManager(SplashScreenActivity.this).getUser().getId();
                    if (idUser != null) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
            }
        }.execute();
    }

}
