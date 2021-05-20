package com.example.pbe_table;

import android.graphics.Color;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private TableLayout tableLayout;
    private EditText textQuery;
    private String[] header;
    private ArrayList<String[]> rows;
    private TableDynamic tableDynamic;
    private RequestQueue requestQueue;
    private TextView text;
    private boolean tableExist=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rows = new ArrayList<>();
        tableLayout = (TableLayout) findViewById(R.id.table);
        textQuery = (EditText) findViewById(R.id.textQuery);
        text = (TextView) findViewById(R.id.textView);
        text.setTextSize(20);
        text.setHintTextColor(Color.RED);
        

        requestQueue = Volley.newRequestQueue(this);
        tableDynamic= new TableDynamic(tableLayout, getApplicationContext());
    }

    public void httpGet(View view){
        String url="http://192.168.1.42/"+textQuery.getText().toString();
        String title=textQuery.getText().toString().split("\\?")[0];
        ArrayList<String[]> test=new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray  response) {
                        JSONArray jsonArray = response;

                        if(tableExist) {tableDynamic.deleteAll(tableLayout);}

                        try {
                            header = new String[jsonArray.getJSONObject(0).names().length()];
                            for(int h=0; h<header.length; h++){
                                header[h]=jsonArray.getJSONObject(0).names().getString(h);
                            }
                            tableDynamic.addHeader(header);
                            String[][] item = new String[jsonArray.length()][header.length];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                for(int j=0; j < header.length; j++){
                                    String cell=jsonObject.getString(header[j]);
                                    item[i][j]=cell;
                                }
                                rows.add(item[i]);
                            }

                            text.setText(title);

                            tableDynamic.addData(rows);
                            tableDynamic.backgroundHeader(Color.CYAN);
                            tableDynamic.backgroundData(Color.YELLOW, Color.GREEN);
                            rows.clear();

                            tableExist=true;

                        } catch (Exception w) {
                            text.setText(w.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                        text.setText(error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);

    }

}