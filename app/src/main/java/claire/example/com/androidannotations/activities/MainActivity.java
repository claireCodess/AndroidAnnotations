package claire.example.com.androidannotations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.api.BackgroundExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.adapters.ButtonArrayAdapter;
import claire.example.com.androidannotations.toast.AffichageToast;

import static claire.example.com.androidannotations.activities.GameActivity.CHEMINS_FICHIERS_IMAGES;
import static claire.example.com.androidannotations.toast.AffichageToast.afficherToast;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    // Constantes

    public static final int GAME_ACTIVITY_INTENT_CODE = 1;
    public static final String NUM_NIVEAU_SUIVANT = "claire.example.com.androidannotations.niveau";
    public static final String COMMAND = "ping -c 1 claire.tsc-hunter.com";


    // Injection d'une vue

    @ViewById(R.id.noms_niveaux)
    public ListView listView;


    // Ressources (String ici)

    @StringRes(R.string.identifiant_niveau)
    public String idNiveau;

    @StringRes(R.string.niveau)
    public String constanteNiveau;


    // Attributs de classe publiques

    public List<List<String>> cheminsFichiersToutesLesImages;
    public List<String> tousLesMotsATrouver;
    public int nombreDeNiveaux;


    // OnClickListener générale pour toutes les vues (on ne peut pas utiliser l'annotation @Click ici !)

    public final View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            String niveau = (String)v.getTag();
            niveau = niveau.replace(getResources().getString(R.string.identifiant_niveau), "");
            int indexNiveau = Integer.parseInt(niveau);
            demarrerNiveau(indexNiveau);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String urlFichierJsonStr = getResources().getString(R.string.url_root) + "rebus.json";
            URL urlFichierJson = new URL(urlFichierJsonStr);
            telechargerFichierJson(urlFichierJson);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Background(id="cancellable_task")
    public void telechargerFichierJson(URL urlFichierJson) {
        InputStream file;
        JsonReader jsonReader;

        tousLesMotsATrouver = new ArrayList<>();
        cheminsFichiersToutesLesImages = new ArrayList<>();
        nombreDeNiveaux = 0;
        List<String> nomsNiveaux = new ArrayList<>();
        boolean accesServeur = false;

        try {
            accesServeur = Runtime.getRuntime().exec (COMMAND).waitFor() == 0;
        } catch(IOException | InterruptedException e) {
            afficherMessageErreurConnexion();
            BackgroundExecutor.cancelAll("cancellable_task", true);
        }

        if(accesServeur) {
            try {
                file = urlFichierJson.openStream();
                if (file != null) {
                    jsonReader = new JsonReader(new InputStreamReader(file));
                    lireFichierJson(jsonReader);
                    for (int numNiveau = 1; numNiveau <= nombreDeNiveaux; numNiveau++) {
                        nomsNiveaux.add(constanteNiveau + " " + numNiveau);
                    }
                }
                mettreAJourMenuPrincipal(nomsNiveaux);
            } catch (IOException e) {
                afficherMessageErreurJson();
                BackgroundExecutor.cancelAll("cancellable_task", true);
            }
        } else {
            afficherMessageErreurConnexion();
            BackgroundExecutor.cancelAll("cancellable_task", true);
        }

    }

    @UiThread
    public void mettreAJourMenuPrincipal(List<String> nomsNiveaux) {
        listView.setAdapter(new ButtonArrayAdapter<>(this,
                R.layout.item_list_level, nomsNiveaux));
    }

    @UiThread
    public void afficherMessageErreurConnexion() {
        afficherToast(this, AffichageToast.ERREUR_CONNEXION);
    }

    @UiThread
    public void afficherMessageErreurJson() {
        afficherToast(this, AffichageToast.ERREUR_LECTURE_JSON);
    }

    @OnActivityResult(GAME_ACTIVITY_INTENT_CODE)
    public void onResult(int resultCode, @OnActivityResult.Extra(value = NUM_NIVEAU_SUIVANT) int indexNiveauSuivant) {
        if(resultCode == RESULT_OK) {
            if(indexNiveauSuivant < nombreDeNiveaux) {
                demarrerNiveau(indexNiveauSuivant);
            }
        }
        // Si resultCode == RESULT_CANCELED, alors l'utilisateur a appuyé
        // sur la flèche de retour, donc on ne fait rien.
    }

    // Lire le fichier JSON pour définir les valeurs des attributs de classe.
    public void lireFichierJson(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String propertyName = jsonReader.nextName();
            if (propertyName.equals("rebus")) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String rebusPropertyName = jsonReader.nextName();
                        if (rebusPropertyName.equals("mot")) {
                            String mot = jsonReader.nextString();
                            tousLesMotsATrouver.add(mot);
                        } else if (rebusPropertyName.equals("images")) {
                            List<String> cheminsFichiersImgMotCourant = new ArrayList<>();
                            jsonReader.beginArray();
                            while (jsonReader.hasNext()) {
                                String cheminImg = jsonReader.nextString();
                                cheminsFichiersImgMotCourant.add(cheminImg);
                            }
                            cheminsFichiersToutesLesImages.add(cheminsFichiersImgMotCourant);
                            jsonReader.endArray();
                        }
                    }
                    jsonReader.endObject();
                    nombreDeNiveaux++;
                }
                jsonReader.endArray();
            }
        }
        jsonReader.endObject();
    }

    // Démarrer le niveau correspondant à l'index spécifié dans le fichier JSON.
    public void demarrerNiveau(int indexNiveau) {
        ArrayList<String> cheminsFichiersImgNiveauCourant = (ArrayList<String>) cheminsFichiersToutesLesImages.get(indexNiveau);
        if(cheminsFichiersImgNiveauCourant != null) {
            String motATrouver = tousLesMotsATrouver.get(indexNiveau);

            Intent intent = GameActivity_.intent(this)
                    .numNiveauCourant(indexNiveau)
                    .motATrouver(motATrouver).get();
            intent.putStringArrayListExtra(CHEMINS_FICHIERS_IMAGES, cheminsFichiersImgNiveauCourant);
            startActivityForResult(intent, GAME_ACTIVITY_INTENT_CODE);
        }
    }

}
