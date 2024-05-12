package com.example.synthegratechinsertion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Account extends AppCompatActivity {
    String name = "", email = "", type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setTitle("Account Details");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
            type = bundle.getString("type");
        }
        Button deleteButton = findViewById(R.id.button2);
        Button logoutButton = findViewById(R.id.button);
        EditText nameField = findViewById(R.id.editText);
        EditText emailField = findViewById(R.id.editText3);
        nameField.setText(name);
        emailField.setText(email);
        ImageButton dButton = findViewById(R.id.homeButton);
        ImageButton sButton = findViewById(R.id.statButton);
        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                bundle.putString("type", type);
                Intent intent = new Intent(Account.this, Statistics.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                bundle.putString("type", type);
                Intent intent = new Intent(Account.this, Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailField.getText().toString();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    URL url = new URL("http://capstone2024.online/snt/deleteaccount.php");
                                    urlConnection = (HttpURLConnection) url.openConnection();
                                    urlConnection.setRequestMethod("POST");
                                    urlConnection.setDoOutput(true);
                                    String requestBody = "email=" + email;
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Account.this);
                                            alertDialog.setTitle("Account Deletion");
                                            alertDialog.setMessage(response.toString());
                                            alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(Account.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                                }
                                            });
                                            alertDialog.show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Account.this);
                                            alertDialog.setTitle("Account Deletion");
                                            alertDialog.setMessage("Failed to delete account. Please try again later.");
                                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            alertDialog.show();
                                        }
                                    });
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
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
}
