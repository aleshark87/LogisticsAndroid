package com.example.logistics;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistics.fragment.HomeFragment;

import java.io.InputStream;

public class Utilities {
    public static void insertFragment(AppCompatActivity activity, Fragment fragment, String tag){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container_view, fragment, tag);

        if(!(fragment instanceof HomeFragment)){
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    public static Bitmap getImageBitmap(Activity activity, Uri currentPhotoUri){
        ContentResolver resolver = activity.getApplicationContext()
                .getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(currentPhotoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setUpToolbar(AppCompatActivity activity, String title) {
        Toolbar toolbar = activity.findViewById(R.id.topAppBar);
        toolbar.setTitle(title);
    }
}
