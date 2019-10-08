package pt.truewind.cronostest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import pt.truewind.cronostest.BuildConfig;
import pt.truewind.cronostest.R;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.model.User;
import pt.truewind.cronostest.persistence.sqlite.MultiThreadDbHelper;
import pt.truewind.cronostest.service.local.ConfigurationService;
import pt.truewind.cronostest.service.local.EndpointService;
import pt.truewind.cronostest.task.CronosPortalAuthAsyncTask;
import pt.truewind.cronostest.task.DefinitivePasswordAsyncTask;
import pt.truewind.cronostest.task.ProvisionalPasswordAsyncTask;
import pt.truewind.cronostest.util.system.SystemUtil;
import pt.truewind.cronostest.util.ui.AlertPopupDialog;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button loginButton;
    private TextView esqueceuSenhaLink;
    private ImageView loading;
    //private ProgressBar loading;
    private LinearLayout userBox;
    private LinearLayout passwordBox;
    private LinearLayout newPasswordBox;
    private LinearLayout confirmPasswordBox;

    private String server;

    private User loggedUser;

    @Override
    public void onBackPressed() {

        // Finish doesn't close the app, it just closes the activity.
        // If this is the launcher activity, then it will close your app;
        // if not, it will go back to the previous activity
        // Esta tela Login é a "launcher activity" (definido no manifest)

        // Foi testadao que o botão "Voltar" de Android, com o usuário posicionado na tela de Login,
        // com "this.finish()" realmente volta para o aplicativo anterior (se tiver),
        // ou volta para a tela principal do celular (se não tiver)).
        // Obs.: o caso do aplicativo anterior apenas pode acontece quando nosso app for iniciado pela notificação (externa).

//      finishAffinity();       // Volta para a activity anterior, fechando  também a mesma activity em outros apps, se tiver
        this.finish();          // Volta para a activity anterior
//      super.onBackPressed();  // Volta para a activity anterior
    }


    // O seguinte serve apenas para Android < 2.0:
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        // Desabilitar o botão Voltar de Android pois isso está tratado dentro do web site:
//        return (keyCode == KeyEvent.KEYCODE_BACK ? true : super.onKeyDown(keyCode, event));
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(this, "LoginActivity: onCreate entrado.");
        setContentView(R.layout.activity_login);
        SystemUtil.loadDatabase(this);
        MultiThreadDbHelper.INSTANCE.getDbHelper().open();

        Logger.d(this, "Login");

        this.username = (EditText) findViewById(R.id.username);
        this.password = (EditText) findViewById(R.id.password);
        this.newPassword = (EditText) findViewById(R.id.newPassword);
        this.confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.loading = (ImageView) findViewById(R.id.loading);
        Glide.with(this).load(R.mipmap.loading1).into(loading);
        //this.loading = (ProgressBar) findViewById(R.id.progressBar);
        this.userBox = (LinearLayout) findViewById(R.id.userBox);
        this.passwordBox = (LinearLayout) findViewById(R.id.pswdBox);
        this.newPasswordBox = (LinearLayout) findViewById(R.id.newPswdBox);
        this.confirmPasswordBox = (LinearLayout) findViewById(R.id.confirmPswdBox);
        this.esqueceuSenhaLink = (TextView) findViewById(R.id.esqueceuSenhaLink);
        this.esqueceuSenhaLink.setPaintFlags(this.esqueceuSenhaLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        newPasswordBox.setVisibility(View.GONE);
        confirmPasswordBox.setVisibility(View.GONE);
        showLogin();

        ConfigurationService configurationService = new ConfigurationService();
        Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
        Logger.d(this, configuration==null?"configuration null":configuration.getValue().toString());

        if (!SystemUtil.isOnline(this)){
            Logger.d(this, "LoginActivity: No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
        }
        else {
            if (configuration != null) {
                if (configuration.getValue().equals(Integer.toString(Constants.AUTO_LOGIN_ENABLED))) {

                    showProgressBar();
                    EndpointService endpointService = new EndpointService();
                    Endpoint endpoint = endpointService.findEndpointById(0);
                    Logger.d(this, endpointService.findAll().toString());

                    if (endpoint.getUsername() != null && endpoint.getPassword() != null) {
                        setSharedPreferences(endpoint.getUsername(), endpoint.getPassword());
                        //// TODO: 12/05/2017  se null, auto login disabled e preencher login. - 03/10/2017: já estava resolvido antes do upload inicial no GitHub, segundo Mário Viegas
                    }

                    this.server = BuildConfig.ENDPOINT + Constants.LOGIN_ACCESS;
                    Logger.d(this, this.server);

                    new CronosPortalAuthAsyncTask(this, endpoint.getUsername(), endpoint.getPassword(), this.server,
                            new CronosPortalAuthAsyncTask.TaskListener() {

                                @Override
                                public void onFinished(Boolean result) {
                                    if (result) {
                                        Logger.i(null, "Login Ok");
                                        startMainActivity();
                                    } else {
                                        Logger.i(null, "Login Fail");
                                        showError(getResources().getString(R.string.login_failed), getString(R.string.login_failed_message));
                                        showLogin();
                                    }
                                }
                            }
                    ).execute();
                }
            }
        }

        if ( loginButton != null ){
            // Login by Enter in login button.
            if ( configuration != null &&
                    ((configuration.getValue().equals(Integer.toString(Constants.PROVISIONAL_PASSWORD)))
                            || ((configuration.getValue().equals(Integer.toString(Constants.NEW_PASSWORD))))
                            || ((configuration.getValue().equals(Integer.toString(Constants.BLOCKED_USER)))))) {
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar();
                        provisionalLogin(username.getText().toString(), password.getText().toString());
                    }
                });
            }
            else{
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressBar();
                        login(username.getText().toString(), password.getText().toString());
                    }
                });
            }
        }

        if (esqueceuSenhaLink != null)
        {
            esqueceuSenhaLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validarEsqueceuSenha(username.getText().toString(), "", getString(R.string.certeza_provisoria), LoginActivity.this);
                }
            });
        }

        Logger.d(this, "LoginActivity: onCreate finalizado.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d(this, "LoginActivity: onRestart() entrado");
        showLogin();
        password.setText("");

        ConfigurationService configurationService = new ConfigurationService();
        Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
        Logger.d(this, new ConfigurationService().findAll().toString() + "dentro do onRestart()");


        if (!SystemUtil.isOnline(this)){
            Logger.d(this, "LoginActivity: No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
        }
        else{
            if (configuration != null) {
                if (configuration.getValue().equals(Integer.toString(Constants.PROVISIONAL_PASSWORD))
                        || configuration.getValue().equals(Integer.toString(Constants.BLOCKED_USER))) {
                    if ( loginButton != null ){
                        // Login by Enter in login button.
                        loginButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showProgressBar();
                                provisionalLogin(username.getText().toString(), password.getText().toString());
                            }
                        });
                    }
                }
                else if(configuration.getValue().equals(Integer.toString(Constants.NEW_PASSWORD))){

                    showNewPassword();
                    Logger.e(null, "Username do loggedUser é "+ this.loggedUser.getUsername());
                    Logger.e(null, "Password do loggedUser é "+ this.loggedUser.getPassword());

                    //TODO melhorar/optimizar - 03/10/2017: já estava resolvido antes do upload inicial no GitHub, segundo Mário Viegas

                    final String user = this.loggedUser.getUsername();
                    final String pass = this.loggedUser.getPassword();

                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgressBar();
                            setDefinitivePassword(user, pass, newPassword.getText().toString(), confirmPassword.getText().toString());
                        }
                    });
                }
            }
        }
        Logger.d(this, "LoginActivity: onRestart() finalizado");
    }

    /**
     * Validate login credentials.
     *
     * @param username Username
     * @param password Password
     * @return result from login
     */
    private void login(final String username, String password){

        Logger.d(this, "LoginActivity: login() entrado");

        if (!SystemUtil.isOnline(this)){
            Logger.d(this, "LoginActivity: No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showLogin();
            return;
        }
        else if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
            showError(getString(R.string.login_failed), getString(R.string.fill_login_fields));
            showLogin();
            return;
        }
        else if (username == null || username.isEmpty()) {
            showError(getString(R.string.login_failed), getString(R.string.fill_username));
            showLogin();
            return;
        }
        else if (password == null || password.isEmpty()) {
            showError(getString(R.string.login_failed), getString(R.string.fill_password));
            showLogin();
            return;
        }
        else {
            // Encode Password in MD5
            loggedUser = new User(username, password);
            this.server = BuildConfig.ENDPOINT + Constants.LOGIN_ACCESS;
            Logger.d(this, this.server);
            Logger.d(this, this.username.getText().toString());
            Logger.d(this, this.password.getText().toString());
            setSharedPreferences(this.loggedUser.getUsername(), this.loggedUser.getPassword());
            new CronosPortalAuthAsyncTask(this, username, password, this.server,
                    new CronosPortalAuthAsyncTask.TaskListener() {

                        @Override
                        public void onFinished(Boolean result) {
                            if (result) {
                                Logger.i(null, getResources().getString(R.string.login_ok));

                                Logger.d(null, new ConfigurationService().findAll().toString() + "dentro do metodo login");
                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.AUTO_LOGIN_ENABLED);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);
                                Logger.d(null, new ConfigurationService().findAll().toString() + "dentro do metodo login");
                                startMainActivity();
                            } else {
                                Logger.i(null, getResources().getString(R.string.login_failed));
                                ConfigurationService configurationService = new ConfigurationService();
                                Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
                                Logger.d(null, new ConfigurationService().findAll().toString() + "dentro do onRestart()");

                                if ( configuration != null &&
                                        configuration.getValue().equals(Integer.toString(Constants.PROVISIONAL_PASSWORD))) {

                                    askForNewPassword(username, getString(R.string.invalid_password), getString(R.string.senha_provisoria), LoginActivity.this);
                                }
                                else if ( configuration != null &&
                                        configuration.getValue().equals(Integer.toString(Constants.BLOCKED_USER)) ) {

                                    showError(getString(R.string.login_failed), getString(R.string.blocked_user));
                                    onRestart();
                                }
                                else if ( configuration != null &&
                                        configuration.getValue().equals(Integer.toString(Constants.BLOCKED_PERFIL)) ) {

                                    showError(getString(R.string.login_failed), getString(R.string.blocked_perfil));
                                    onRestart();
                                }
                                else {
                                    showError(getString(R.string.login_failed), getString(R.string.invalid_password));
                                }

                                showLogin();
                            }
                        }
                    }
            ).execute();
        }
        Logger.d(this, "LoginActivity: login() finalizado");
    }

    /**
     * Validate provisional login credentials.
     *
     * @param username Username
     * @param password Password
     * @return result from login
     */
    private void provisionalLogin(final String username, String password){
        Logger.d(this, "LoginActivity: provisionalLogin() entrado");

        if (!SystemUtil.isOnline(this)){
            Logger.d(this, "LoginActivity: No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showLogin();
            return;
        }
        else {
            // Encode Password in MD5
            loggedUser = new User(username, password);
            this.server = BuildConfig.ENDPOINT + Constants.LOGIN_ACCESS;
            Logger.d(this, this.server);
            Logger.d(this, this.username.getText().toString());
            Logger.d(this, this.password.getText().toString());
            setSharedPreferences(this.loggedUser.getUsername(), this.loggedUser.getPassword());
            new CronosPortalAuthAsyncTask(this, username, password, this.server,
                    new CronosPortalAuthAsyncTask.TaskListener() {

                        @Override
                        public void onFinished(Boolean result) {
                            if (result) {
                                Logger.i(null, getResources().getString(R.string.login_ok));

                                Logger.d(null, new ConfigurationService().findAll().toString() + "dentro do metodo login provisorio");
                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.NEW_PASSWORD);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);
                                Logger.d(null, new ConfigurationService().findAll().toString() + "dentro do metodo login provisorio");
                                onRestart();
                            } else {
                                Logger.i(null, getResources().getString(R.string.login_failed));

                                askForNewPassword(username, getString(R.string.invalid_password), getString(R.string.senha_provisoria), LoginActivity.this);
                                showLogin();
                            }
                        }
                    }
            ).execute();
        }
        Logger.d(this, "LoginActivity: provisionalLogin() finalizado");
    }

    private void setDefinitivePassword(final String username, String password, String newPassword, String confirmPassword){
        Logger.d(this, "LoginActivity: setDefinitivePassword() entrado");

        if (!SystemUtil.isOnline(this)){
            Logger.d(this, "LoginActivity: No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showNewPassword();
            return;
        }
        else if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            Logger.d(this, getString(R.string.fill_new_password));
            showError(getString(R.string.login_failed), getString(R.string.fill_new_password));
            showNewPassword();
            return;
        }
        else if ( newPassword.length() < 6) {
            Logger.d(this, getString(R.string.password_6_digits));
            showError(getString(R.string.login_failed), getString(R.string.password_6_digits));
            showNewPassword();
            return;
        }
        else if (!newPassword.equals(confirmPassword)) {
            Logger.d(this, getString(R.string.diferent_passwords));
            showError(getString(R.string.login_failed), getString(R.string.diferent_passwords));
            showNewPassword();
            return;
        }
        else {
            loggedUser = new User(username, confirmPassword);
            setSharedPreferences(this.loggedUser.getUsername(), this.loggedUser.getPassword());
            this.server = BuildConfig.ENDPOINT + Constants.SENHA_DEFINITIVA;
            Logger.d(this, this.server);
            Logger.d(this, this.username.getText().toString());
            Logger.d(this, this.password.getText().toString());
            Logger.d(this, this.newPassword.getText().toString());
            Logger.d(this, this.confirmPassword.getText().toString());
            new DefinitivePasswordAsyncTask(this, username, password, newPassword, confirmPassword, this.server,
                    new DefinitivePasswordAsyncTask.TaskListener(){
                        @Override
                        public void onFinished(Boolean result) {
                            if(result) {
                                Logger.i(null, getResources().getString(R.string.login_ok));

                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.AUTO_LOGIN_ENABLED);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);

                                startMainActivity();
                            }
                            else {
                                Logger.i(null, getResources().getString(R.string.login_failed));

                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.PROVISIONAL_PASSWORD);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);
                                askForNewPassword(username, getString(R.string.invalid_password), getString(R.string.senha_provisoria), LoginActivity.this);
                                showLogin();
                            }
                        }
                    }
            ).execute();
        }
        Logger.d(this, "LoginActivity: setDefinitivePassword() finalizado");
    }

    private void setProvisionalPassword(String username, String password){
        Logger.d(this, "LoginActivity: setProvisionalPassword() entrado");

        if (!SystemUtil.isOnline(this)){
            Logger.d(this, "LoginActivity: No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showLogin();
        }
        else {
            this.server = BuildConfig.ENDPOINT + Constants.SENHA_PROVISORIA;
            Logger.d(this, this.server);
            Logger.d(this, this.username.getText().toString());
            Logger.d(this, this.password.getText().toString());
            new ProvisionalPasswordAsyncTask(this, username, password, this.server,
                    new ProvisionalPasswordAsyncTask.TaskListener() {
                        @Override
                        public void onFinished(Boolean result) {
                            if (result) {
                                Logger.i(null, getString(R.string.password_to_email));
                                showNotification(getString(R.string.provisional_password), getString(R.string.password_to_email));
                                onRestart();
                            } else {
                                Logger.i(null, getString(R.string.error_provisional));
                                showError(getString(R.string.provisional_password), getString(R.string.error_provisional));
                                showLogin();
                            }
                        }
                    }
            ).execute();
        }
        Logger.d(this, "LoginActivity: setProvisionalPassword() finalizado");
    }

    /**
     * Starts the home activity
     */
    private void startMainActivity(){
        Logger.d(this, "LoginActivity: startMainActivity() entrado");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();

        Logger.d(this, "LoginActivity: startMainActivity() finalizado");
    }

    /**
     * Set user data on Shared Preferences (Session)
     * @param username
     * @param password
     */
    private void setSharedPreferences(String username, String password){
        Logger.d(this, "LoginActivity: setSharedPreferences() entrado");

        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

        Logger.d(this, "LoginActivity: setSharedPreferences() finalizado");
    }

    /**
     * Show error popup
     */
    public void showError(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
        Logger.d(this, "LoginActivity: showError(): message = " + message);
    }

    public void showNotification(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
        Logger.d(this, "LoginActivity: showNotification(): message = " + message);
    }

    /**
     * Asks for new Password
     */
    public void askForNewPassword(final String username, String title, String message, final Activity activity){
        Logger.d(this, "LoginActivity: askForNewPassword() entrado");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ConfigurationService configurationService = new ConfigurationService();
                Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
                if (configuration != null) {
                    configuration.setValue(Integer.toString(Constants.AUTO_LOGIN_DISABLED));
                    configurationService.insert(configuration);
                }
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newPasswordforSure(username, "", getString(R.string.certeza_provisoria), LoginActivity.this);
            }
        });

        builder.setTitle(title);
        builder.setMessage(message);

        AlertDialog popup = builder.create();
        popup.show();

        Logger.d(this, "LoginActivity: askForNewPassword() finalizado");
    }

    /**
     * new Password for sure?
     */
    public void newPasswordforSure(final String username, String title, String message, final Activity activity){
        Logger.d(this, "LoginActivity: newPasswordforSure() entrado");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConfigurationService configurationService = new ConfigurationService();
                String value = Integer.toString(Constants.PROVISIONAL_PASSWORD);
                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                configurationService.insert(configuration);
                setProvisionalPassword(username, "teste");
            }
        });

        builder.setTitle(title);
        builder.setMessage(message);

        AlertDialog popup = builder.create();
        popup.show();

        Logger.d(this, "LoginActivity: newPasswordforSure() finalizado");
    }

    public void validarEsqueceuSenha(final String username, String title, String message, final Activity activity) {
        if (!SystemUtil.isOnline(this)) {
            Logger.d(this, "LoginActivity - esqueceuSenhaLink.onClick(): No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showLogin();
            return;
        }
        else if (username == null || username.isEmpty()) {
            showError(getString(R.string.login_failed), getString(R.string.fill_username));
            showLogin();
            return;
        }
        else {
            showProgressBar();
            newPasswordforSure(username, title, message, activity);
        }
    }

    public void showProgressBar(){
        loading.setVisibility(View.VISIBLE);
        userBox.setVisibility(View.GONE);
        passwordBox.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        esqueceuSenhaLink.setVisibility(View.GONE);
        newPasswordBox.setVisibility(View.GONE);
        confirmPasswordBox.setVisibility(View.GONE);
    }

    public void showLogin(){

        // Tentativa para resolver o bug em celulares Android 9.0: não está sincronizando as teclas do teclado
        // com o conteúdo dos campos "Usuário" e "Senha" após a digitação de cada tecla.
        // Nos outros campos "Nova Senha" e "Confirmar Senha" funciona.
        // Testei no emulador Android que este bug não acontece com Android 7 e 8.
        // Até a seguinte gambiarra não deu certo:
        if (Build.VERSION.SDK_INT == 28) {
            userBox.setVisibility(View.GONE);
            passwordBox.setVisibility(View.GONE);
        }

        loading.setVisibility(View.GONE);
        userBox.setVisibility(View.VISIBLE);
        passwordBox.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        esqueceuSenhaLink.setVisibility(View.VISIBLE);
        newPasswordBox.setVisibility(View.GONE);
        confirmPasswordBox.setVisibility(View.GONE);
    }

    public void showNewPassword(){
        newPasswordBox.setVisibility(View.VISIBLE);
        confirmPasswordBox.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        esqueceuSenhaLink.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        userBox.setVisibility(View.GONE);
        passwordBox.setVisibility(View.GONE);
    }
}