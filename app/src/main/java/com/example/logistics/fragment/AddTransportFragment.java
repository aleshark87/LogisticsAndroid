package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.logistics.viewmodel.CardViewModelCompany;
import com.example.logistics.viewmodel.LocViewModel;
import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class AddTransportFragment extends Fragment implements OnMapReadyCallback {
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private Activity activity;
    private LocViewModel locViewModel;
    private RadioGroup radioGroup;
    private MapView mapView;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    private Address originAddress;
    private Address destinationAddress;
    private TextInputEditText titleEdit;
    private TextInputEditText quantityEdit;
    private String formattedDate;
    private String formattedTime;
    private boolean dateSet = false;
    private boolean timeSet = false;

    //TODO temare timepicker
    //TODO autisti(recyclerview con lista trasporti disponibili(nuovo autista)
    //TODO supermappa filtrata azienda(per merce, per autista, per data)
    //TODO check geocoder lat long(se uno sceglie il mare(latlong troppo diversa da quella scelta)
    //TODO sistemare quando si chiede il permesso della posizione


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.addtransport, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Add Transport");
        titleEdit = view.findViewById(R.id.editTitle);
        quantityEdit = view.findViewById(R.id.quantityEditText);
        radioGroup = view.findViewById(R.id.radioGroup);
        locViewModel = new ViewModelProvider(requireActivity()).get(LocViewModel.class);
        setLocationListeners(view);
        CardViewModelCompany cardViewModelCompany = new ViewModelProvider((ViewModelStoreOwner) activity).get(CardViewModelCompany.class);
        mapView = view.findViewById(R.id.transportMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Button dateButton = view.findViewById(R.id.dateButton);
        Button timeButton = view.findViewById(R.id.hourButton);
        TextView resultDate = view.findViewById(R.id.labelTimeResult);
        setDateButtonListeners(dateButton, timeButton, resultDate);
        view.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = titleEdit.getText().toString();
                String quantityText = quantityEdit.getText().toString();
                if(originAddress != null && destinationAddress != null && !titleText.matches("") && !quantityText.matches("") && timeSet && dateSet) {
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    RadioButton radioButton = (RadioButton) view.findViewById(selectedId);
                    int photoId = R.drawable.carbone_710x355;
                    String productType = "Coal";
                    if(radioButton.getText().toString().matches("Iron")){
                        photoId = R.drawable.iron;
                        productType = "Iron";
                    }
                    else{
                        if(radioButton.getText().toString().matches("Wood")){
                            photoId = R.drawable.wood;
                            productType = "Wood";
                        }
                    }
                    cardViewModelCompany.addCardItem(
                            new CardItemCompany(photoId, titleText,
                                    originAddress.getLatitude(), originAddress.getLongitude(),
                                    destinationAddress.getLatitude(), destinationAddress.getLongitude(),
                                    originAddress.getLocality(), destinationAddress.getLocality(),
                                    formattedDate + ", " + formattedTime,
                                    productType, Integer.parseInt(quantityText)));
                    titleEdit.getText().clear();
                    Toast.makeText(activity, "Added succesfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(activity, "You are missing some fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDateButtonListeners(Button dateButton, Button timeButton, TextView resultDateView){
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setTheme(R.style.DatePicker)
                .build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.setTimeInMillis((Long)selection);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                formattedDate = format.format(utc.getTime());
                dateSet = true;
                if(timeSet){
                    resultDateView.setText("Date set " + formattedDate + ", Time set " + formattedTime);
                }
                else{
                    resultDateView.setText("Date set " + formattedDate + ", Time not set");
                }
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getParentFragmentManager(), "tag");
            }
        });
        MaterialTimePicker timePicker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select departure time")
                        .build();
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show(getParentFragmentManager(), "tag");
            }
        });
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSet = true;
                formattedTime = timePicker.getHour() + ":" + timePicker.getMinute();
                if(dateSet){
                    resultDateView.setText("Date set " + formattedDate + ", Time set " + formattedTime);
                }
                else{
                    resultDateView.setText("Date not set, Time set " + formattedTime);
                }
            }
        });
    }

    private void setLocationListeners(View view){
        locViewModel.getStartLocation().observe((AppCompatActivity)activity, loc -> {
            TextView tv = view.findViewById(R.id.labelPlaceDeparture);
            tv.setText(loc.getLocality());
            originAddress = loc;
        });

        locViewModel.getStopLocation().observe((AppCompatActivity)activity, loc -> {
            TextView tv = view.findViewById(R.id.labelPlaceArrive);
            tv.setText(loc.getLocality());
            destinationAddress = loc;
        });

        view.findViewById(R.id.startLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity)activity, new LocationPickerFragment(true), "LocationPickerFragment");
            }
        });
        view.findViewById(R.id.stopLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.insertFragment((AppCompatActivity)activity, new LocationPickerFragment(false), "LocationPickerFragment");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                AtomicBoolean startLocation = new AtomicBoolean(false);
                AtomicBoolean arriveLocation = new AtomicBoolean(false);

                locViewModel.getStartLocation().observe((AppCompatActivity)activity, loc -> {
                    startLocation.set(true);
                });

                locViewModel.getStopLocation().observe((AppCompatActivity)activity, loc -> {
                    arriveLocation.set(true);
                });

                if(startLocation.get() && arriveLocation.get()){
                    origin = Point.fromLngLat(originAddress.getLongitude(), originAddress.getLatitude());
                    destination = Point.fromLngLat(destinationAddress.getLongitude(), destinationAddress.getLatitude());
                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(new LatLng(origin.latitude(), origin.longitude()))
                            .include(new LatLng(destination.latitude(), destination.longitude()))
                            .build();

                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
                    initSource(style);
                    initLayers(style);
                    // Get the directions route from the Mapbox Directions API
                    getRoute(mapboxMap, origin, destination);
                }
            }
        });
    }

    /**
     * Add the route and marker sources to the map
     */
    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);
// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.marker_red))));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     * @param mapboxMap the Mapbox map object that the route will be drawn on
     * @param origin      the starting point of the route
     * @param destination the desired finish point of the route
     */
    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                if (response.body() == null) {
                    return;
                } else if (response.body().routes().size() < 1) {
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            // Create a LineString with the directions route's geometry and
                            // reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(activity, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
