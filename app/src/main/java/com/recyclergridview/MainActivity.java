package com.recyclergridview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.recyclergridview.adapter.ImagesAdapter;
import com.recyclergridview.model.ImageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView imageList;

    GridLayoutManager gridLayoutManager;
    ImagesAdapter imagesAdapters;
    ArrayList<ImageModel> imageModelArrays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageList = (RecyclerView)findViewById(R.id.imageList);

        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2, LinearLayoutManager.VERTICAL,false);
        imageList.setLayoutManager(gridLayoutManager);

        imageModelArrays = new ArrayList<>();

        imagesAdapters = new ImagesAdapter(MainActivity.this, imageModelArrays, this);
        imageList.setAdapter(imagesAdapters);


        try {
            getImageFromPexels();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getImageFromPexels() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.pexels.com/v1/search?query=work+place&per_page=50&page=1")
                .header("Authorization", "YOUR_API_KEY")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                //Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String mMessage = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject parent = new JSONObject(mMessage);
                            JSONArray photos = parent.getJSONArray("photos");

                            Log.d("resp", "data"+photos);
                            for (int i = 0; i < photos.length(); i++) {

                                JSONObject data = photos.getJSONObject(i);

                                JSONObject imageUrls = data.getJSONObject("src");
                                ImageModel imageModel = new ImageModel();
                                imageModel.setId(data.getInt("id"));
                                imageModel.setPhotographer(data.getString("photographer"));
                                imageModel.setUrl(imageUrls.getString("medium"));

                                imageModelArrays.add(imageModel);
                            }

                            imagesAdapters.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
