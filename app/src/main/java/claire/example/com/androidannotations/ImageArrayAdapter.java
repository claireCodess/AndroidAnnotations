package claire.example.com.androidannotations;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import static claire.example.com.androidannotations.MainActivity.cheminsFichiersImages;
import static claire.example.com.androidannotations.MainActivity.nombreImagesChargees;

/**
 * Created by Claire on 09/11/2017.
 */

public class ImageArrayAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;
    private List<T> objects;
    LinearLayout gameLayout;
    LinearLayout loadingLayout;

    ImageArrayAdapter(Context context, int resource, List<T> objects, LinearLayout gameLayout, LinearLayout loadingLayout) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.gameLayout = gameLayout;
        this.loadingLayout = loadingLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView;
        String imageUri = context.getResources().getString(R.string.url_root) + (String)objects.get(position);
        DisplayImageOptions options;
        ImageLoader imageLoader;

        if (convertView == null) {
            view = layoutInflater.inflate(resource, parent, false);
            imageView = ((ImageView) view.findViewById(R.id.image));
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
                nombreImagesChargees++;

                if(nombreImagesChargees == cheminsFichiersImages.size()) {
                    loadingLayout.setVisibility(View.GONE);
                    gameLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}
