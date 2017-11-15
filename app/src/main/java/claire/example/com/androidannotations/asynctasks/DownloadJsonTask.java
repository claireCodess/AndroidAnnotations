package claire.example.com.androidannotations.asynctasks;

import android.os.AsyncTask;
import android.util.JsonReader;
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
import claire.example.com.androidannotations.toast.AffichageToast;

import static claire.example.com.androidannotations.activities.MainActivity.COMMAND;

/**
 * Created by Claire on 12/11/2017.
 */

// Cette classe ne sera pas utilisée dans la version finale.
public class DownloadJsonTask extends AsyncTask<URL, Integer, List<String>> {

    private MainActivity activity;

    public DownloadJsonTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected List<String> doInBackground(URL... urls) {
        InputStream file = null;
        JsonReader jsonReader;

        activity.tousLesMotsATrouver = new ArrayList<>();
        activity.cheminsFichiersToutesLesImages = new ArrayList<>();
        activity.nombreDeNiveaux = 0;
        List<String> nomsNiveaux = new ArrayList<>();
        boolean accesServeur = false;

        try {
            accesServeur = Runtime.getRuntime().exec (COMMAND).waitFor() == 0;
        } catch(IOException | InterruptedException e) {
            AffichageToast.afficherToast(this.activity, AffichageToast.ERREUR_CONNEXION);
            cancel(true);
            if(isCancelled())
                return null;
        }

        if(accesServeur) {
            // Il devrait y avoir une seule URL !
            if(urls.length == 1) {
                try {
                    file = urls[0].openStream();
                    if (file != null) {
                        jsonReader = new JsonReader(new InputStreamReader(file));
                        activity.lireFichierJson(jsonReader);
                        for (int numNiveau = 1; numNiveau <= activity.nombreDeNiveaux; numNiveau++) {
                            nomsNiveaux.add(activity.getResources().getString(R.string.niveau) + " " + numNiveau);
                        }
                    }
                } catch(IOException e) {
                    AffichageToast.afficherToast(this.activity, AffichageToast.ERREUR_LECTURE_JSON);
                    cancel(true);
                    if(isCancelled())
                        return null;
                }
            }
        } else {
            AffichageToast.afficherToast(this.activity, AffichageToast.ERREUR_CONNEXION);
            cancel(true);
            if(isCancelled())
                return null;
        }

        return nomsNiveaux;
    }

    @Override
    protected void onPostExecute(List<String> nomsNiveaux) {

        activity.listView = (ListView) activity.findViewById(R.id.noms_niveaux);
        activity.listView.setAdapter(new ButtonArrayAdapter<String>(activity,
                R.layout.item_list_level, nomsNiveaux));

    }

}
