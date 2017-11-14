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

    private static final int GAME_ACTIVITY_INTENT_CODE = 1;
    public static final String NUM_NIVEAU_SUIVANT = "claire.example.com.androidannotations.niveau";


    // Injection d'une vue

    @ViewById(R.id.noms_niveaux)
    public ListView listView;


    // Ressources (String ici)

    @StringRes(R.string.identifiant_niveau)
    public String idNiveau;

    @StringRes(R.string.niveau)
    public String constanteNiveau;


    // L'extra passée en paramètre à l'Intent de "réponse" à l'Intent qui a démarré l'activité GameActivity

    /*@Extra(NUM_NIVEAU_SUIVANT)
    public int */


    private List<List<String>> cheminsFichiersToutesLesImages;
    private List<String> tousLesMotsATrouver;
    private int nombreDeNiveaux;


    public List<List<String>> getCheminsFichiersToutesLesImages() {
        return cheminsFichiersToutesLesImages;
    }

    public void setCheminsFichiersToutesLesImages(List<List<String>> cheminsFichiersToutesLesImages) {
        this.cheminsFichiersToutesLesImages = cheminsFichiersToutesLesImages;
    }

    public List<String> getTousLesMotsATrouver() {
        return tousLesMotsATrouver;
    }

    public void setTousLesMotsATrouver(List<String> tousLesMotsATrouver) {
        this.tousLesMotsATrouver = tousLesMotsATrouver;
    }

    public int getNombreDeNiveaux() {
        return nombreDeNiveaux;
    }

    public void setNombreDeNiveaux(int nombreDeNiveaux) {
        this.nombreDeNiveaux = nombreDeNiveaux;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String urlFichierJsonStr = getResources().getString(R.string.url_root) + "rebus.json";
            URL urlFichierJson = new URL(urlFichierJsonStr);
            //new DownloadJsonTask((MainActivity_)this).execute(urlFichierJson);
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
            String command = "ping -c 1 claire.tsc-hunter.com";
            accesServeur = Runtime.getRuntime().exec (command).waitFor() == 0;
        } catch(IOException | InterruptedException e) {
            afficherMessageErreurConnexion();
            BackgroundExecutor.cancelAll("cancellable_task", true);
        }

        if(accesServeur) {
            try {
                file = urlFichierJson.openStream();
                if (file != null) {
                    jsonReader = new JsonReader(new InputStreamReader(file));
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

                    for (int numNiveau = 1; numNiveau <= nombreDeNiveaux; numNiveau++) {
                        nomsNiveaux.add(constanteNiveau + " " + numNiveau);
                    }
                }
                mettreAJourMenuPrincipal(nomsNiveaux);
            } catch (IOException e) {
                afficherMessageErreurConnexion();
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

    // Global OnClickListener for all views
    final private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            System.out.println("Appuyé !");
            String niveau = (String)v.getTag();
            niveau = niveau.replace(getResources().getString(R.string.identifiant_niveau), "");
            int indexNiveau = Integer.parseInt(niveau);
            System.out.println("indexNiveau = " + indexNiveau);
            demarrerNiveau(indexNiveau);
        }

    };

    /*@Click(R.id.bouton_niveau)
    public void levelButtonClicked(Button clickedButton) {
        System.out.println("Appuyé !");
        String niveau = (String)clickedButton.getTag();
        niveau = niveau.replace(idNiveau, "");
        int indexNiveau = Integer.parseInt(niveau);
        demarrerNiveau(indexNiveau);
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GAME_ACTIVITY_INTENT_CODE) {
            if(resultCode == RESULT_OK) {
                int indexNiveauSuivant = data.getIntExtra(NUM_NIVEAU_SUIVANT, 0);
                if(indexNiveauSuivant < nombreDeNiveaux) {
                    demarrerNiveau(indexNiveauSuivant);
                }
            }
            // Si resultCode == RESULT_CANCELED, alors l'utilisateur a appuyé
            // sur la flèche de retour, donc on ne fait rien.
        }
    }*/

    @OnActivityResult(GAME_ACTIVITY_INTENT_CODE)
    public void onResult(int resultCode, @OnActivityResult.Extra(value = NUM_NIVEAU_SUIVANT) int indexNiveauSuivant) {
        if(resultCode == RESULT_OK) {
            //int indexNiveauSuivant = data.getIntExtra(NUM_NIVEAU_SUIVANT, 0);
            if(indexNiveauSuivant < nombreDeNiveaux) {
                demarrerNiveau(indexNiveauSuivant);
            }
        }
        // Si resultCode == RESULT_CANCELED, alors l'utilisateur a appuyé
        // sur la flèche de retour, donc on ne fait rien.
    }

    /**
     * Démarrer le niveau à l'index correspondant dans le fichier JSON.
     * @param indexNiveau L'index du niveau à démarrer.
     */
    public void demarrerNiveau(int indexNiveau) {
        ArrayList<String> cheminsFichiersImgNiveauCourant = (ArrayList<String>) cheminsFichiersToutesLesImages.get(indexNiveau);
        System.out.println("Size cheminsFichiersImgNiveauCourant = " + cheminsFichiersImgNiveauCourant.size());
        if(cheminsFichiersImgNiveauCourant != null) {
            String motATrouver = tousLesMotsATrouver.get(indexNiveau);

            //Intent intent = new Intent(this, GameActivity_.class);
            Intent intent = GameActivity_.intent(this)
                    .numNiveauCourant(indexNiveau)
                    .motATrouver(motATrouver).get();
        /*intent.putExtra(NUM_NIVEAU_DEMARRAGE, indexNiveau);
        intent.putExtra(MOT_A_TROUVER, motATrouver);*/
            intent.putStringArrayListExtra(CHEMINS_FICHIERS_IMAGES, cheminsFichiersImgNiveauCourant);
            startActivityForResult(intent, GAME_ACTIVITY_INTENT_CODE);
        } else {
            System.err.println("cheminsFichiersImgNiveauCourant null !");
        }
    }

}
