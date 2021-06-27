package com.example.logistics.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.database.CardItemRepo;
import com.example.logistics.recyclercompany.CardItemCompany;
import com.example.logistics.recyclerdriver.CardItemDriver;

import org.json.JSONException;
import org.json.JSONObject;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

import static android.Manifest.permission_group.CAMERA;
import static com.example.logistics.fragment.CardMapViewFragment.CARD_MAP_FRAGMENT;


public class QrReaderFragment extends Fragment {
    public static final String QR_FRAGMENT = "Qr-Fragment";
    private Activity activity;
    private ScannerLiveView camera;
    private TextView scannedTV;
    private Context context;
    private CardItemRepo repo;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.qr_reader_fragment, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repo = new CardItemRepo(activity.getApplication());
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, 200);
        }

        // initialize scannerLiveview and textview.
        scannedTV = view.findViewById(R.id.idTVscanned);
        camera = (ScannerLiveView) view.findViewById(R.id.camview);

        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
            }

            @Override
            public void onScannerError(Throwable err) {
            }

            @Override
            public void onCodeScanned(String data) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                scannedTV.setText(data);
                JSONObject json = null;
                try {
                    json = new JSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    CardItemCompany cardItem = new CardItemCompany(json.getDouble("originLat"),
                            json.getDouble("originLong"), json.getDouble("destinationLat"),
                            json.getDouble("destinationLong"), json.getString("date"));
                    Utilities.insertFragment((AppCompatActivity)activity, new CardMapViewFragment(cardItem, false, true, false), CARD_MAP_FRAGMENT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkPermission() {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        int camera_permission = ContextCompat.checkSelfPermission(context, CAMERA);
        return camera_permission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        // this method is to request
        // the runtime permission.
        int PERMISSION_REQUEST_CODE = 200;
        ActivityCompat.requestPermissions((Activity)context, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // this method is called when user
        // allows the permission to use camera.
        if (grantResults.length > 0) {
            boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraaccepted) {
                Toast.makeText(activity, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Permission Denined \n You cannot use app without providing permssion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        // 0.5 is the area where we have
        // to place red marker for scanning.
        decoder.setScanAreaPercent(0.8);
        // below method will set secoder to camera.
        camera.setDecoder(decoder);
        camera.startScanner();
    }

    @Override
    public void onPause() {
        // on app pause the
        // camera will stop scanning.
        camera.stopScanner();
        super.onPause();
    }
}
