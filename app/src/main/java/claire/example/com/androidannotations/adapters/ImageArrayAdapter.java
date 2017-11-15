package claire.example.com.androidannotations.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.activities.GameActivity_;
import claire.example.com.androidannotations.toast.AffichageToast;

import static claire.example.com.androidannotations.toast.AffichageToast.afficherToast;

/**
 * Created by Claire on 09/11/2017.
 */

public class ImageArrayAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater layoutInflater;
    private GameActivity_ activity;
    private int resource;
    private List<T> objects;
    private Set<View> imagesChargees;
    private boolean messageErreurDejaAffiche;

    public ImageArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        this.activity = (GameActivity_) context;
        this.resource = resource;
        this.objects = objects;
        this.imagesChargees = new HashSet<>();
        this.messageErreurDejaAffiche = false;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        ImageView imageView;
        String imageUri = activity.urlRoot/*getResources().getString(R.string.url_root)*/ + objects.get(position);
        DisplayImageOptions options;
        ImageLoader imageLoader;
        //final LinearLayout loadingLayout = (LinearLayout) activity.findViewById(R.id.loading_layout);
        //final LinearLayout gameLayout = (LinearLayout) activity.findViewById(R.id.game_layout);

        if (convertView == null) {
            view = layoutInflater.inflate(resource, parent, false);
            imageView = view.findViewById(R.id.image);
            view.setTag(imageView);
        } else {
            imageView = (ImageView) view.getTag();
        }

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUri, imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imagesChargees.add(view);
                if (imagesChargees.size() == getCount()) {
                    // Le layout de chargement disparaît
                    activity.linearLayouts.get(0).setVisibility(View.GONE);

                    // Le layout du jeu devient visible
                    activity.linearLayouts.get(1).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (failReason.getType() == FailReason.FailType.IO_ERROR) {
                    // Le layout de chargement disparaît
                    activity.linearLayouts.get(0).setVisibility(View.GONE);

                    if(!messageErreurDejaAffiche) {
                        afficherToast(activity, AffichageToast.ERREUR_CONNEXION);
                        messageErreurDejaAffiche = true;
                    }
                }
            }
        });

        return view;
    }

}
