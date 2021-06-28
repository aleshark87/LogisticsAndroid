package com.example.logistics.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.logistics.MainActivity;
import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclerdriver.CardItemDriver;
import com.example.logistics.viewmodel.HiredViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class DetailedMapFragment extends Fragment {
    public static String DETAILED_MAP_FRAGMENT = "Detailed_Map_Fragment";
    private Activity activity;
    //servirebbe il driver, ma nel caso della company no problem
    private MapView mapViewAvailable;
    private MapView mapViewInProgress;
    private List<CardItemCompany> available;
    private List<CardItemCompany> inProgress;
    private MapboxMap mapboxMapAv;
    private MapboxMap mapboxMapInP;
    private boolean fromDriver;
    private String formattedDate;

    public DetailedMapFragment(List<CardItemCompany> available, List<CardItemCompany> inProgress){
        this.available = available;
        this.inProgress = inProgress;
    }

    public DetailedMapFragment(List<CardItemCompany> available, List<CardItemCompany> inProgress, boolean fromDriver){
        this.available = available;
        this.inProgress = inProgress;
        this.fromDriver = fromDriver;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(activity, getString(R.string.mapbox_access_token));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.detailed_map_fragment, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity) activity, "Detailed Map View");
        mapViewAvailable = view.findViewById(R.id.availableMapView);
        mapViewAvailable.onCreate(savedInstanceState);
        mapViewAvailable.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                DetailedMapFragment.this.mapboxMapAv = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        for (CardItemCompany cardItemCompany : available) {
                            Point origin = Point.fromLngLat(cardItemCompany.getOriginLong(), cardItemCompany.getOriginLat());
                            Point destination = Point.fromLngLat(cardItemCompany.getDestinationLong(), cardItemCompany.getDestinationLat());
                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(new LatLng(cardItemCompany.getOriginLat(), cardItemCompany.getOriginLong()))
                                    .include(new LatLng(cardItemCompany.getDestinationLat(), cardItemCompany.getDestinationLong()))
                                    .build();

                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
                            String ROUTE_SOURCE_ID = "route-source-id" + cardItemCompany.getId();
                            String ICON_SOURCE_ID = "icon-source-id" + cardItemCompany.getId();
                            String ROUTE_LAYER_ID = "route-layer-id" + cardItemCompany.getId();
                            String ICON_LAYER_ID = "icon-layer-id" + cardItemCompany.getId();
                            String PIN_ICON_ID = "pin-icon-id" + cardItemCompany.getId();
                            Random random = new Random();
                            int nextInt = random.nextInt(0xffffff + 1);
                            String colorRandomLayer = String.format("#%06x", nextInt);
                            nextInt = random.nextInt(0xffffff + 1);
                            String colorRandomMarker = String.format("#%06x", nextInt);
                            initSource(style, cardItemCompany, ROUTE_SOURCE_ID, ICON_SOURCE_ID);
                            initLayers(style, ROUTE_SOURCE_ID, ROUTE_LAYER_ID, ICON_LAYER_ID, ICON_SOURCE_ID, colorRandomLayer, colorRandomMarker, PIN_ICON_ID);
                            // Get the directions route from the Mapbox Directions API
                            getRoute(mapboxMap, origin, destination, ROUTE_SOURCE_ID);
                        }
                    }
                });
            }
        });
        mapViewInProgress = view.findViewById(R.id.inProgressMapView);
        mapViewInProgress.onCreate(savedInstanceState);
        mapViewInProgress.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                DetailedMapFragment.this.mapboxMapInP = mapboxMap;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        for (CardItemCompany cardItemCompany : inProgress) {
                            Point origin = Point.fromLngLat(cardItemCompany.getOriginLong(), cardItemCompany.getOriginLat());
                            Point destination = Point.fromLngLat(cardItemCompany.getDestinationLong(), cardItemCompany.getDestinationLat());
                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(new LatLng(cardItemCompany.getOriginLat(), cardItemCompany.getOriginLong()))
                                    .include(new LatLng(cardItemCompany.getDestinationLat(), cardItemCompany.getDestinationLong()))
                                    .build();

                            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
                            String ROUTE_SOURCE_ID = "route-source-id" + cardItemCompany.getId();
                            String ICON_SOURCE_ID = "icon-source-id" + cardItemCompany.getId();
                            String ROUTE_LAYER_ID = "route-layer-id" + cardItemCompany.getId();
                            String ICON_LAYER_ID = "icon-layer-id" + cardItemCompany.getId();
                            String PIN_ICON_ID = "pin-icon-id" + cardItemCompany.getId();
                            Random random = new Random();
                            int nextInt = random.nextInt(0xffffff + 1);
                            String colorRandomLayer = String.format("#%06x", nextInt);
                            nextInt = random.nextInt(0xffffff + 1);
                            String colorRandomMarker = String.format("#%06x", nextInt);
                            initSource(style, cardItemCompany, ROUTE_SOURCE_ID, ICON_SOURCE_ID);
                            initLayers(style, ROUTE_SOURCE_ID, ROUTE_LAYER_ID, ICON_LAYER_ID, ICON_SOURCE_ID, colorRandomLayer, colorRandomMarker, PIN_ICON_ID);
                            // Get the directions route from the Mapbox Directions API
                            getRoute(mapboxMap, origin, destination, ROUTE_SOURCE_ID);
                        }
                    }
                });
            }
        });
        view.findViewById(R.id.productFilter).setOnClickListener(new View.OnClickListener() {

            private int lastClickedItem;

            @Override
            public void onClick(View v) {
                lastClickedItem = 0;
                String[] singleItems = {"Coal", "Iron", "Wood"};
                int checkedItem = 0;

                new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog)
                        .setTitle("Choose the product to filter")
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mapboxMapAv.getStyle(new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                for(CardItemCompany availableItem : DetailedMapFragment.this.available){
                                    if(!availableItem.getProductType().matches(getProductClicked())){
                                        removeRouteOnMap(style, availableItem.getId());
                                    }
                                    else{
                                        //se l'elemento non è in mappa bisogna rimetterlo
                                        if(!isRouteOnMap(style, availableItem.getId())){
                                            addRouteOnMap(style, availableItem.getId());
                                        }
                                    }
                                }
                            }

                            private void addRouteOnMap(Style mapStyle, int idCardItem){
                                String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                                setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                            }

                            private boolean isRouteOnMap(Style mapStyle, int idCardItem){
                                String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                if(mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)){
                                    return true;
                                }
                                else{
                                    return false;
                                }
                            }

                            private void removeRouteOnMap(Style mapStyle, int idCardItem){
                                String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                                setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                            }

                            private void setVisibilities(Layer layer, boolean visibility){
                                if (layer != null) {
                                    if(!visibility){
                                        if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                            layer.setProperties(visibility(NONE));
                                        }
                                    }
                                    else {
                                        if (NONE.equals(layer.getVisibility().getValue())) {
                                            layer.setProperties(visibility(VISIBLE));
                                        }
                                    }
                                }
                            }



                            private String getProductClicked(){
                                if(lastClickedItem == 0){
                                    return "Coal";
                                }
                                else{
                                    if(lastClickedItem == 1){
                                        return "Iron";
                                    }
                                    else{
                                        return "Wood";
                                    }
                                }
                            }

                        });
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mapboxMapAv.getStyle(new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull Style style) {
                                        for(CardItemCompany availableItem : DetailedMapFragment.this.available){
                                            if(!isRouteOnMap(style, availableItem.getId())){
                                                addRouteOnMap(style, availableItem.getId());
                                            }

                                        }
                                    }

                                    private void addRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                        setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                                        setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                                    }

                                    private boolean isRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        if(mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)){
                                            return true;
                                        }
                                        else{
                                            return false;
                                        }
                                    }

                                    private void removeRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                        setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                                        setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                                    }

                                    private void setVisibilities(Layer layer, boolean visibility){
                                        if (layer != null) {
                                            if(!visibility){
                                                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                                    layer.setProperties(visibility(NONE));
                                                }
                                            }
                                            else {
                                                if (NONE.equals(layer.getVisibility().getValue())) {
                                                    layer.setProperties(visibility(VISIBLE));
                                                }
                                            }
                                        }
                                    }



                                    private String getProductClicked(){
                                        if(lastClickedItem == 0){
                                            return "Coal";
                                        }
                                        else{
                                            if(lastClickedItem == 1){
                                                return "Iron";
                                            }
                                            else{
                                                return "Wood";
                                            }
                                        }
                                    }

                                });
                            }
                        })
                .setSingleChoiceItems(singleItems, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lastClickedItem = which;
                    }
                })
                .show();

            }
        });

        if(!fromDriver){
            view.findViewById(R.id.driverDateFilter).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(activity)
                            .setTitle("Impossible")
                            .setMessage("Available Transports cannot be taken by any drivers!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        }
        else{
            Button dateButtonAv = view.findViewById(R.id.driverDateFilter);
            dateButtonAv.setText("DATE FILTER");
            dateButtonAv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        view.findViewById(R.id.productFilterAv).setOnClickListener(new View.OnClickListener() {

            private int lastClickedItem;

            @Override
            public void onClick(View v) {
                lastClickedItem = 0;
                String[] singleItems = {"Coal", "Iron", "Wood"};
                int checkedItem = 0;

                new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog)
                        .setTitle("Choose the product to filter")
                        .setPositiveButton("Go", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mapboxMapInP.getStyle(new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull Style style) {
                                        for(CardItemCompany availableItem : DetailedMapFragment.this.inProgress){
                                            if(!availableItem.getProductType().matches(getProductClicked())){
                                                removeRouteOnMap(style, availableItem.getId());
                                            }
                                            else{
                                                //se l'elemento non è in mappa bisogna rimetterlo
                                                if(!isRouteOnMap(style, availableItem.getId())){
                                                    addRouteOnMap(style, availableItem.getId());
                                                }
                                            }
                                        }
                                    }

                                    private void addRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                        setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                                        setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                                    }

                                    private boolean isRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        if(mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)){
                                            return true;
                                        }
                                        else{
                                            return false;
                                        }
                                    }

                                    private void removeRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                        setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                                        setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                                    }

                                    private void setVisibilities(Layer layer, boolean visibility){
                                        if (layer != null) {
                                            if(!visibility){
                                                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                                    layer.setProperties(visibility(NONE));
                                                }
                                            }
                                            else {
                                                if (NONE.equals(layer.getVisibility().getValue())) {
                                                    layer.setProperties(visibility(VISIBLE));
                                                }
                                            }
                                        }
                                    }



                                    private String getProductClicked(){
                                        if(lastClickedItem == 0){
                                            return "Coal";
                                        }
                                        else{
                                            if(lastClickedItem == 1){
                                                return "Iron";
                                            }
                                            else{
                                                return "Wood";
                                            }
                                        }
                                    }

                                });
                            }
                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mapboxMapInP.getStyle(new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull Style style) {
                                        for(CardItemCompany availableItem : DetailedMapFragment.this.inProgress){
                                            if(!isRouteOnMap(style, availableItem.getId())){
                                                addRouteOnMap(style, availableItem.getId());
                                            }

                                        }
                                    }

                                    private void addRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                        setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                                        setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                                    }

                                    private boolean isRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        if(mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)){
                                            return true;
                                        }
                                        else{
                                            return false;
                                        }
                                    }

                                    private void removeRouteOnMap(Style mapStyle, int idCardItem){
                                        String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                        String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                        setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                                        setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                                    }

                                    private void setVisibilities(Layer layer, boolean visibility){
                                        if (layer != null) {
                                            if(!visibility){
                                                if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                                    layer.setProperties(visibility(NONE));
                                                }
                                            }
                                            else {
                                                if (NONE.equals(layer.getVisibility().getValue())) {
                                                    layer.setProperties(visibility(VISIBLE));
                                                }
                                            }
                                        }
                                    }



                                    private String getProductClicked(){
                                        if(lastClickedItem == 0){
                                            return "Coal";
                                        }
                                        else{
                                            if(lastClickedItem == 1){
                                                return "Iron";
                                            }
                                            else{
                                                return "Wood";
                                            }
                                        }
                                    }

                                });
                            }
                        })
                        .setSingleChoiceItems(singleItems, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lastClickedItem = which;
                            }
                        })
                        .show();

            }
        });

        if(!fromDriver){

            view.findViewById(R.id.driverDateFilter).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new MaterialAlertDialogBuilder(activity)
                            .setTitle("Impossible")
                            .setMessage("Available Transports cannot be taken by any drivers!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        }
        else{
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
                    mapboxMapAv.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            for(CardItemCompany availableItem : DetailedMapFragment.this.available){
                                if(!availableItem.getDate().split(",")[0].matches(formattedDate)){
                                    removeRouteOnMap(style, availableItem.getId());
                                }
                                else{
                                    //se l'elemento non è in mappa bisogna rimetterlo
                                    if(!isRouteOnMap(style, availableItem.getId())){
                                        addRouteOnMap(style, availableItem.getId());
                                    }
                                }
                            }
                        }

                        private void addRouteOnMap(Style mapStyle, int idCardItem){
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                        }

                        private boolean isRouteOnMap(Style mapStyle, int idCardItem){
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            if(mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)){
                                return true;
                            }
                            else{
                                return false;
                            }
                        }

                        private void removeRouteOnMap(Style mapStyle, int idCardItem){
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                        }

                        private void setVisibilities(Layer layer, boolean visibility){
                            if (layer != null) {
                                if(!visibility){
                                    if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(NONE));
                                    }
                                }
                                else {
                                    if (NONE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(VISIBLE));
                                    }
                                }
                            }
                        }
                    });
                }
            });

            datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapboxMapAv.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            for (CardItemCompany availableItem : DetailedMapFragment.this.available) {
                                if (!isRouteOnMap(style, availableItem.getId())) {
                                    addRouteOnMap(style, availableItem.getId());
                                }
                            }
                        }

                        private void addRouteOnMap(Style mapStyle, int idCardItem) {
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                        }

                        private boolean isRouteOnMap(Style mapStyle, int idCardItem) {
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            if (mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        private void removeRouteOnMap(Style mapStyle, int idCardItem) {
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                        }

                        private void setVisibilities(Layer layer, boolean visibility) {
                            if (layer != null) {
                                if (!visibility) {
                                    if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(NONE));
                                    }
                                } else {
                                    if (NONE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(VISIBLE));
                                    }
                                }
                            }
                        }
                    });
                }
            });

            Button dateButtonAv = view.findViewById(R.id.driverDateFilter);
            dateButtonAv.setText("DATE FILTER");
            dateButtonAv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePicker.show(getParentFragmentManager(), "tag");
                }
            });
        }

        if(!fromDriver) {

            view.findViewById(R.id.driverDateFilterAv).setOnClickListener(new View.OnClickListener() {

                private int lastClickedItem;
                private String[] names;

                @Override
                public void onClick(View v) {
                    lastClickedItem = 0;
                    HiredViewModel hiredViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(HiredViewModel.class);
                    hiredViewModel.getCardItems().observe((LifecycleOwner) activity, new Observer<List<CardItemDriver>>() {
                        @Override
                        public void onChanged(List<CardItemDriver> cardItemDrivers) {
                            int size = cardItemDrivers.size();
                            names = new String[size];
                            int i = 0;
                            for (CardItemDriver driver : cardItemDrivers) {
                                names[i] = driver.getDriverName();
                                i++;
                            }
                            new MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog)
                                    .setTitle("Choose the product to filter")
                                    .setPositiveButton("Go", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mapboxMapInP.getStyle(new Style.OnStyleLoaded() {
                                                @Override
                                                public void onStyleLoaded(@NonNull Style style) {
                                                    for (CardItemCompany availableItem : DetailedMapFragment.this.inProgress) {
                                                        if (!availableItem.getDriverName().matches(getProductClicked())) {
                                                            removeRouteOnMap(style, availableItem.getId());
                                                        } else {
                                                            //se l'elemento non è in mappa bisogna rimetterlo
                                                            if (!isRouteOnMap(style, availableItem.getId())) {
                                                                addRouteOnMap(style, availableItem.getId());
                                                            }
                                                        }
                                                    }
                                                }

                                                private void addRouteOnMap(Style mapStyle, int idCardItem) {
                                                    String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                                    String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                                    setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                                                    setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                                                }

                                                private boolean isRouteOnMap(Style mapStyle, int idCardItem) {
                                                    String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                                    if (mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)) {
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                }

                                                private void removeRouteOnMap(Style mapStyle, int idCardItem) {
                                                    String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                                    String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                                    setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                                                    setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                                                }

                                                private void setVisibilities(Layer layer, boolean visibility) {
                                                    if (layer != null) {
                                                        if (!visibility) {
                                                            if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                                                layer.setProperties(visibility(NONE));
                                                            }
                                                        } else {
                                                            if (NONE.equals(layer.getVisibility().getValue())) {
                                                                layer.setProperties(visibility(VISIBLE));
                                                            }
                                                        }
                                                    }
                                                }


                                                private String getProductClicked() {
                                                    return names[lastClickedItem];
                                                }

                                            });
                                        }
                                    })

                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mapboxMapInP.getStyle(new Style.OnStyleLoaded() {
                                                @Override
                                                public void onStyleLoaded(@NonNull Style style) {
                                                    for (CardItemCompany availableItem : DetailedMapFragment.this.inProgress) {
                                                        //se l'elemento non è in mappa bisogna rimetterlo
                                                        if (!isRouteOnMap(style, availableItem.getId())) {
                                                            addRouteOnMap(style, availableItem.getId());
                                                        }

                                                    }
                                                }

                                                private void addRouteOnMap(Style mapStyle, int idCardItem) {
                                                    String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                                    String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                                    setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                                                    setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                                                }

                                                private boolean isRouteOnMap(Style mapStyle, int idCardItem) {
                                                    String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                                    if (mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)) {
                                                        return true;
                                                    } else {
                                                        return false;
                                                    }
                                                }

                                                private void removeRouteOnMap(Style mapStyle, int idCardItem) {
                                                    String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                                                    String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                                                    setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                                                    setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                                                }

                                                private void setVisibilities(Layer layer, boolean visibility) {
                                                    if (layer != null) {
                                                        if (!visibility) {
                                                            if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                                                layer.setProperties(visibility(NONE));
                                                            }
                                                        } else {
                                                            if (NONE.equals(layer.getVisibility().getValue())) {
                                                                layer.setProperties(visibility(VISIBLE));
                                                            }
                                                        }
                                                    }
                                                }


                                                private String getProductClicked() {
                                                    return names[lastClickedItem];
                                                }

                                            });
                                        }
                                    })
                                    .setSingleChoiceItems(names, lastClickedItem, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            lastClickedItem = which;
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            });
        }
        else{
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
                    mapboxMapInP.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            for(CardItemCompany availableItem : DetailedMapFragment.this.inProgress){
                                if(!availableItem.getDate().split(",")[0].matches(formattedDate)){
                                    removeRouteOnMap(style, availableItem.getId());
                                }
                                else{
                                    //se l'elemento non è in mappa bisogna rimetterlo
                                    if(!isRouteOnMap(style, availableItem.getId())){
                                        addRouteOnMap(style, availableItem.getId());
                                    }
                                }
                            }
                        }

                        private void addRouteOnMap(Style mapStyle, int idCardItem){
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                        }

                        private boolean isRouteOnMap(Style mapStyle, int idCardItem){
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            if(mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)){
                                return true;
                            }
                            else{
                                return false;
                            }
                        }

                        private void removeRouteOnMap(Style mapStyle, int idCardItem){
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                        }

                        private void setVisibilities(Layer layer, boolean visibility){
                            if (layer != null) {
                                if(!visibility){
                                    if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(NONE));
                                    }
                                }
                                else {
                                    if (NONE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(VISIBLE));
                                    }
                                }
                            }
                        }
                    });
                }
            });

            datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapboxMapInP.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            for (CardItemCompany availableItem : DetailedMapFragment.this.inProgress) {
                                if (!isRouteOnMap(style, availableItem.getId())) {
                                    addRouteOnMap(style, availableItem.getId());
                                }
                            }
                        }

                        private void addRouteOnMap(Style mapStyle, int idCardItem) {
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), true);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), true);
                        }

                        private boolean isRouteOnMap(Style mapStyle, int idCardItem) {
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            if (mapStyle.getLayer(ROUTE_LAYER_ID).getVisibility().getValue().equals(VISIBLE)) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        private void removeRouteOnMap(Style mapStyle, int idCardItem) {
                            String ROUTE_LAYER_ID = "route-layer-id" + idCardItem;
                            String ICON_LAYER_ID = "icon-layer-id" + idCardItem;
                            setVisibilities(mapStyle.getLayer(ROUTE_LAYER_ID), false);
                            setVisibilities(mapStyle.getLayer(ICON_LAYER_ID), false);
                        }

                        private void setVisibilities(Layer layer, boolean visibility) {
                            if (layer != null) {
                                if (!visibility) {
                                    if (VISIBLE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(NONE));
                                    }
                                } else {
                                    if (NONE.equals(layer.getVisibility().getValue())) {
                                        layer.setProperties(visibility(VISIBLE));
                                    }
                                }
                            }
                        }
                    });
                }
            });

            Button dateButtonAv = view.findViewById(R.id.driverDateFilterAv);
            dateButtonAv.setText("DATE FILTER");
            dateButtonAv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePicker.show(getParentFragmentManager(), "tag");
                }
            });
        }
    }

    private void initSource(@NonNull Style loadedMapStyle, CardItemCompany cardItemCompany, String ROUTE_SOURCE_ID, String ICON_SOURCE_ID) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(cardItemCompany.getOriginLong(), cardItemCompany.getOriginLat())),
                Feature.fromGeometry(Point.fromLngLat(cardItemCompany.getDestinationLong(), cardItemCompany.getDestinationLat()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    private void initLayers(@NonNull Style loadedMapStyle, String ROUTE_SOURCE_ID, String ROUTE_LAYER_ID,
                            String ICON_LAYER_ID, String ICON_SOURCE_ID, String colorLayer, String colorMarker,
                            String PIN_ICON_ID) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor(colorLayer))
        );
        loadedMapStyle.addLayer(routeLayer);
        // Add the marker icon image to the map
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(activity, R.drawable.marker_red);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(colorMarker));
        loadedMapStyle.addImage(PIN_ICON_ID, Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                wrappedDrawable)));

        // Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination, String ROUTE_SOURCE_ID) {
        MapboxDirections client = MapboxDirections.builder()
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
                DirectionsRoute currentRoute = response.body().routes().get(0);
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

    @Override
    public void onResume() {
        super.onResume();
        mapViewAvailable.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapViewAvailable.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapViewAvailable.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewAvailable.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapViewAvailable.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapViewAvailable.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewAvailable.onLowMemory();
    }
}
