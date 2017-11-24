package pt.truewind.cronostest.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;

public class OrdemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(this, "OrdemActivity: onCreate() entrado.");
        setContentView(R.layout.activity_cotacao);
        Constants.tipoNotificacao = Constants.NOTIFICACAO_ORDEM;
        startMainActivity();
        Logger.d(this, "OrdemActivity: onCreate() finalizado.");
    }

    private void startMainActivity() {
        Logger.d(this, "OrdemActivity: startMainActivity() entrado");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        Logger.d(this, "OrdemActivity: startMainActivity() finalizado");
    }

}
