package claire.example.com.androidannotations.asynctasks;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.activities.MainActivity_;
import claire.example.com.androidannotations.adapters.ButtonArrayAdapter;

/**
 * Created by Claire on 12/11/2017.
 */

public class DownloadJsonTask extends AsyncTask<URL, Integer, List<String>> {

    private MainActivity_ activity;

    public DownloadJsonTask(MainActivity_ activity) {
        this.activity = activity;
    }

    @Override
    protected List<String> doInBackground(URL... urls) {
        InputStream file = null;
        JsonReader jsonReader;

        activity.setTousLesMotsATrouver(new ArrayList<String>());
        activity.setCheminsFichiersToutesLesImages(new ArrayList<List<String>>());
        int nbNiveaux = 0;
        List<String> nomsNiveaux = new ArrayList<>();

        LinearLayout gameLayout, loadingLayout;
        GridView gridView;

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
                                } else if (rebusPropertyName.equals("images")) {
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

                activity.setNombreDeNiveaux(nbNiveaux);
                for(int numNiveau = 1; numNiveau <= nbNiveaux; numNiveau++) {
                    nomsNiveaux.add(activity.constanteNiveau/*getResources().getString(R.string.niveau)*/ + " " + numNiveau);
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return nomsNiveaux;
    }

    @Override
    protected void onPostExecute(List<String> nomsNiveaux) {

        //ListView listView = (ListView) activity.findViewById(R.id.noms_niveaux);
        activity.listView.setAdapter(new ButtonArrayAdapter<String>(activity,
                R.layout.item_list_level, nomsNiveaux));

    }
}
