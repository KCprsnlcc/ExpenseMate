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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import java.net.URLEncoder;
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
        barChartDatas();
        pieChartDatas();

        ImageButton aButton = findViewById(R.id.accountButton);
        ImageButton sButton = findViewById(R.id.statButton);
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
        sButton.setOnClickListener(new View.OnClickListener() {
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

    public void barChartDatas() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://capstone2024.online/snt/fetchexpenses.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    String postData = "email=" + URLEncoder.encode(email, "UTF-8");

                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(postData.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    ArrayList<BarEntry> barDatas = new ArrayList<>();
                    ArrayList<String> weeks = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        float totalAmount = (float) jsonObject.getDouble("total_amount");
                        String weekRange = "Week " + (i + 1); // Fix: Adjust week numbering
                        barDatas.add(new BarEntry(i + 1, totalAmount));
                        weeks.add(weekRange);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BarChart barChart = findViewById(R.id.barChart);
                            BarDataSet barDataSet = new BarDataSet(barDatas, "Expenses");
                            barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                            barDataSet.setValueTextSize(12f);
                            barDataSet.setDrawValues(true);
                            BarData barData = new BarData(barDataSet);
                            barChart.setData(barData);

// Customizing X-axis
                            XAxis xAxis = barChart.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Set position to bottom
                            xAxis.setTextSize(12f);
                            xAxis.setGranularity(1f);
                            xAxis.setLabelCount(weeks.size()); // Set the number of labels to be displayed
                            xAxis.setDrawGridLines(false); // Hide grid lines
                            xAxis.setDrawAxisLine(false); // Hide axis line
                            xAxis.setCenterAxisLabels(true); // Center labels inside bars
                            xAxis.setLabelRotationAngle(0); // Set initial rotation angle to 0

// Set an OnChartValueSelectedListener to dynamically adjust label rotation
                            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {
                                    // Calculate the width of the bars
                                    float barWidth = barChart.getBarData().getBarWidth();
                                    // Calculate the space available for the label
                                    float spaceAvailable = barWidth / 2f;
                                    // Get the index of the selected entry
                                    int index = (int) e.getX();
                                    // Get the position of the selected entry
                                    float xPos = h.getXPx();
                                    // Check if there's enough space on the left side for the label
                                    if (xPos < spaceAvailable) {
                                        // Set label rotation angle to 90 for left side
                                        xAxis.setLabelRotationAngle(90);
                                    } else {
                                        // Set label rotation angle to -90 for right side
                                        xAxis.setLabelRotationAngle(-90);
                                    }
                                    // Refresh the chart
                                    barChart.invalidate();
                                }

                                @Override
                                public void onNothingSelected() {
                                    // Reset label rotation angle when nothing is selected
                                    xAxis.setLabelRotationAngle(0);
                                    // Refresh the chart
                                    barChart.invalidate();
                                }
                            });
                            barChart.invalidate();
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("Fetch Expenses Error", "Failed to fetch expenses: " + e.getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });
        thread.start();
    }

    public void pieChartDatas() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://capstone2024.online/snt/fetchcategories.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    int[] colors = {Color.parseColor("#0F4C75"), Color.parseColor("#525CEB"), Color.parseColor("#F4D160"), Color.parseColor("#FBEEAC")};
                    PieChart pieChart = findViewById(R.id.pieChart);

                    String requestBody = "email=" + email;
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(requestBody.getBytes());
                    outputStream.close();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    ArrayList<PieEntry> pieDatas = new ArrayList<>();
                    ArrayList<String> legendEntries = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String category = jsonObject.getString("category");
                        int count = jsonObject.getInt("count");
                        String percentage = jsonObject.getString("percentage") + "%";
                        pieDatas.add(new PieEntry(count,percentage));
                        legendEntries.add(category);
                    }

                    PieDataSet pieDataSet = new PieDataSet(pieDatas, "");
                    pieDataSet.setColors(colors);
                    pieDataSet.setDrawValues(false);
                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);

                    pieChart.getDescription().setEnabled(false);
                    Legend legend = pieChart.getLegend();
                    legend.setCustom(getLegendEntries(legendEntries, colors)); // Set custom legend entries
                    legend.setTextSize(12f);
                    pieChart.invalidate();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("Fetch Categories Error", "Failed to fetch categories: " + e.getMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });
        thread.start();
    }

    private LegendEntry[] getLegendEntries(ArrayList<String> categories, int[] colors) {
        LegendEntry[] legendEntries = new LegendEntry[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            legendEntries[i] = new LegendEntry(category, Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, colors[i]);
        }
        return legendEntries;
    }

}
