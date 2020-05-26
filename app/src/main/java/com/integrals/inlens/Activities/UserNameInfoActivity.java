package com.integrals.inlens.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.integrals.inlens.Helper.AppConstants;
import com.integrals.inlens.Helper.FirebaseConstants;
import com.integrals.inlens.Helper.PreOperationCheck;
import com.integrals.inlens.MainActivity;
import com.integrals.inlens.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserNameInfoActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private FirebaseAuth Auth;

    private EditText UserNameEdittext,UserEmailEdittext;
    private TextView UserNameTextview;
    private ImageButton UserNameDoneButton , MyToolbarBackButton ;
    private View MyToolbar;

    private String Name,Email;

    private ProgressBar userInfoProgressbar;

    String appTheme="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences appDataPref = getSharedPreferences(AppConstants.appDataPref, Context.MODE_PRIVATE);
        final SharedPreferences.Editor appDataPrefEditor = appDataPref.edit();
        if(appDataPref.contains(AppConstants.appDataPref_theme))
        {
            appTheme = appDataPref.getString(AppConstants.appDataPref_theme,AppConstants.themeLight);
            if(appTheme.equals(AppConstants.themeLight))
            {
                setTheme(R.style.AppTheme);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else
            {
                setTheme(R.style.DarkTheme);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        else
        {
            appTheme = AppConstants.themeLight;
            appDataPrefEditor.putString(AppConstants.appDataPref_theme,AppConstants.themeLight);
            appDataPrefEditor.commit();
            setTheme(R.style.AppTheme);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_info);

        FirebaseInit();
        CheckUserAuthentication();
        VariablesInit();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(FirebaseConstants.NAME))
                {
                    Name = dataSnapshot.child(FirebaseConstants.NAME).getValue().toString();
                    UserNameEdittext.append(Name);

                }
                if(dataSnapshot.hasChild(FirebaseConstants.EMAIL))
                {
                    Email = dataSnapshot.child(FirebaseConstants.EMAIL).getValue().toString();
                    UserEmailEdittext.append(Email);
                }
                userInfoProgressbar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                userInfoProgressbar.setVisibility(View.INVISIBLE);

            }
        });



        UserNameDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Name = UserNameEdittext.getText().toString();
                Email = UserEmailEdittext.getText().toString();

                if(!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Email) && isEmailValid(Email))
                {
                    PreOperationCheck check = new PreOperationCheck();
                    check.hideSoftKeyboard(UserNameInfoActivity.this,UserNameEdittext);


                    userRef.child("Name").setValue(Name).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                userRef.child("Email").setValue(Email);
                                startActivity(new Intent(UserNameInfoActivity.this, SplashScreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK).putStringArrayListExtra(AppConstants.USER_ID_LIST, (ArrayList<String>) new ArrayList<String>()));
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();
                            }
                            else
                            {
                                final String info = UserNameTextview.getText().toString();
                                UserNameTextview.setText(task.getResult().toString());
                                UserNameTextview.setTextColor(Color.parseColor("#ff3815"));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        UserNameTextview.setText(info);
                                        UserNameTextview.setTextColor(Color.parseColor("#cdcdcd"));

                                    }
                                },5000);
                            }
                        }
                    });
                }
                else
                {
                    if(TextUtils.isEmpty(Name) && TextUtils.isEmpty(Email))
                    {
                        showDialogMessage("Fields Missing","Please type in your username and email.");
                    }
                    else if(TextUtils.isEmpty(Name))
                    {
                        showDialogMessage("Name Missing","Please type in your username");
                    }
                    else if(TextUtils.isEmpty(Email))
                    {
                        showDialogMessage("Email Missing","Please type in your email");
                    }
                    else if(!isEmailValid(Email))
                    {
                        showDialogMessage("Invalid Email","Please type in your valid email again ");

                    }

                    else
                    {
                        showDialogMessage("Fields Missing","Please type in your username and email.");

                    }
                }



            }
        });


        findViewById(R.id.mytoolbar_info_button).setVisibility(View.GONE);

    }

    public void showDialogMessage(String title, String message) {

        int cf_bg_color,colorPrimary,red_inlens,cf_alert_dialogue_dim_bg;
        if(appTheme.equals(AppConstants.themeLight))
        {
            cf_bg_color = getResources().getColor(R.color.Light_cf_bg_color);
            colorPrimary = getResources().getColor(R.color.colorLightPrimary);
            red_inlens =  getResources().getColor(R.color.Light_red_inlens);
            cf_alert_dialogue_dim_bg = getResources().getColor(R.color.Light_cf_alert_dialogue_dim_bg);
        }
        else
        {
            cf_bg_color = getResources().getColor(R.color.Dark_cf_bg_color);
            colorPrimary = getResources().getColor(R.color.colorDarkPrimary);
            red_inlens =  getResources().getColor(R.color.Dark_red_inlens);
            cf_alert_dialogue_dim_bg = getResources().getColor(R.color.Dark_cf_alert_dialogue_dim_bg);

        }

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTitle(title)
                .setDialogBackgroundColor(cf_bg_color)
                .setTextColor(colorPrimary)
                .setIcon(R.drawable.ic_check_circle_black_24dp)
                .setMessage(message)
                .setCancelable(false)
                .addButton("OK", colorPrimary,
                        cf_alert_dialogue_dim_bg,
                        CFAlertDialog.CFAlertActionStyle.DEFAULT,
                        CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
        builder.show();
    }

    private void VariablesInit() {

        MyToolbar = findViewById(R.id.user_name_toolbar);
        MyToolbarBackButton = MyToolbar.findViewById(R.id.mytoolbar_back_button);

        MyToolbarBackButton.setVisibility(View.GONE);
        MyToolbar.findViewById(R.id.mytoolbar_dir_options).setVisibility(View.GONE);

        UserNameEdittext = findViewById(R.id.user_name_activity_edittext);
        UserNameDoneButton = findViewById(R.id.user_name_activity_done_button);
        UserNameTextview = findViewById(R.id.user_name_activity_textview);
        UserEmailEdittext= findViewById(R.id.user_email_activity_edittext);
        userInfoProgressbar = findViewById(R.id.user_email_activity_progressbar);
    }

    private void FirebaseInit() {

        Auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS).child(Auth.getCurrentUser().getUid());


    }

    private void CheckUserAuthentication() {

        if(Auth.getCurrentUser() == null)
        {
            startActivity(new Intent(UserNameInfoActivity.this, AuthActivity.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
        }

    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
