package claire.example.com.androidannotations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.adapters.ImageArrayAdapter;
import claire.example.com.androidannotations.toast.AffichageToast;

// Etape 1 : à décommenter
/* import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.androidannotations.annotations.res.StringRes; */

@EActivity(R.layout.activity_game)
public class GameActivity extends AppCompatActivity {

    // Constantes

    public static final String CHEMINS_FICHIERS_IMAGES = "claire.example.com.androidannotations.chemins";
    public static final String MOT_A_TROUVER = "claire.example.com.androidannotations.mot";
    public static final String NUM_NIVEAU_COURANT = "claire.example.com.androidannotations.niveau_start";


    // Injections de vues

    @ViewById(R.id.grid_view)
    public GridView gridView;

    @ViewById(R.id.reponse_utilisateur)
    public TextView reponseUtilisateur;

    @ViewById(R.id.valider_reponse)
    public Button valider;

    @ViewsById({R.id.loading_layout, R.id.game_layout})
    public List<LinearLayout> linearLayouts;


    // Ressources (String ici)

    @StringRes
    public String motTrouve;

    @StringRes
    public String motNonTrouve;

    @StringRes(R.string.url_root)
    public String urlRoot;


    // Les extras passées en paramètre à l'Intent qui a démarré cette Activity

    @Extra(NUM_NIVEAU_COURANT)
    public Integer numNiveauCourant;

    @Extra(MOT_A_TROUVER)
    public String motATrouver;

    // Nous ne mettons pas l'annotation @Extra ici car List<String> n'est pas un type primitif.
    // Nous serons obligés de le récupérer par l'Intent par la méthode classique.
    public List<String> cheminsFichiersImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /*Intent intent = getIntent();
        numNiveauCourant = intent.getIntExtra(MainActivity_.NUM_NIVEAU_DEMARRAGE, 0);
        motATrouver = intent.getStringExtra(MainActivity_.MOT_A_TROUVER);
        cheminsFichiersImages = intent.getStringArrayListExtra(MainActivity_.CHEMINS_FICHIERS_IMAGES);*/

        //GridView gridView = (GridView) findViewById(R.id.grid_view);
        //gridView.setAdapter(new ImageArrayAdapter(this, R.layout.item_grid_image, cheminsFichiersImages));

        //Button valider = (Button) findViewById(R.id.valider_reponse);
        /*valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.valider_reponse) {
                    //TextView reponseUtilisateur = (TextView) findViewById(R.id.reponse_utilisateur);

                    if (reponseUtilisateur.getText().toString().equals(motATrouver)) {
                        // Ici, il faut penser à l'astuce d'utiliser "GameActivity.this", sinon, le "this"
                        // représente cette instance d'OnClickListener !
                        Toast.makeText(GameActivity.this, R.string.motTrouve, Toast.LENGTH_SHORT).show();

                        // On passe au niveau suivant s'il existe, sinon on retourne à l'écran d'accueil
                        /*Intent intentRetour = new Intent();
                        intentRetour.putExtra(MainActivity.NUM_NIVEAU_SUIVANT, ++numNiveauCourant);
                        Intent intentRetour = GameActivity_.intent(this)
                                .indexNiveauSuivant(++numNiveauCourant);

                        setResult(RESULT_OK, intentRetour);

                        finish();
                    } else {
                        // Même remarque ici
                        Toast.makeText(GameActivity.this, R.string.motNonTrouve, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*/

    }

    @AfterViews
    public void chargerImages() {
        Intent intent = getIntent();
        cheminsFichiersImages = intent.getStringArrayListExtra(CHEMINS_FICHIERS_IMAGES);
        gridView.setAdapter(new ImageArrayAdapter(this, R.layout.item_grid_image, cheminsFichiersImages));
    }

    @Click(R.id.valider_reponse)
    public void validerClicked() {
        if (reponseUtilisateur.getText().toString().equals(motATrouver)) {
            AffichageToast.afficherToast(this, motTrouve);

            // On passe au niveau suivant s'il existe, sinon on retourne à l'écran d'accueil
            Intent intentRetour = MainActivity_.intent(this).get();
            intentRetour.putExtra(MainActivity_.NUM_NIVEAU_SUIVANT, ++numNiveauCourant);
            setResult(RESULT_OK, intentRetour);
            finish();
        } else {
            AffichageToast.afficherToast(this, motNonTrouve);
        }
    }
}
