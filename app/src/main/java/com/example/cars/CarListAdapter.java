package com.example.cars;

// Ahmed Alotaibi,
// An adapter that retrieves data from
// the data set and for generating View objects based on that data.
// The generated View objects are then used to populate
// any adapter view that is bound to the adapter.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.List;

// A abstract class that implements the interface
public class CarListAdapter extends BaseAdapter {
    private final int makeId;
    private Context context;
    private List<Car> cars;

    // LayoutInflater is used to instantiate layout
    // XML file into its corresponding View objects.
    LayoutInflater inflter;

    // Constructor the the objects.
    public CarListAdapter(Context context, List<Car> cars, int makeId) {
        this.context = context;
        this.cars = cars;
        this.makeId = makeId;
        // Get the inflater in the constructor.
        inflter = (LayoutInflater.from(context));
    }

    @Override
    // Indicates to Android how many items (or rows)
    // are in the data set that will be presented in the AdapterView.
    public int getCount() {
        return cars.size();
    }

    @Override
    // get the data item associated with the item (or row) from the AdapterView passed as a
    // parameter to the method.
    // This method will be used by Android to fetch the appropriate data to
    // build the item/row in the AdapterView.
    public Object getItem(int i) {
        return cars.get(i);
    }

    @Override
    // This method returns the data set’s id for a item/row position of the Adapter.
    // Typically, the data set id matches the AdapterView rows
    // so this method just returns the same value.
    public long getItemId(int i) {
        return i;
    }

    @Override
    // This method creates the View (which may be a single View component
    // like a TextView or a complex set of widgets in a layout)
    // that displays the data for the specified (by position) item/row in the Adapter.
    public View getView(int i, View view, ViewGroup viewGroup) {

        // Inflate the xml which gives the view
        view = inflter.inflate(R.layout.car_layout, null);
        // Get the widget with the id name which is defined in the xml
        // of the row
        TextView carYear = view.findViewById(R.id.carYear);
        TextView carName = view.findViewById(R.id.carName);
        ImageView carMakeImg = view.findViewById(R.id.carMakeImg);

        // Populate the row's xml with info from the item.
        carName.setText(("Make: " +cars.get(i).getName()));
        carYear.setText(("Year: " +cars.get(i).getYear()));
        //Adding the logs into the CarMakes
        carMakeImg.setImageBitmap(CarMakesListAdapter.getBitmapFromAssets(context, makeId + ".jpg"));

        // Return the generated view
        return view;}}
