package com.specknet.pdiotapp;


import android.content.Context;
import android.content.Intent;
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

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    Button button_register;
    EditText UNN;
    EditText Password;
    EditText rePassword;
    Boolean userLoginState = false;
    TextView subtitle;
    Context context;
    Toast toast = null;
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
        button_register = (Button) view.findViewById(R.id.btn_register);
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

                        try {
                            json.put("student_id", student_id);
                            json.put("password", password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send json to server, and wait for feedback
                        // Retrieve User Historical data

                        Boolean feedback = false;
                        if(feedback){
                            userLoginState = true;
                            startActivity(new Intent(getActivity(), MainActivity.class));

                            toast = Toast.makeText(context, "Registered Successfully!", Toast.LENGTH_SHORT);
                            //toast.cancel();
                            toast.show();
                        }else{

                            toast = Toast.makeText(context, "404 not found!", Toast.LENGTH_SHORT);
                            //toast.cancel();
                            toast.show();
                        }
                    }

                } else{
                    subtitle.setText(getResources().getString(R.string.invalid_unn));
                    subtitle.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
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
