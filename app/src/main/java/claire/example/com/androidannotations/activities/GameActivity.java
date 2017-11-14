package claire.example.com.androidannotations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.androidannotations.annotations.res.StringRes;

import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.adapters.ImageArrayAdapter;

@EActivity(R.layout.activity_game)
public class GameActivity extends AppCompatActivity {

    // Injections de vues

    @ViewById(R.id.grid_view)
    public GridView gridView;

    @ViewById(R.id.reponse_utilisateur)
    public TextView reponseUtilisateur;

    @ViewById(R.id.valider_reponse)
    public Button valider;

    @ViewsById({R.id.loading_layout, R.id.game_layout})
    public List<LinearLayout> linearLayouts;

    // Ressources (Strings ici)

    @StringRes
    public String motTrouve;

    @StringRes
    public String motNonTrouve;

    @StringRes(R.string.url_root)
    public String urlRoot;

    private int numNiveauCourant;
    private String motATrouver;
    private List<String> cheminsFichiersImages;

    public static final String NUM_NIVEAU_SUIVANT = "claire.example.com.androidannotations.niveau";

    public String getMotATrouver() {
        return motATrouver;
    }

    public void setMotATrouver(String motATrouver) {
        this.motATrouver = motATrouver;
    }

    public List<String> getCheminsFichiersImages() {
        return cheminsFichiersImages;
    }

    public void setCheminsFichiersImages(List<String> cheminsFichiersImages) {
        this.cheminsFichiersImages = cheminsFichiersImages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //GridView gridView;

        Intent intent = getIntent();
        numNiveauCourant = intent.getIntExtra(MainActivity_.NUM_NIVEAU_DEMARRAGE, 0);
        motATrouver = intent.getStringExtra(MainActivity_.MOT_A_TROUVER);
        cheminsFichiersImages = intent.getStringArrayListExtra(MainActivity_.CHEMINS_FICHIERS_IMAGES);

        //gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageArrayAdapter(this, R.layout.item_grid_image, cheminsFichiersImages));

        //Button valider = (Button) findViewById(R.id.valider_reponse);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.valider_reponse) {
                    //TextView reponseUtilisateur = (TextView) findViewById(R.id.reponse_utilisateur);

                    if (reponseUtilisateur.getText().toString().equals(motATrouver)) {
                        // Ici, il faut penser à l'astuce d'utiliser "GameActivity.this", sinon, le "this"
                        // représente cette instance d'OnClickListener !
                        Toast.makeText(GameActivity.this, /*R.string.*/motTrouve, Toast.LENGTH_SHORT).show();

                        // On passe au niveau suivant s'il existe, sinon on retourne à l'écran d'accueil
                        Intent intentRetour = new Intent();
                        intentRetour.putExtra(NUM_NIVEAU_SUIVANT, ++numNiveauCourant);
                        setResult(RESULT_OK, intentRetour);
                        finish();
                    } else {
                        // Même remarque ici
                        Toast.makeText(GameActivity.this, /*R.string.*/motNonTrouve, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
