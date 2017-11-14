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
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import claire.example.com.androidannotations.R;
import claire.example.com.androidannotations.activities.GameActivity_;

/**
 * Created by Claire on 09/11/2017.
 */

public class ImageArrayAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater layoutInflater;
    private GameActivity_ activity;
    private int resource;
    private List<T> objects;

    public ImageArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        this.activity = (GameActivity_) context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            /*imageView.setMaxHeight(80);
            imageView.setMaxWidth(80);*/
            view.setTag(imageView);
        } else {
            imageView = (ImageView) view.getTag();
        }

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().build();
        imageLoader.displayImage(imageUri, imageView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if(position == activity.getCheminsFichiersImages().size()-1) {
                // Le layout de chargement disparaît
                activity.linearLayouts.get(0).setVisibility(View.GONE);

                // Le layout du jeu devient visible
                activity.linearLayouts.get(1).setVisibility(View.VISIBLE);
            }
            }
        });

        return view;
    }
}
