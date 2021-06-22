package com.example.logistics.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.logistics.R;
import com.example.logistics.Utilities;
import com.example.logistics.recyclerdriver.CardItemDriver;
import com.example.logistics.viewmodel.NotHiredViewModel;
import com.example.logistics.viewmodel.DriverPhotoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.logistics.Utilities.REQUEST_IMAGE_CAPTURE;

public class NewDriverFragment extends Fragment {

    public static final String NEW_DRIVER_FRAGMENT = "New_Driver_Fragment";
    private NotHiredViewModel notHiredViewModel;
    private DriverPhotoViewModel viewModelPhoto;
    private Activity activity;
    private TextInputEditText editTextName;
    private TextInputEditText editTextCapacity;
    private Button captureButton;
    private MaterialButton submitButton;
    private ImageView imgView;
    private TextView hourStartTV;
    private TextView hourFinishTV;
    private Button buttonStartHour;
    private Button buttonFinishHour;
    private boolean timeStartSet = false;
    private boolean timeFinishSet = false;
    private String formattedTimeStart = "";
    private String formattedTimeFinish = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.new_carrier, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.setUpToolbar((AppCompatActivity)activity, "New Driver");
        getItemsFromView(view);
        viewModelPhoto = new ViewModelProvider((ViewModelStoreOwner) activity).get(DriverPhotoViewModel.class);
        notHiredViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(NotHiredViewModel.class);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there is a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        viewModelPhoto.getPhoto().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                imgView.setImageBitmap(bitmap);
            }
        });

        setTimePickers();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = editTextName.getText().toString();
                String capacityString = editTextCapacity.getText().toString();
                if(checkTimings(formattedTimeStart, formattedTimeFinish)){
                    if(!nameString.matches("") && !capacityString.matches("") && timeStartSet && timeFinishSet){
                        Bitmap bitmap = viewModelPhoto.getPhoto().getValue();
                        String imageUriString = "profile";
                        if(bitmap != null){
                            try {
                                imageUriString = String.valueOf(saveImage(bitmap, activity));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        notHiredViewModel.addCardItem(new CardItemDriver(imageUriString,
                                nameString, Integer.parseInt(capacityString),
                                formattedTimeStart + "_" + formattedTimeFinish, false));
                        Bitmap bitmapProfile = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
                        viewModelPhoto.setPhoto(bitmapProfile);
                        editTextCapacity.setText("");
                        editTextName.setText("");
                        timeStartSet = false; hourStartTV.setText("Hour not set");
                        timeFinishSet = false; hourFinishTV.setText("Hour not set");
                        Toast.makeText(activity, "Added succesfully!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(activity, "not added", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(activity, "Time not set properly", Toast.LENGTH_SHORT).show();
                    timeStartSet = false; hourStartTV.setText("Hour not set");
                    timeFinishSet = false; hourFinishTV.setText("Hour not set");
                }

            }
        });
    }

    private boolean checkTimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method called to save the image taken as a file in the gallery
     * @param bitmap the image taken
     * @throws IOException if there are some issue with the creation of the image file
     * @return the Uri of the image saved
     */
    private Uri saveImage(Bitmap bitmap, Activity activity) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date());
        String name = "JPEG_" + timeStamp + "_.png";

        ContentResolver resolver = activity.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.d("LAB-AddFragment", String.valueOf(imageUri));
        OutputStream fos = resolver.openOutputStream(imageUri);

        //for the jpeg quality, it goes from 0 to 100
        //for the png, the quality is ignored
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (fos != null) {
            fos.close();
        }
        return imageUri;
    }

    private void setTimePickers(){
        MaterialTimePicker timePickerStart =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select start time")
                        .build();
        MaterialTimePicker timePickerFinish =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select finish time")
                        .build();


        timePickerStart.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStartSet = true;
                formattedTimeStart = timePickerStart.getHour() + ":" + timePickerStart.getMinute();
                hourStartTV.setText("Time set " + formattedTimeStart);
            }
        });

        timePickerFinish.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeFinishSet = true;
                formattedTimeFinish = timePickerFinish.getHour() + ":" + timePickerFinish.getMinute();
                hourFinishTV.setText("Time set " + formattedTimeFinish);
            }
        });

        buttonStartHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerStart.show(getParentFragmentManager(), "tag");
            }
        });

        buttonFinishHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerFinish.show(getParentFragmentManager(), "tag");
            }
        });
    }

    private void getItemsFromView(View view){
        editTextName = view.findViewById(R.id.editTextName);
        editTextCapacity = view.findViewById(R.id.editTextCapacity);
        captureButton = view.findViewById(R.id.captureButton);
        submitButton = view.findViewById(R.id.submitButtonDriver);
        imgView = view.findViewById(R.id.imageView);
        hourStartTV = view.findViewById(R.id.hourSetStartTV);
        hourFinishTV = view.findViewById(R.id.hourSetFinishTV);
        buttonStartHour = view.findViewById(R.id.buttonStartHour);
        buttonFinishHour = view.findViewById(R.id.buttonFinishHour);
    }
}
