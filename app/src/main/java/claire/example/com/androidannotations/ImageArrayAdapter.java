package claire.example.com.androidannotations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Claire on 09/11/2017.
 */

public class ImageArrayAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater layoutInflater;
    private Context context;
    private int resource;
    private List<T> objects;

    ImageArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView;
        if (convertView == null) {
            view = layoutInflater.inflate(resource, parent, false);
            imageView = ((ImageView) view.findViewById(R.id.image));
            /*imageView.setMaxHeight(80);
            imageView.setMaxWidth(80);*/
            view.setTag(imageView);
        } else {
            imageView = (ImageView) view.getTag();
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(context.getResources().getString(R.string.url_root) + (String)objects.get(position), imageView);

        return view;
    }
}
