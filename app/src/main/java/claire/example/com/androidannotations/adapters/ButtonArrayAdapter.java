package claire.example.com.androidannotations.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import claire.example.com.androidannotations.R;

// Etape 2 : remplacer "MainActivity" par "MainActivity_"
import claire.example.com.androidannotations.activities.MainActivity;

/**
 * Created by Claire on 13/11/2017.
 */

public class ButtonArrayAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater layoutInflater;

    // Etape 2 : remplacer "MainActivity" par "MainActivity_"
    private MainActivity activity;

    private int resource;
    private List<T> objects;

    public ButtonArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);

        // Etape 2 : remplacer "MainActivity" par "MainActivity_"
        this.activity = (MainActivity) context;

        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Button button;

        if (convertView == null) {
            view = layoutInflater.inflate(resource, parent, false);
            button = view.findViewById(R.id.bouton_niveau);
            view.setTag(button);
        } else {
            button = (Button) view.getTag();
        }

        button.setText((String)objects.get(position));
        button.setTag(activity.idNiveau/*getResources().getString(R.string.identifiant_niveau)*/ + position);
        button.setOnClickListener(activity.onClickListener);

        return view;
    }

}
