package claire.example.com.androidannotations.application;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

// Etape 1 : à décommenter
// import org.androidannotations.annotations.EApplication;

/**
 * Created by Claire on 09/11/2017.
 */

// Etape 2 : à décommenter
// @EApplication
public class UILApplication extends Application {
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // Initialiser ImageLoader avec la configuration par défaut.
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
    }

}
