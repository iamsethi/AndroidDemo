package com.mytaxi.android_demo;


import android.content.Context;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.mytaxi.android_demo.activities.AuthenticationActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    private static String username;
    private static String password;
    public static final String URL = "https://randomuser.me/api/?seed=a1f30d446f820665";
    private String driverName = "Sarah Scott";
    private String searchKeyword = "sa";

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public ActivityTestRule<AuthenticationActivity> activityRule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    @BeforeClass
    public static void setUserData() throws JSONException {
        JSONObject jsonObj = getJsonData();
        JSONArray results = jsonObj.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject c = results.getJSONObject(i);
            JSONObject phone = c.getJSONObject("login");
            username = phone.getString("username");
            password = phone.getString("password");

        }
    }

    @Test
    public void test1_AppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.mytaxi.android_demo", appContext.getPackageName());
    }


    @Test
    public void test2_UserLogin() {
        login();
        onView(withContentDescription(R.string.navigation_drawer_open)).perform(click());
        // Assertion checking that username displayed is correct
        onView(ViewMatchers.withId(R.id.nav_username))
                .check(matches(ViewMatchers.withText(username)));
        onView(ViewMatchers.withId(R.id.nav_view)).perform(click());
    }

    @Test
    public void test3_UserSearchDriverAndClick() {
        login();
        //Search for "sa", select the 2nd result (via the name, not the index) from the list, then click the call button.
        onView(withId(R.id.textSearch))
                .perform(typeText(searchKeyword));
        onView(withText("Sarah Scott")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        //checking the drivername
        onView(withId(R.id.textViewDriverName)).check(matches(withText(driverName)));
        onView(withId(R.id.fab))
                .perform(click()); //click the call button
    }

    public void login() {
        onView(withId(R.id.edt_username))
                .perform(typeText(username)); //enter username
        onView(withId(R.id.edt_password))
                .perform(typeText(password)); // enter password
        onView(withId(R.id.btn_login)).perform(click()); // click on Login button
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}