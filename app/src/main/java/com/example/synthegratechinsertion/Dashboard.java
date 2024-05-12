package com.example.synthegratechinsertion;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Dashboard extends AppCompatActivity {
    String name = "", email = "", type = "",category = "select",amount = "";
    Button categoryButton, cButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("Add Expense");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
            type = bundle.getString("type");
        }
        Button b50 = findViewById(R.id.button50);
        Button b100 = findViewById(R.id.button100);
        Button b150 = findViewById(R.id.button150);
        Button b250 = findViewById(R.id.button250);
        Button b300 = findViewById(R.id.button300);
        Button setButton = findViewById(R.id.setButton);
        categoryButton = findViewById(R.id.a);
        cButton = findViewById(R.id.c);
        EditText amountField = findViewById(R.id.amountField);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        ImageButton aButton = findViewById(R.id.accountButton);
        ImageButton hButton = findViewById(R.id.homeButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                bundle.putString("type", type);
                Intent intent = new Intent(Dashboard.this, Account.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        hButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                bundle.putString("type", type);
                Intent intent = new Intent(Dashboard.this, Statistics.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountField.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL("http://capstone2024.online/snt/addexpense.php");
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setDoOutput(true);
                            String requestBody = "email=" + email + "&category=" + category + "&amount=" + amount;
                            OutputStream outputStream = urlConnection.getOutputStream();
                            outputStream.write(requestBody.getBytes());
                            outputStream.close();
                            InputStream inputStream = urlConnection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            final StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();
                            inputStream.close();
                            Log.d("Expense Addition Response", response.toString().trim());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response.toString().trim());
                                        String status = jsonResponse.getString("status");
                                        String message = jsonResponse.getString("message");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                                        builder.setTitle("Expense Addition Status");
                                        builder.setMessage(message);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                cButton.setEnabled(false);
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("JSON Parsing Error", "Failed to parse JSON response: " + e.getMessage());
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("Expense Addition Error", "Failed to add expense: " + e.getMessage());
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }
                    }
                });
                thread.start();
            }
        });
        b50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.equals("select")==false){
                    cButton.setVisibility(View.VISIBLE);
                    amountField.setText("50");
                }else{
                    amountField.setText("50");
                }
            }
        });
        b100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.equals("select")==false){
                    cButton.setVisibility(View.VISIBLE);
                    amountField.setText("100");
                }else{
                    amountField.setText("100");
                }
            }
        });
        b150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.equals("select")==false){
                    cButton.setVisibility(View.VISIBLE);
                    amountField.setText("150");
                }else{
                    amountField.setText("150");
                }
            }
        });
        b250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.equals("select")==false){
                    cButton.setVisibility(View.VISIBLE);
                    amountField.setText("250");
                }else{
                    amountField.setText("250");
                }
            }
        });
        b300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.equals("select")==false){
                    cButton.setVisibility(View.VISIBLE);
                    amountField.setText("300");
                }else{
                    amountField.setText("300");
                }
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountField.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(amountField, InputMethodManager.SHOW_IMPLICIT);
                amountField.setSelection(amountField.getText().length());
            }
        });
        amountField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(amountField.getText().equals("0")==false && category.equals("select")==false){
                    cButton.setVisibility(View.VISIBLE);
                }else{
                    cButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        LinearLayout layoutShopping = dialog.findViewById(R.id.layoutShopping);
        LinearLayout layoutEntertainment = dialog.findViewById(R.id.layoutEntertainment);
        LinearLayout layoutHome = dialog.findViewById(R.id.layoutHome);
        LinearLayout layoutSelf = dialog.findViewById(R.id.layoutSelf);

        layoutShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                category = "Shopping";
                categoryButton.setText(category);
                cButton.setVisibility(View.VISIBLE);
            }
        });
        layoutEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                category = "Entertainment";
                categoryButton.setText(category);
                cButton.setVisibility(View.VISIBLE);
            }
        });

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                category = "Home";
                categoryButton.setText(category);
                cButton.setVisibility(View.VISIBLE);
            }
        });

        layoutSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                category = "Personal";
                categoryButton.setText(category);
                cButton.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
