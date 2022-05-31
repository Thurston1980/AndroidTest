package com.datechnologies.androidtest.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;
import com.datechnologies.androidtest.api.ChatLogMessageModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Screen that displays a list of chats from a chat log.
 */
public class ChatActivity extends AppCompatActivity {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, ChatActivity.class);
        context.startActivity(starter);
    }

    private void fetch() {
        String httpURL = getResources().getString(R.string.chatURL);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        JSONObject response = null;

        try {
            URL url = new URL(httpURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder buffer = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                buffer.append(line);
            }
            response = new JSONObject(buffer.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        if(response != null){
            JSONObject finalResponse = response;
            runOnUiThread(() -> update(finalResponse));
        }
    }

    public void update(JSONObject response){
        List<ChatLogMessageModel> tempList = new ArrayList<>();

        try {
            JSONArray arr = response.getJSONArray("data");
            for(int i = 0; i < arr.length(); i++){
                JSONObject msg = arr.getJSONObject(i);

                ChatLogMessageModel chatLogMessageModel = new ChatLogMessageModel();
                chatLogMessageModel.userId = msg.getInt("user_id");
                chatLogMessageModel.message = msg.getString("message");
                chatLogMessageModel.username = msg.getString("name");
                chatLogMessageModel.avatarUrl = msg.getString("avatar_url");

                tempList.add(chatLogMessageModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatAdapter.setChatLogMessageModelList(tempList);
    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Chat");

        recyclerView = findViewById(R.id.recyclerView);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        chatAdapter = new ChatAdapter();

        recyclerView.setAdapter(chatAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(this::fetch);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
