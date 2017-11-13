package claire.example.com.androidannotations.asynctask;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.activities.MainActivity;
import claire.example.com.androidannotations.adapters.ButtonArrayAdapter;

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
        int nbNiveaux = 0;
        List<String> nomsNiveaux;

        activity.setCheminsFichiersToutesLesImages(new ArrayList<List<String>>());
        activity.setTousLesMotsATrouver(new ArrayList<String>());


        try {
            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
                String propertyName = jsonReader.nextName();
                if (propertyName.equals("rebus")) {
                    jsonReader.beginArray();
                    while(jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String rebusPropertyName = jsonReader.nextName();
                            if (rebusPropertyName.equals("mot")) {
                                String mot = jsonReader.nextString();
                                activity.getTousLesMotsATrouver().add(mot);
                                /*if(nbNiveaux == 0)
                                    activity.setMotATrouver(mot);*/
                            } else if (rebusPropertyName.equals("images")) {
                                //if(nbNiveaux == 0)
                                List<String> cheminsFichiersImgMotCourant = new ArrayList<>();
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()) {
                                    String cheminImg = jsonReader.nextString();
                                    cheminsFichiersImgMotCourant.add(cheminImg);
                                }
                                activity.getCheminsFichiersToutesLesImages().add(cheminsFichiersImgMotCourant);
                                jsonReader.endArray();
                            }
                        }
                        jsonReader.endObject();
                        nbNiveaux++;
                    }
                    jsonReader.endArray();
                }
            }
            jsonReader.endObject();

            activity.setNbNiveaux(nbNiveaux);
            nomsNiveaux = new ArrayList<>();
            for(int numNiveau = 1; numNiveau <= nbNiveaux; numNiveau++) {
                nomsNiveaux.add(activity.getResources().getString(R.string.niveau) + " " + numNiveau);
            }

            ListView listView = (ListView) activity.findViewById(R.id.noms_niveaux);
            listView.setAdapter(new ButtonArrayAdapter<String>(activity,
                    R.layout.item_list_level, nomsNiveaux));

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
