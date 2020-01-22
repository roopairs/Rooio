package com.rooio.repairs;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static org.hamcrest.Matchers.not;


import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginInstrumentedTest {

    @Rule
    public IntentsTestRule<Login> intentRule = new IntentsTestRule<>(Login.class);

    @Test
    public void testLaunchActivity() {
        onView(withId(R.id.title)).check(matches(withText("Connect your account")));
        onView(withId(R.id.cancelLogin)).check(matches(withText("Cancel")));
        onView(withId(R.id.connectAccount)).check(matches(withText("Connect Account")));
    }

    @Test
    public void testEmptyUsernameAndPassword() {
        onView(withId(R.id.usernameField)).perform(typeText(""));
        onView(withId(R.id.passwordField)).perform(typeText(""));
        onView(withId(R.id.connectAccount)).perform(click());
        onView(withId(R.id.errorMessage)).check(matches(withText("Incorrect username and/or password.")));
    }

    @Test
    public void testEmptyUsername() {
        onView(withId(R.id.usernameField)).perform(typeText(""));
        onView(withId(R.id.passwordField)).perform(typeText("hacker"));
        onView(withId(R.id.connectAccount)).perform(click());
        onView(withId(R.id.errorMessage)).check(matches(withText("Incorrect username and/or password.")));
    }

    @Test
    public void testEmptyPassword() {
        onView(withId(R.id.usernameField)).perform(typeText("hacker"));
        onView(withId(R.id.passwordField)).perform(typeText(""));
        onView(withId(R.id.connectAccount)).perform(click());
        onView(withId(R.id.errorMessage)).check(matches(withText("Incorrect username and/or password.")));
    }

    @Test
    public void testCancelButton() {
        onView(withId(R.id.cancelLogin)).perform(click());
        intended(hasComponent(Landing.class.getName()));
    }
    
}
