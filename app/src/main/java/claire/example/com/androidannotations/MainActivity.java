package claire.example.com.androidannotations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<String> cheminsFichiersImages;
    public static int nombreImagesChargees;
    public static String motATrouver;

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

            // Il devrait y avoir une seule URL !
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
            LinearLayout gameLayout, loadingLayout;
            GridView gridView;
            nombreImagesChargees = 0;

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
                                        if(i == 0) {
                                            cheminsFichiersImages.add(cheminImg);
                                        }
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

                /*TextView textView = (TextView) findViewById(R.id.mot_a_trouver);
                textView.setText("Mot à trouver : " + motATrouver);*/

                List<URL> urlsImagesARecuperer = new ArrayList<>();
                for(String chemin : cheminsFichiersImages) {
                    urlsImagesARecuperer.add(new URL(getResources().getString(R.string.url_root) + chemin));
                }

                gridView = (GridView)findViewById(R.id.grid_view);
                gameLayout = (LinearLayout) findViewById(R.id.game_layout);
                loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
                gridView.setAdapter(new ImageArrayAdapter(MainActivity.this, R.layout.item_grid_image, cheminsFichiersImages, gameLayout, loadingLayout));
                //new DownloadImagesTask().execute(urlsImagesARecuperer.toArray(new URL[urlsImagesARecuperer.size()]));

                Button valider = (Button) findViewById(R.id.valider_reponse);
                valider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.valider_reponse) {
                            TextView reponseUtilisateur = (TextView) findViewById(R.id.reponse_utilisateur);

                            if(reponseUtilisateur.getText().toString().equals(motATrouver)) {
                                // Ici, il faut penser à l'astuce d'utiliser "MainActivity.this", sinon, le "this"
                                // représente cette instance d'OnClickListener !
                                Toast.makeText(MainActivity.this, R.string.motTrouve, Toast.LENGTH_SHORT).show();
                            } else {
                                // Même remarque ici
                                Toast.makeText(MainActivity.this, R.string.motNonTrouve, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
