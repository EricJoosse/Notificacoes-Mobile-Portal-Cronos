package pt.truewind.cronostest.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;

public class CotacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(this, "CotacaoActivity: onCreate() entrado.");
        setContentView(R.layout.activity_cotacao);
        Constants.tipoNotificacao = Constants.NOTIFICACAO_COTACAO;
        startMainActivity();
        this.finish();
        Logger.d(this, "CotacaoActivity: onCreate() finalizado.");
    }

    private void startMainActivity() {
        Logger.d(this, "CotacaoActivity: startMainActivity() entrado");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        Logger.d(this, "CotacaoActivity: startMainActivity() finalizado");
    }

}
