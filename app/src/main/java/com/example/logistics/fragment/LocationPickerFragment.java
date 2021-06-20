package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.logistics.viewmodel.LocViewModel;
import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class LocationPickerFragment extends Fragment implements PermissionsListener, OnMapReadyCallback {

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private Activity activity;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button selectLocationButton;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;
    private LocViewModel viewModel;
    private boolean startMode;

    public LocationPickerFragment(boolean startMode){
        this.startMode = startMode;
    }

    @Override
    public void onAttach(Context context) {
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
        View layout = inflater.inflate(R.layout.locationpicker, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Location Picker");
        // Initialize the mapboxMap view
        selectLocationButton = view.findViewById(R.id.select_location_button);
        selectLocationButton.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.star_command));
        selectLocationButton.setText("Select Location");
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        viewModel = new ViewModelProvider(requireActivity()).get(LocViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
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
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull final Style style) {
                enableLocationPlugin(style);

                // When user is still picking a location, we hover a marker above the mapboxMap in the center.
                // This is done by using an image view with the default marker found in the SDK. You can
                // swap out for your own marker image, just make sure it matches up with the dropped marker.
                hoveringMarker = new ImageView(activity);
                hoveringMarker.setImageResource(R.drawable.marker_red);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);

                // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                initDroppedMarker(style);

                // Button for user to drop marker or to pick marker back up.

                selectLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hoveringMarker.getVisibility() == View.VISIBLE) {

                            // Use the map target's coordinates to make a reverse geocoding search
                            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                            // Hide the hovering red hovering ImageView marker
                            hoveringMarker.setVisibility(View.INVISIBLE);
                            Toast toast = Toast.makeText(activity, "Location selected, you can redo your choice clicking CANCEL if you want", Toast.LENGTH_SHORT);
                            toast.show();
                            // Transform the appearance of the button to become the cancel button
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(getContext(), R.color.jet));
                            selectLocationButton.setText("Cancel");

                            // Show the SymbolLayer icon to represent the selected map location
                            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                                if (source != null) {
                                    source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                }
                                droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                if (droppedMarkerLayer != null) {
                                    droppedMarkerLayer.setProperties(visibility(VISIBLE));
                                }
                            }
                            Geocoder geoCoder = new Geocoder(activity, Locale.ITALY);
                            List<Address> locations = new ArrayList<>();
                            try {
                                locations = geoCoder.getFromLocation(mapTargetLatLng.getLatitude(), mapTargetLatLng.getLongitude(), 5);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(startMode){
                                viewModel.setStartLocation(locations.get(0));
                            }
                            else{
                                viewModel.setStopLocation(locations.get(0));
                            }

                        } else {
                            Toast toast = Toast.makeText(activity, "Remake your choice!", Toast.LENGTH_SHORT);
                            toast.show();
                            // Switch the button appearance back to select a location.
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(getContext(), R.color.star_command));
                            selectLocationButton.setText("Select Location");

                            // Show the red hovering ImageView marker
                            hoveringMarker.setVisibility(View.VISIBLE);

                            // Hide the selected location SymbolLayer
                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(NONE));
                            }
                        }
                    }
                });
            }
        });

    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        // Add the marker image to map
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.blue_marker, null);
        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
        loadedMapStyle.addImage("dropped-icon-image", mBitmap);
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {

        // Get an instance of the component. Adding in LocationComponentOptions is also an optional parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
                    activity, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(activity);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {

        }
    }
}
