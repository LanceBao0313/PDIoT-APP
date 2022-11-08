package com.specknet.pdiotapp;


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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    Button button_guest;
    Button button_login;
    EditText UNN;
    TextInputLayout Password;
    Boolean userLoginState = false;
    TextView subtitle;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        button_guest = (Button) view.findViewById(R.id.btn_guest);
        button_login = (Button) view.findViewById(R.id.btn_login);
        UNN = (EditText) view.findViewById(R.id.UNN);
        Password = (TextInputLayout) view.findViewById(R.id.et_password);
        subtitle = (TextView)  view.findViewById(R.id.tv_subtitle);



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
                String password = String.valueOf(Password.getEditText().getText());

                if(student_id.length() == 8 && student_id.charAt(0) == 's' && isDigit(StringUtils.substring(student_id,1))){
                    if (password != null){
                        JSONObject json = new JSONObject();
                        Log.i("LoginData", "UNN is:  " + student_id);
                        Log.i("LoginData", "password is:  " + password);
                        try {
                            json.put("student_id", student_id);
                            json.put("password", password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // send json to server, and wait for feedback
                        // Retrieve User Historical data

                        Boolean feedback = true;
                        if(true){
                            userLoginState = true;
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
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

    public Boolean isDigit(String a){
        for (int i=0;i<a.length();i++){
            if (!Character.isDigit(a.charAt(i))){
                return false;
            }
        }
        return true;
    }

}