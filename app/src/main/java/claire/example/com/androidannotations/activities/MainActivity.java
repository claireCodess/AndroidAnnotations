package claire.example.com.androidannotations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.asynctasks.DownloadJsonTask;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    // Injection de vue
    @ViewById(R.id.noms_niveaux)
    public ListView listView;

    // Ressources (Strings ici)

    @StringRes(R.string.identifiant_niveau)
    public String idNiveau;

    @StringRes(R.string.niveau)
    public String constanteNiveau;

    private List<List<String>> cheminsFichiersToutesLesImages;
    private List<String> tousLesMotsATrouver;
    private int nbNiveaux;

    public static final String CHEMINS_FICHIERS_IMAGES = "claire.example.com.androidannotations.chemins";
    public static final String MOT_A_TROUVER = "claire.example.com.androidannotations.mot";
    public static final String NUM_NIVEAU_DEMARRAGE = "claire.example.com.androidannotations.niveau_start";
    private static final int GAME_ACTIVITY_INTENT_CODE = 1;

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

    public int getNbNiveaux() {
        return nbNiveaux;
    }

    public void setNbNiveaux(int nbNiveaux) {
        this.nbNiveaux = nbNiveaux;
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
            new DownloadJsonTask((MainActivity_)this).execute(urlFichierJson);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    // Global OnClickListener for all views
    final private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            String niveau = (String)v.getTag();
            niveau = niveau.replace(/*getResources().getString(R.string.identifiant_niveau)*/idNiveau, "");
            int indexNiveau = Integer.parseInt(niveau);
            demarrerNiveau(indexNiveau);
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GAME_ACTIVITY_INTENT_CODE) {
            if(resultCode == RESULT_OK) {
                int indexNiveauSuivant = data.getIntExtra(GameActivity_.NUM_NIVEAU_SUIVANT, 0);
                if(indexNiveauSuivant < nbNiveaux) {
                    demarrerNiveau(indexNiveauSuivant);
                }
            }
            // Si resultCode == RESULT_CANCELED, alors l'utilisateur a appuyé
            // sur la flèche de retour, donc on ne fait rien.
        }
    }

    /**
     * Démarrer le niveau à l'index correspondant dans le fichier JSON.
     * @param indexNiveau L'index du niveau à démarrer.
     */
    public void demarrerNiveau(int indexNiveau) {
        ArrayList<String> cheminsFichiersImgNiveauCourant = (ArrayList) cheminsFichiersToutesLesImages.get(indexNiveau);
        String motATrouver = tousLesMotsATrouver.get(indexNiveau);

        Intent intent = new Intent(this, GameActivity_.class);
        intent.putExtra(NUM_NIVEAU_DEMARRAGE, indexNiveau);
        intent.putExtra(MOT_A_TROUVER, motATrouver);
        intent.putStringArrayListExtra(CHEMINS_FICHIERS_IMAGES, cheminsFichiersImgNiveauCourant);
        //startActivity(intent);
        startActivityForResult(intent, GAME_ACTIVITY_INTENT_CODE);
    }

}
