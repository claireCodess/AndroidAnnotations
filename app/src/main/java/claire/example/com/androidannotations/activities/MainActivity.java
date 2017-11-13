package claire.example.com.androidannotations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.asynctask.DownloadJsonTask;

public class MainActivity extends AppCompatActivity {

    private List<List<String>> cheminsFichiersToutesLesImages;
    private List<String> tousLesMotsATrouver;

    public static final String CHEMINS_FICHIERS_IMAGES = "claire.example.com.androidannotations.chemins";
    public static final String MOT_A_TROUVER = "claire.example.com.androidannotations.mot";
    public static final String NUM_NIVEAU_COURANT = "claire.example.com.androidannotations.niveau";

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
            new DownloadJsonTask(this).execute(urlFichierJson);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    // Global OnClickListener for all views
    final private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            String niveau = (String)v.getTag();
            niveau = niveau.replace(getResources().getString(R.string.identifiant_niveau), "");
            int indexNiveau = Integer.parseInt(niveau);
            ArrayList<String> cheminsFichiersImgNiveauCourant = (ArrayList) cheminsFichiersToutesLesImages.get(indexNiveau);
            String motATrouver = tousLesMotsATrouver.get(indexNiveau);

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra(NUM_NIVEAU_COURANT, indexNiveau);
            intent.putExtra(MOT_A_TROUVER, motATrouver);
            intent.putStringArrayListExtra(CHEMINS_FICHIERS_IMAGES, cheminsFichiersImgNiveauCourant);
            startActivity(intent);
        }

    };

}
