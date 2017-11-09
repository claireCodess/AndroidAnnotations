package claire.example.com.androidannotations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.widget.GridView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<String> cheminsFichiersImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String urlFichierJsonStr = getResources().getString(R.string.url_root) + "rebus.json";
            URL urlFichierJson = new URL(urlFichierJsonStr);
            new DownloadJsonTask().execute(urlFichierJson);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }

    class DownloadImagesTask extends AsyncTask<URL, Integer, List> {

        @Override
        protected List doInBackground(URL... urls) {
            InputStream file = null;

            for(URL url : urls) {
                try {
                    file = url.openStream();
                } catch(IOException e) {
                    e.printStackTrace();
                    return null;
                }
                Bitmap image = BitmapFactory.decodeStream(file);
                /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, myStringArray);*/
            }

            return null;
        }

    }

    class DownloadJsonTask extends AsyncTask<URL, Integer, JsonReader> {

        @Override
        protected JsonReader doInBackground(URL... urls) {
            InputStream file = null;
            JsonReader jsonReader = null;

            // There should only be one URL
            if(urls.length == 1) {
                try {
                    file = urls[0].openStream();
                } catch(IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            if(file != null) {
                jsonReader = new JsonReader(new InputStreamReader(file));
            }

            return jsonReader;
        }

        @Override
        protected void onPostExecute(JsonReader jsonReader) {
            String motATrouver = null;
            GridView gridView;

            try {
                jsonReader.beginObject();
                while(jsonReader.hasNext()) {
                    String propertyName = jsonReader.nextName();
                    if (propertyName.equals("rebus")) {
                        jsonReader.beginArray();
                        int i = 0;
                        while(jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
                                String rebusPropertyName = jsonReader.nextName();
                                if (rebusPropertyName.equals("mot")) {
                                    String mot = jsonReader.nextString();
                                    if(i == 0)
                                        motATrouver = mot;
                                } else if (rebusPropertyName.equals("images")) {
                                    if(i == 0)
                                        cheminsFichiersImages = new ArrayList<>();
                                    jsonReader.beginArray();
                                    while (jsonReader.hasNext()) {
                                        String cheminImg = jsonReader.nextString();
                                        if(i == 0)
                                            cheminsFichiersImages.add(cheminImg);
                                    }
                                    jsonReader.endArray();
                                }
                            }
                            jsonReader.endObject();
                            i++;
                        }
                        jsonReader.endArray();
                    }
                }
                jsonReader.endObject();

                TextView textView = (TextView) findViewById(R.id.mot_a_trouver);
                textView.setText("Mot à trouver : " + motATrouver);

                List<URL> urlsImagesARecuperer = new ArrayList<>();
                for(String chemin : cheminsFichiersImages) {
                    urlsImagesARecuperer.add(new URL(getResources().getString(R.string.url_root) + chemin));
                }

                gridView = (GridView)findViewById(R.id.grid_view);
                gridView.setAdapter(new ImageArrayAdapter(MainActivity.this, R.layout.item_grid_image, cheminsFichiersImages));
                //new DownloadImagesTask().execute(urlsImagesARecuperer.toArray(new URL[urlsImagesARecuperer.size()]));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}