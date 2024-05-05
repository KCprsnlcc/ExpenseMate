package com.example.synthegratechinsertion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Statistics extends AppCompatActivity {
    String name = "", email = "", type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        getSupportActionBar().setTitle("Statistics");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
            type = bundle.getString("type");
        }
        BarChart barChart = findViewById(R.id.barChart);
        ArrayList<BarEntry> barDatas = new ArrayList<>();
        for (int i=1; i<10; i++){
            float value = (float) (i*10.0);
            BarEntry barData = new BarEntry(i, value);
            barDatas.add(barData);
        }
        BarDataSet barDataSet = new BarDataSet(barDatas, "Expenses");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        barDataSet.setDrawValues(false);
        barChart.setData(new BarData(barDataSet));
        pieChartDatas();

        ImageButton aButton = findViewById(R.id.accountButton);
        ImageButton hButton = findViewById(R.id.homeButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                bundle.putString("type", type);
                Intent intent = new Intent(Statistics.this, Account.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                Intent intent = new Intent(Statistics.this, Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
    public void pieChartDatas(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    // Create URL for fetching data
                    URL url = new URL("http://10.0.2.2/synthegratech/fetchcategories.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    int[] colors = {Color.parseColor("#0F4C75"), Color.parseColor("#525CEB"), Color.parseColor("#F4D160"), Color.parseColor("#FBEEAC")};
                    PieChart pieChart = findViewById(R.id.pieChart);
                    // Prepare request body
                    String requestBody = "email=" + email;

                    // Write request body to output stream
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(requestBody.getBytes());
                    outputStream.close();

                    // Read response from input stream
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    // Process response data
                    JSONArray jsonArray = new JSONArray(response.toString());
                    ArrayList<PieEntry> pieDatas = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("category");
                        int count = jsonObject.getInt("count");
                        pieDatas.add(new PieEntry(count, category));
                    }

                    // Update pie chart with fetched data
                    PieDataSet pieDataSet = new PieDataSet(pieDatas, "");
                    pieDataSet.setColors(colors);
                    Legend legend = pieChart.getLegend();
                    pieDataSet.setDrawValues(false);
                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.getLegend().setEnabled(true);

                    // Update legend
                    LegendEntry[] legendEntries = new LegendEntry[pieDatas.size()];
                    for (int i = 0; i < pieDatas.size(); i++) {
                        PieEntry entry = pieDatas.get(i);
                        legendEntries[i] = new LegendEntry(entry.getLabel(), Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, colors[i]);
                    }

                    legend.setCustom(legendEntries);
                    legend.setTextSize(16f);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("Fetch Categories Error", "Failed to fetch categories: " + e.getMessage());
                    // Handle error
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });
        thread.start();
    }
}
