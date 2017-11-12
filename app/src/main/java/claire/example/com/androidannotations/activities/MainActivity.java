package claire.example.com.androidannotations.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.asynctask.DownloadJsonTask;

public class MainActivity extends AppCompatActivity {

    private List<String> cheminsFichiersImages;
    private int nombreImagesChargees;
    private String motATrouver;

    public List<String> getCheminsFichiersImages() {
        return cheminsFichiersImages;
    }

    public void setCheminsFichiersImages(List<String> cheminsFichiersImages) {
        this.cheminsFichiersImages = cheminsFichiersImages;
    }

    public int getNombreImagesChargees() {
        return nombreImagesChargees;
    }

    public void setNombreImagesChargees(int nombreImagesChargees) {
        this.nombreImagesChargees = nombreImagesChargees;
    }

    public String getMotATrouver() {
        return motATrouver;
    }

    public void setMotATrouver(String motATrouver) {
        this.motATrouver = motATrouver;
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


}
