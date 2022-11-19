package com.specknet.pdiotapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.app.Application;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.specknet.pdiotapp.utils.Constants;
import com.specknet.pdiotapp.utils.GlobalStates;
import com.specknet.pdiotapp.R;
import com.specknet.pdiotapp.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Button button_guest;
    Button button_login;
    Button login_main;
    EditText UNN;
    TextInputLayout Password;
    public boolean userLoginState = false;
    TextView subtitle;
    Context context;
    Toast toast;
    public String login_result = "false";


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        View view2 = inflater.inflate(R.layout.activity_main, container, false);
        button_guest = (Button) view.findViewById(R.id.btn_guest);
        button_login = (Button) view.findViewById(R.id.btn_login);
        login_main = (Button) view2.findViewById(R.id.loginButton);
        UNN = (EditText) view.findViewById(R.id.UNN);
        Password = (TextInputLayout) view.findViewById(R.id.et_password);
        subtitle = (TextView)  view.findViewById(R.id.tv_subtitle);
        context = getActivity();


        setupClickListeners();

        return view;
    }

    public void setupClickListeners() {
        button_guest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        button_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String student_id = String.valueOf(UNN.getText());
                String password = Password.getEditText().getText().toString();
                Log.i("LoginData", "password is:  " + password);

                if(student_id.length() == 8 && student_id.charAt(0) == 's' && isDigit(StringUtils.substring(student_id,1))){
                    if (password.length() >= 8 && password.length() <= 16){
                        JSONObject json = new JSONObject();

                        //String feedback = "false";
                        try {
                            json.put("student_id", student_id);
                            json.put("password", password.hashCode());
                            Log.i("Login result 1", login_result);
                            makePost(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send json to server, and wait for feedback
                        // Retrieve User Historical data

//                        if(login_result.equals("true") ){
//                            userLoginState = true;
//                            //GlobalStates global = (GlobalStates) getActivity();
//                            //global.setUserLoginState(true);
//
//                            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
//                            sharedPreferences.edit().putBoolean(Constants.USER_LOGIN_STATE, true).apply();
//                            sharedPreferences.edit().putString(Constants.USER_ID, student_id).apply();
//
//                            startActivity(new Intent(getActivity(), MainActivity.class));
//                            toast.makeText(context, "You are logged-in!", Toast.LENGTH_LONG).show();
//                            //toast.show();
//                        }else{
//                            subtitle.setText(getResources().getString(R.string.login_failed));
//                            subtitle.setTextColor(getResources().getColor(R.color.red));
//                        }
                    } else{
                        subtitle.setText(getResources().getString(R.string.invalid_password));
                        subtitle.setTextColor(getResources().getColor(R.color.red));
                    }

                } else{
                    subtitle.setText(getResources().getString(R.string.invalid_unn));
                    subtitle.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });


    }

    private void makePost(JSONObject json) {
        String url = "http://34.89.117.73:5000/login";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                login_result = response.toString().substring(12,16);
                //if(login_result.equals("true") ){
                userLoginState = true;
                //GlobalStates global = (GlobalStates) getActivity();
                //global.setUserLoginState(true);

                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(Constants.USER_LOGIN_STATE, true).apply();
                sharedPreferences.edit().putString(Constants.USER_ID, json.toString().substring(15,23)).apply();
                Log.i("student_id111", sharedPreferences.getString(Constants.USER_ID, ""));
                //login_main.setText(sharedPreferences.getString(Constants.USER_ID, "Login"));
                Intent intent = new Intent(getActivity(), MainActivity.class);
                //intent.putExtra("buttontxt",sharedPreferences.getString(Constants.USER_ID, "Login"));
                startActivity(intent);
                toast.makeText(context, "You are logged-in!", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                subtitle.setText(getResources().getString(R.string.login_failed));
                subtitle.setTextColor(getResources().getColor(R.color.red));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public Boolean isDigit(String a){
        for (int i=0;i<a.length();i++){
            if (!Character.isDigit(a.charAt(i))){
                return false;
            }
        }
        return true;
    }

}