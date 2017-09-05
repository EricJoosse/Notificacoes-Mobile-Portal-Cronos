package pt.truewind.cronostest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private ImageView loading;
    //private ProgressBar loading;
    private LinearLayout userBox;
    private LinearLayout passwordBox;
    private LinearLayout newPasswordBox;
    private LinearLayout confirmPasswordBox;

    private String server;

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SystemUtil.loadDatabase(this);
        MultiThreadDbHelper.INSTANCE.getDbHelper().open();

        Logger.d("Login");

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

        newPasswordBox.setVisibility(View.GONE);
        confirmPasswordBox.setVisibility(View.GONE);
        showLogin();

        ConfigurationService configurationService = new ConfigurationService();
        Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
        Logger.d(configuration==null?"configuration null":configuration.getValue().toString());

        if (!SystemUtil.isOnline(this)){
            Logger.d("No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
        }
        else {
            if (configuration != null) {
                if (configuration.getValue().equals(Integer.toString(Constants.AUTO_LOGIN_ENABLED))) {

                    showProgressBar();
                    EndpointService endpointService = new EndpointService();
                    Endpoint endpoint = endpointService.findEndpointById(0);
                    Logger.d(endpointService.findAll().toString());

                    if (endpoint.getUsername() != null && endpoint.getPassword() != null) {
                        setSharedPreferences(endpoint.getUsername(), endpoint.getPassword());
                        //// TODO: 12/05/2017  se null, auto login disabled e preencher login.
                    }

                    this.server = BuildConfig.ENDPOINT + Constants.LOGIN_ACCESS;
                    Logger.d(this.server);

                    new CronosPortalAuthAsyncTask(this, endpoint.getUsername(), endpoint.getPassword(), this.server,
                            new CronosPortalAuthAsyncTask.TaskListener() {

                                @Override
                                public void onFinished(Boolean result) {
                                    if (result) {
                                        Logger.i("Login Ok");
                                        startMainActivity();
                                    } else {
                                        Logger.i("Login Fail");
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showLogin();
        Logger.e("onRestart()");
        password.setText("");

        ConfigurationService configurationService = new ConfigurationService();
        Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
        Logger.d(new ConfigurationService().findAll().toString() + "dentro do onRestart()");


        if (!SystemUtil.isOnline(this)){
            Logger.d("No internet connection!");
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
                    Logger.e("Username do loggedUser é "+ this.loggedUser.getUsername());
                    Logger.e("Password do loggedUser é "+ this.loggedUser.getPassword());

                    //TODO melhorar/optimizar

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
    }

    /**
     * Validate login credentials.
     *
     * @param username Username
     * @param password Password
     * @return result from login
     */
    private void login(final String username, String password){

        if (!SystemUtil.isOnline(this)){
            Logger.d("No internet connection!");
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
            Logger.d(this.server);
            Logger.d(this.username.getText().toString());
            Logger.d(this.password.getText().toString());
            setSharedPreferences(this.loggedUser.getUsername(), this.loggedUser.getPassword());
            new CronosPortalAuthAsyncTask(this, username, password, this.server,
                    new CronosPortalAuthAsyncTask.TaskListener() {

                        @Override
                        public void onFinished(Boolean result) {
                            if (result) {
                                Logger.i(getResources().getString(R.string.login_ok));

                                Logger.d(new ConfigurationService().findAll().toString() + "dentro do metodo login");
                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.AUTO_LOGIN_ENABLED);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);
                                Logger.d(new ConfigurationService().findAll().toString() + "dentro do metodo login");
                                startMainActivity();
                            } else {
                                Logger.i(getResources().getString(R.string.login_failed));
                                ConfigurationService configurationService = new ConfigurationService();
                                Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
                                Logger.d(new ConfigurationService().findAll().toString() + "dentro do onRestart()");

                                if ( configuration != null &&
                                        configuration.getValue().equals(Integer.toString(Constants.PROVISIONAL_PASSWORD))) {

                                    askForNewPassword(username, getString(R.string.invalid_password), getString(R.string.senha_provisoria), LoginActivity.this);
                                }
                                else if ( configuration != null &&
                                        configuration.getValue().equals(Integer.toString(Constants.BLOCKED_USER)) ) {

                                    showError(getString(R.string.login_failed), getString(R.string.blocked_user));
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
    }

    /**
     * Validate provisional login credentials.
     *
     * @param username Username
     * @param password Password
     * @return result from login
     */
    private void provisionalLogin(final String username, String password){

        if (!SystemUtil.isOnline(this)){
            Logger.d("No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showLogin();
            return;
        }
        else {
            // Encode Password in MD5
            loggedUser = new User(username, password);
            this.server = BuildConfig.ENDPOINT + Constants.LOGIN_ACCESS;
            Logger.d(this.server);
            Logger.d(this.username.getText().toString());
            Logger.d(this.password.getText().toString());
            setSharedPreferences(this.loggedUser.getUsername(), this.loggedUser.getPassword());
            new CronosPortalAuthAsyncTask(this, username, password, this.server,
                    new CronosPortalAuthAsyncTask.TaskListener() {

                        @Override
                        public void onFinished(Boolean result) {
                            if (result) {
                                Logger.i(getResources().getString(R.string.login_ok));

                                Logger.d(new ConfigurationService().findAll().toString() + "dentro do metodo login provisorio");
                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.NEW_PASSWORD);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);
                                Logger.d(new ConfigurationService().findAll().toString() + "dentro do metodo login provisorio");
                                onRestart();
                            } else {
                                Logger.i(getResources().getString(R.string.login_failed));

                                askForNewPassword(username, getString(R.string.invalid_password), getString(R.string.senha_provisoria), LoginActivity.this);
                                showLogin();
                            }
                        }
                    }
            ).execute();
        }
    }

    private void setDefinitivePassword(final String username, String password, String newPassword, String confirmPassword){
        if (!SystemUtil.isOnline(this)){
            Logger.d("No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showNewPassword();
            return;
        }
        else if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            Logger.d(getString(R.string.fill_new_password));
            showError(getString(R.string.login_failed), getString(R.string.fill_new_password));
            showNewPassword();
            return;
        }
        else if ( newPassword.length() < 6) {
            Logger.d(getString(R.string.password_6_digits));
            showError(getString(R.string.login_failed), getString(R.string.password_6_digits));
            showNewPassword();
            return;
        }
        else if (!newPassword.equals(confirmPassword)) {
            Logger.d(getString(R.string.diferent_passwords));
            showError(getString(R.string.login_failed), getString(R.string.diferent_passwords));
            showNewPassword();
            return;
        }
        else {
            loggedUser = new User(username, confirmPassword);
            setSharedPreferences(this.loggedUser.getUsername(), this.loggedUser.getPassword());
            this.server = BuildConfig.ENDPOINT + Constants.SENHA_DEFINITIVA;
            Logger.d(this.server);
            Logger.d(this.username.getText().toString());
            Logger.d(this.password.getText().toString());
            Logger.d(this.newPassword.getText().toString());
            Logger.d(this.confirmPassword.getText().toString());
            new DefinitivePasswordAsyncTask(this, username, password, newPassword, confirmPassword, this.server,
                    new DefinitivePasswordAsyncTask.TaskListener(){
                        @Override
                        public void onFinished(Boolean result) {
                            if(result) {
                                Logger.i(getResources().getString(R.string.login_ok));

                                ConfigurationService configurationService = new ConfigurationService();
                                String value = Integer.toString(Constants.AUTO_LOGIN_ENABLED);
                                Configuration configuration = new Configuration(Constants.AUTO_LOGIN_KEY, value);
                                configurationService.insert(configuration);

                                startMainActivity();
                            }
                            else {
                                Logger.i(getResources().getString(R.string.login_failed));

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
    }

    private void setProvisionalPassword(String username, String password){

        if (!SystemUtil.isOnline(this)){
            Logger.d("No internet connection!");
            showError(getString(R.string.login_failed), getString(R.string.no_internet_connection));
            showLogin();
        }
        else {
            this.server = BuildConfig.ENDPOINT + Constants.SENHA_PROVISORIA;
            Logger.d(this.server);
            Logger.d(this.username.getText().toString());
            Logger.d(this.password.getText().toString());
            new ProvisionalPasswordAsyncTask(this, username, password, this.server,
                    new ProvisionalPasswordAsyncTask.TaskListener() {
                        @Override
                        public void onFinished(Boolean result) {
                            if (result) {
                                Logger.i(getString(R.string.password_to_email));
                                showNotification(getString(R.string.provisional_password), getString(R.string.password_to_email));
                                onRestart();
                            } else {
                                Logger.i(getString(R.string.error_provisional));
                                showError(getString(R.string.provisional_password), getString(R.string.error_provisional));
                                showLogin();
                            }
                        }
                    }
            ).execute();
        }
    }

    /**
     * Starts the home activity
     */
    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Set user data on Shared Preferences (Session)
     * @param username
     * @param password
     */
    private void setSharedPreferences(String username, String password){

        SharedPreferences userDetails = this.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    /**
     * Show error popup
     */
    public void showError(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
    }

    public void showNotification(String title, String message ){
        new AlertPopupDialog(this, title, message).show();
    }

    /**
     * Asks for new Password
     */
    public void askForNewPassword(final String username, String title, String message, final Activity activity){
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
    }

    /**
     * new Password for sure?
     */
    public void newPasswordforSure(final String username, String title, String message, final Activity activity){
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
    }

    public void showProgressBar(){
        loading.setVisibility(View.VISIBLE);
        userBox.setVisibility(View.GONE);
        passwordBox.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        newPasswordBox.setVisibility(View.GONE);
        confirmPasswordBox.setVisibility(View.GONE);
    }

    public void showLogin(){
        loading.setVisibility(View.GONE);
        userBox.setVisibility(View.VISIBLE);
        passwordBox.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        newPasswordBox.setVisibility(View.GONE);
        confirmPasswordBox.setVisibility(View.GONE);
    }

    public void showNewPassword(){
        newPasswordBox.setVisibility(View.VISIBLE);
        confirmPasswordBox.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        userBox.setVisibility(View.GONE);
        passwordBox.setVisibility(View.GONE);
    }
}