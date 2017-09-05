package pt.truewind.cronostest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.truewind.cronostest.activity.LoginActivity;
import pt.truewind.cronostest.model.Endpoint;
import pt.truewind.cronostest.persistence.DBHelper;
import pt.truewind.cronostest.persistence.sqlite.MultiThreadDbHelper;
import pt.truewind.cronostest.service.local.EndpointService;
import pt.truewind.cronostest.util.system.SystemUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by mario.viegas on 08/11/2016.
 */

public class DataBaseTest {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void insertTest() throws Exception {

        //SystemUtil.loadDatabase(context);

        SystemUtil.loadDatabase(context);

        EndpointService endpointService = new EndpointService();
        String token = "token";
        String username = "username";
        Endpoint endpoint = new Endpoint(token);
        endpoint.setUsername(username);

        endpointService.insert(endpoint);

        Integer countEndpoints = endpointService.countEndpoints();

        assertEquals(countEndpoints.intValue(), 1);
    }
}
