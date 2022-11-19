package com.specknet.pdiotapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.specknet.pdiotapp.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    Button button_register;
    //Button button_login;
    EditText UNN;
    EditText Password;
    EditText rePassword;
    Boolean userLoginState = false;
    TextView subtitle;
    Context context;
    Toast toast = null;
    public String register_result = "false";
    //Toast toast_1 = Toast.makeText(context, "Registered Successfully!", Toast.LENGTH_SHORT);
    //Toast toast_2 = Toast.makeText(context, "404 not found!", Toast.LENGTH_SHORT);
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        View view2 = inflater.inflate(R.layout.activity_main, container, false);
        button_register = (Button) view.findViewById(R.id.btn_register);
        //button_login = (Button) view2.findViewById(R.id.loginButton);
        UNN = (EditText) view.findViewById(R.id.et_email);
        Password = (EditText) view.findViewById(R.id.et_password);
        rePassword = (EditText) view.findViewById(R.id.et_repassword);
        subtitle = (TextView)  view.findViewById(R.id.tv_subtitle);
        context = getActivity();


        setupClickListeners();

        return view;
    }

    public void setupClickListeners() {

        button_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String student_id = String.valueOf(UNN.getText());
                String password = Password.getText().toString();
                String repassword = rePassword.getText().toString();
                Log.i("LoginData", "password is:  " + password);



                if(validUNN(student_id)){
                    if (validPassword(password, repassword)){
                        JSONObject json = new JSONObject();
                        Log.i("LoginData", "UNN is:  " + student_id);
                        //String feedback = "false";


                        try {
                            json.put("student_id", student_id);
                            json.put("password", password.hashCode());
                            makePost(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send json to server, and wait for feedback
                        // Retrieve User Historical data

                    }

                } else{
                    subtitle.setText(getResources().getString(R.string.invalid_unn));
                    subtitle.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

//    private String makePost(JSONObject json) throws IOException {
//        RequestBody body = RequestBody.create(
//                MediaType.parse("application/json"), String.valueOf(json));
//
//        Request request = new Request.Builder()
//                .url("http://34.89.117.73:5000/register")
//                .post(body)
//                .build();
//
//        Call call = client.newCall(request);
//        Response response = call.execute();
//
//        if(response.code() == 200){
//            Toast.makeText(context,"register Sent to server!",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(context,"register server failed!",Toast.LENGTH_SHORT).show();
//        }
//        return response.body().toString();
//
//    }

    private void makePost(JSONObject json) {
        String url = "http://34.89.117.73:5000/register";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                register_result = response.toString().substring(12,16);
                //if(register_result.equals("true")){
                userLoginState = true;
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(Constants.USER_LOGIN_STATE, true).apply();
                sharedPreferences.edit().putString(Constants.USER_ID, json.toString().substring(15,23)).apply();
                Log.i("student_id111", sharedPreferences.getString(Constants.USER_ID, ""));
                //button_login.setText(sharedPreferences.getString(Constants.USER_ID, "Login111"));
                startActivity(new Intent(getActivity(), MainActivity.class));
                toast = Toast.makeText(context, "Registered Successfully! You are logged in!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                toast = Toast.makeText(context, "Failed: Registered ID", Toast.LENGTH_SHORT);
                toast.show();
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

    public Boolean validUNN(String unn){
        return unn.length() == 8 && unn.charAt(0) == 's' && isDigit(StringUtils.substring(unn,1));
    }

    public Boolean validPassword(String password, String repassword){
        boolean validLenght_1 = password.length() >= 8 && password.length() <= 16;
        boolean validLenght_2 = repassword.length() >= 8 && repassword.length() <= 16;
        boolean isSame = password.equals(repassword);
        boolean validChars = true;
        String invalidChars = "(){}[]|`¬¦!\"£$%^&*\"<>:;#~_-+=,@";
        for (int i=0;i<password.length();i++){
            if (invalidChars.indexOf(password.charAt(i)) != -1){
                validChars = false;
            }
        }
        if (!validLenght_1 && validLenght_2){
            subtitle.setText(getResources().getString(R.string.invalid_password));
            subtitle.setTextColor(getResources().getColor(R.color.red));
        } else if(!isSame){
            subtitle.setText(getResources().getString(R.string.password_not_match));
            subtitle.setTextColor(getResources().getColor(R.color.red));
        } else if(!validChars){
            subtitle.setText(getResources().getString(R.string.invalid_chars));
            subtitle.setTextColor(getResources().getColor(R.color.red));
        }

        return validLenght_1 && validLenght_2 && isSame && validChars;
    }

}
