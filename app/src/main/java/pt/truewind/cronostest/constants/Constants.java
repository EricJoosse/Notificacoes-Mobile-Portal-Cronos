package pt.truewind.cronostest.constants;

public class Constants {

    public static final String TAG = "--Cronos--";

    public static final String GET  = "GET";
    public static final String POST = "POST";

    public static final String CONTENT_TYPE_FORM_DATA = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APP_JSON = "application/json";
    public static final String LOGIN_ACCESS = "/v2/ControloAcesso/DoLogin";
    public static final String SwitchToMobile = "/v2/ControloAcesso/SwitchToMobile";
    //public static final String PRINCIPAL_ENDPOINT = "/principal.aspx#";
    public static final String PRINCIPAL_ENDPOINT = "/v2/#cotacao/consulta?activeTab=2";
    public static final String SECONDARY_ENDPOINT = "/v2/#cotacao/consulta?activeTab=1";
    public static final String TOKEN_ACCESS = "/v2/ControloAcesso/GetTokenDeviceidNovo";
    public static final String LOG_REMOTO = "/v2/ControloAcesso/LogMobileNovo";
    public static final String VERSAO_APK = "PCronos2.2.apk";
    public static final String LOGOUT = "/v2/ControloAcesso/Login";
    public static final String LOGIN_OK = "{\"Login\":\"OK\"}";
    public static final String CHANGE_PASSWORD_OK = "{\"Login\": \"Senha alterada com sucesso!\"}";
    public static final String NEW_PASSWORD_OK = "\"Senha\":null,\"NovaSenha\":null,\"ConfirmaSenha\":null";
    public static       String COT_ACCESS = "/v2/ControloAcesso/ValidaFornecedorPerm";
    public static       String SENHA_PROVISORIA = "/v2/ControloAcesso/SenhaProvisoria";
    public static       String SENHA_DEFINITIVA = "/v2/ControloAcesso/SenhaDefinitiva";

    public static       boolean toGerarArquivoDebug;

    public static final int NOTIFICACAO_COTACAO = 1;
    public static final int NOTIFICACAO_ORDEM = 2;
    public static       int tipoNotificacao = 2;

    //AUTOLOGIN
    public static final String AUTO_LOGIN_KEY = "autologin";
    public static final int BLOCKED_PERFIL = 5;
    public static final int BLOCKED_USER = 4;
    public static final int PROVISIONAL_PASSWORD = 3;
    public static final int NEW_PASSWORD = 2;
    public static final int AUTO_LOGIN_ENABLED = 1;
    public static final int AUTO_LOGIN_DISABLED = 0;

    static {
        if (pt.truewind.cronostest.BuildConfig.BUILD_TYPE.equals("debug"))
            toGerarArquivoDebug = true;
        else
            toGerarArquivoDebug = false;
    }
}
