package com.mytaxi.android_demo;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.mytaxi.android_demo.activities.AuthenticationActivity;
import com.mytaxi.android_demo.misc.EspressoIdlingResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    private static String username;
    private static String password;
    private static final String URL = "https://randomuser.me/api/?seed=a1f30d446f820665";
    private static final String DRIVER_NAME = "Sarah Scott";
    private static final String SEARCH_KEYWORD = "sa";

    @Rule
    public ActivityTestRule<AuthenticationActivity> activityRule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    @Before
    public void setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @BeforeClass
    public static void setUserData() throws JSONException {
        JSONObject jsonObj = getJsonData();
        JSONArray results = jsonObj.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject c = results.getJSONObject(i);
            JSONObject login = c.getJSONObject("login");
            username = login.getString("username");
            password = login.getString("password");

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getApplicationContext().getPackageName()
                            + " android.permission.ACCESS_FINE_LOCATION");
        }
    }

    @Test
    public void testAppContext() {
        // Context of the app under test.
        Context appContext = getApplicationContext();
        assertEquals("com.mytaxi.android_demo", appContext.getPackageName());
    }


    @Test
    public void login_sucess() {
        login(username, password);

        // Assertion checking that username is on the MainActivity
        onView(allOf(withId(R.id.textSearch), withHint(R.string.text_hint_driver))).check(matches(isDisplayed()));

        //Open Navigation Drawer
        onView(withContentDescription(R.string.navigation_drawer_open)).perform(click());

        // Assertion checking that username displayed is correct
        onView(withId(R.id.nav_username))
                .check(matches(withText(username)));
    }

    @Test
    public void login_failure() {
        // incorrect password
        login(username, username);

        // check snackbar visibility
        onView(withText(R.string.message_login_fail))
                .check(matches(isDisplayed()));
    }


    @Test
    public void search_and_call_driver() {
        login(username, password);

        //Search for "sa", select the 2nd result (via the name, not the index)
        onView(withId(R.id.textSearch))
                .perform(typeText(SEARCH_KEYWORD));

        //clicking on 2nd search result
        onView(withText(DRIVER_NAME)).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        //checking the drivername
        onView(withId(R.id.textViewDriverName)).check(matches(withText(DRIVER_NAME)));

        //click the call button
        onView(withId(R.id.fab))
                .perform(click());
    }

    public void login(String user, String pass) {
        //enter username
        onView(withId(R.id.edt_username))
                .perform(typeText(user), closeSoftKeyboard());

        //enter password
        onView(withId(R.id.edt_password))
                .perform(typeText(pass), closeSoftKeyboard());

        // click on Login button
        onView(withId(R.id.btn_login)).perform(click());
    }

    public static JSONObject getJsonData() {
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(URL);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        } catch (Exception ex) {
            Log.e("App", "exception :: ", ex);
            return null;

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

}