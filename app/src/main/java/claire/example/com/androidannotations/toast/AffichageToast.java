package claire.example.com.androidannotations.toast;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Claire on 14/11/2017.
 */

public class AffichageToast {

    public static String ERREUR_CONNEXION = "Impossible d'établir une connexion avec le serveur.";
    public static String ERREUR_LECTURE_JSON = "Erreur lors de la lecture du fichier JSON du serveur.";

    public static void afficherToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
