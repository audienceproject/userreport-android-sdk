package com.audienceproject.userreport.invokers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.audienceproject.userreport.ISettingsCallback;
import com.audienceproject.userreport.ISettingsLoader;
import com.audienceproject.userreport.interfaces.ISurvey;
import com.audienceproject.userreport.interfaces.ISurveyInvoker;
import com.audienceproject.userreport.models.MediaSettings;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.rules.IInvitationRule;
import com.audienceproject.userreport.rules.LocalQuarantineInAppRule;
import com.audienceproject.userreport.rules.SessionScreensChangeInAppRule;
import com.audienceproject.userreport.rules.SessionTimeSpentInAppRule;
import com.audienceproject.userreport.rules.TotalScreensChangeInAppRule;
import com.audienceproject.userreport.rules.TotalTimeSpentInAppRule;

import java.util.ArrayList;

/**
 * This is default invoker.
 * <p>
 * This invoker will be used in case if you not provide any invoker to builder, which is in most
 * case just fine. All methods of this class work like described in documentation for ISurveyInvoker
 */
public class StandardInvoker implements ISurveyInvoker, Application.ActivityLifecycleCallbacks {

    private TotalScreensChangeInAppRule totalScreensChangeInAppRule;
    private TotalTimeSpentInAppRule totalTimeSpentInAppRule;
    private SessionScreensChangeInAppRule sessionScreensChangeInAppRule;
    private SessionTimeSpentInAppRule sessionTimeSpentInAppRule;
    private LocalQuarantineInAppRule localQuarantineInAppRule;
    private ArrayList<IInvitationRule> allRules;
    private Thread timerThread;
    private boolean alreadyInvited;
    private boolean isActive = true;
    private boolean rulesInitialized;
    private Session session;

    public StandardInvoker(Context context, ISettingsLoader settingsLoader, Session session) {
        this.allRules = new ArrayList<>();
        this.session = session;

        settingsLoader.registerSettingsLoadCallback(new ISettingsCallback() {
            @Override
            public void onSuccess(MediaSettings settings) {
                totalScreensChangeInAppRule = new TotalScreensChangeInAppRule(settings.getInviteAfterTotalScreensViewed(), session);
                allRules.add(totalScreensChangeInAppRule);

                totalTimeSpentInAppRule = new TotalTimeSpentInAppRule(settings.getInviteAfterNSecondsInApp(), session);
                allRules.add(totalTimeSpentInAppRule);

                sessionScreensChangeInAppRule = new SessionScreensChangeInAppRule(settings.getSessionScreensView(), session);
                allRules.add(sessionScreensChangeInAppRule);

                sessionTimeSpentInAppRule = new SessionTimeSpentInAppRule(settings.getSessionNSecondsLength(), session);
                allRules.add(sessionTimeSpentInAppRule);

                localQuarantineInAppRule = new LocalQuarantineInAppRule(settings.getLocalQuarantineDays(), session);
                allRules.add(localQuarantineInAppRule);

                rulesInitialized = true;
                setRuleStates();
            }

            @Override
            public void onFailed(Exception ex) {
            }
        });

        Application application = ((Application) context.getApplicationContext());
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void setSurvey(final ISurvey survey) {
        this.timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && !alreadyInvited) {
                        Log.d("Thread.", "Iteration");
                        Thread.sleep(1000);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (rulesInitialized) {
                                boolean allTriggered = true;
                                for (IInvitationRule rule : allRules) {
                                    boolean isRuleTriggered = rule.isTriggered();
                                    System.out.println(isRuleTriggered + " " + rule.getClass().getSimpleName());
                                    allTriggered = allTriggered && isRuleTriggered;
                                }

                                if (allTriggered && !alreadyInvited) {
                                    alreadyInvited = true;
                                    Log.d("StandardInvoker", "Rules triggered. Invoke survey.");

                                    // Set local quarantine
                                    session.updateLocalQuarantine(localQuarantineInAppRule.getValue());

                                    survey.tryInvite();
                                }
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        };
        this.timerThread.start();
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        this.alreadyInvited = true;
        if (this.timerThread != null) {
            this.timerThread.interrupt();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.isActive = true;
        this.setRuleStates();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        this.isActive = false;
        this.setRuleStates();
    }

    private void setRuleStates() {
        if (this.rulesInitialized) {
            if (this.isActive) {
                this.session.continueCounting();
                this.session.trackScreen();
            } else {
                this.session.stopCounting();
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
