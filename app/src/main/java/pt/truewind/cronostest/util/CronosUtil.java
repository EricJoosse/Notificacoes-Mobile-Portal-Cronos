package pt.truewind.cronostest.util;

import pt.truewind.cronostest.activity.LoginActivity;
import pt.truewind.cronostest.constants.Constants;
import pt.truewind.cronostest.log.Logger;
import pt.truewind.cronostest.model.Configuration;
import pt.truewind.cronostest.service.local.ConfigurationService;
import pt.truewind.cronostest.util.ui.AlertPopupDialog;

/**
 * Created by vasco.caetano on 21/11/2016.
 */
public class CronosUtil {

    public static void doLogout(){
        Logger.d("LogOut");
        ConfigurationService configurationService = new ConfigurationService();
        Configuration configuration = configurationService.findConfigurationByName(Constants.AUTO_LOGIN_KEY);
        configuration.setValue(Integer.toString(Constants.AUTO_LOGIN_DISABLED));
        configurationService.insert(configuration);
    }

}
