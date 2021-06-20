package com.example.logistics.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclercompany.CardItemCompany;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

public class CardMapViewFragment extends Fragment implements OnMapReadyCallback{

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private Activity activity;
    private CardItemCompany cardItemCompany;
    private MapView mapView;
    private TextView informationTV;

    public CardMapViewFragment(CardItemCompany item){
        this.cardItemCompany = item;
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
        View layout = inflater.inflate(R.layout.card_map_view, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity)activity, "Card Map View");
        mapView = view.findViewById(R.id.cardMapView);
        informationTV = view.findViewById(R.id.informationCardMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Point origin = Point.fromLngLat(cardItemCompany.getOriginLong(), cardItemCompany.getOriginLat());
                Point destination = Point.fromLngLat(cardItemCompany.getDestinationLong(), cardItemCompany.getDestinationLat());
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(cardItemCompany.getOriginLat(), cardItemCompany.getOriginLong()))
                        .include(new LatLng(cardItemCompany.getDestinationLat(), cardItemCompany.getDestinationLong()))
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
                initSource(style);
                initLayers(style);
                // Get the directions route from the Mapbox Directions API
                getRoute(mapboxMap, origin, destination);
            }
        });
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(cardItemCompany.getOriginLong(), cardItemCompany.getOriginLat())),
                Feature.fromGeometry(Point.fromLngLat(cardItemCompany.getDestinationLong(), cardItemCompany.getDestinationLat()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

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

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
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
                                informationTV.setText(msgLabelBuilder(currentRoute));
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

    private String msgLabelBuilder(DirectionsRoute route){
        String msg;
        int distanceRounded = (int)Math.round(route.distance() / 1000);
        List<String> timeRoundedResults = timeRounder(route.duration());
        int numberOfHours = Integer.parseInt(timeRoundedResults.get(1).split(":")[0]);
        int numberOfMinutes = Integer.parseInt(timeRoundedResults.get(1).split(":")[1]);
        String dateStart = cardItemCompany.getDate().split(" ")[1];
        Log.d("tag", dateStart.split(":")[1]);
        int startHours = Integer.parseInt(dateStart.split(":")[0]);
        int startMinutes = Integer.parseInt(dateStart.split(":")[1]);
        msg = "The distance is " + distanceRounded + " km and it will be " + timeRounder(route.duration()).get(0) +
                ". Estimated Time of Arrive : " + arriveTime(startHours, startMinutes, numberOfHours, numberOfMinutes);

        return msg;
    }

    private String arriveTime(int startHour, int startMinutes, int hoursAdd, int minutesAdd){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("h:mm a");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,startHour);
        cal.set(Calendar.MINUTE,startMinutes);
        cal.add(Calendar.HOUR, hoursAdd);
        cal.add(Calendar.MINUTE, minutesAdd);
        Date d = cal.getTime();

        return dateFormat.format(d);
    }

    private List<String> timeRounder(Double secondsDuration){
        List<String> stringResult = new ArrayList<>();
        int hourDuration = 0;
        int minutesDuration = (int)Math.round(secondsDuration/60);
        String timeRounded = "";
        if(minutesDuration >= 60){
            while(minutesDuration >= 60){
                hourDuration++;
                minutesDuration -= 60;
            }
            if(hourDuration > 1){
                if(minutesDuration != 1){
                    timeRounded = hourDuration + " hours and " + minutesDuration + " minutes long";
                }
                else{
                    timeRounded = hourDuration + " hours and " + "one minute long";
                }
            }
            else{
                if(minutesDuration != 1){
                    timeRounded = hourDuration + " hour and " + minutesDuration + " minutes long";
                }
                else{
                    timeRounded = hourDuration + " hour and " + " one minute long";
                }
            }
        }
        else{
            timeRounded = minutesDuration + " minutes long";
        }
        stringResult.add(0, timeRounded);
        stringResult.add(1, hourDuration + ":" + minutesDuration);
        return stringResult;
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
}
