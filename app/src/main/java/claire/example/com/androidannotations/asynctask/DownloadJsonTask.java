package claire.example.com.androidannotations.asynctask;

import android.os.AsyncTask;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.activities.MainActivity;
import claire.example.com.androidannotations.image.ImageArrayAdapter;

/**
 * Created by Claire on 12/11/2017.
 */

public class DownloadJsonTask extends AsyncTask<URL, Integer, JsonReader> {

    private MainActivity activity;

    public DownloadJsonTask(MainActivity activity) {
        this.activity = activity;
    }

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
        activity.setNombreImagesChargees(0);

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
                                    activity.setMotATrouver(mot);
                            } else if (rebusPropertyName.equals("images")) {
                                if(i == 0)
                                    activity.setCheminsFichiersImages(new ArrayList<String>());
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()) {
                                    String cheminImg = jsonReader.nextString();
                                    if(i == 0) {
                                        activity.getCheminsFichiersImages().add(cheminImg);
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
            for(String chemin : activity.getCheminsFichiersImages()) {
                urlsImagesARecuperer.add(new URL(activity.getResources().getString(R.string.url_root) + chemin));
            }

            gridView = (GridView) activity.findViewById(R.id.grid_view);
            gameLayout = (LinearLayout) activity.findViewById(R.id.game_layout);
            loadingLayout = (LinearLayout) activity.findViewById(R.id.loading_layout);
            gridView.setAdapter(new ImageArrayAdapter(activity, R.layout.item_grid_image, activity.getCheminsFichiersImages()));
            //new DownloadImagesTask().execute(urlsImagesARecuperer.toArray(new URL[urlsImagesARecuperer.size()]));

            Button valider = (Button) activity.findViewById(R.id.valider_reponse);
            valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.valider_reponse) {
                        TextView reponseUtilisateur = (TextView) activity.findViewById(R.id.reponse_utilisateur);

                        if(reponseUtilisateur.getText().toString().equals(activity.getMotATrouver())) {
                            // Ici, il faut penser à l'astuce d'utiliser "MainActivity.this", sinon, le "this"
                            // représente cette instance d'OnClickListener !
                            Toast.makeText(activity, R.string.motTrouve, Toast.LENGTH_SHORT).show();
                        } else {
                            // Même remarque ici
                            Toast.makeText(activity, R.string.motNonTrouve, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
