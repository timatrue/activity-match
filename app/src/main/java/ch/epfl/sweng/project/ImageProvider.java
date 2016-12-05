package ch.epfl.sweng.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;


/**
 * Created by nathan on 06.11.16.
 */

public class ImageProvider {


    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef;

    public ImageProvider(){
        // Create a storage reference from our app
        storageRef = storage.getReferenceFromUrl("gs://activitymatch-a370d.appspot.com");
    }


    public void downloadImage(Context context, String folder, LinearLayout imageLayout, List<String> imagesList) {
        ///
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int dp = context.getResources().getDimensionPixelSize(R.dimen.imageGalleryHeight);

        for(String imageName: imagesList)  {
            // Reference to an image file in Firebase Storage
            StorageReference storageReference = storageRef.child("images/" + folder + "/" + imageName);

            ImageView imageView = new ImageView(context);
            //imageView.setAdjustViewBounds(true);

            imageLayout.addView(imageView);

            // Load the image using Glide
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .override(screenWidth,dp)
                    .centerCrop()
                    .into(imageView);
        }

    }

    public void previewImage(final Context context,  String folder, final ImageView imageView, String imageName){

        StorageReference storageReference = storageRef.child("images/" + folder + "/" + imageName);

        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });

    }

    public void UploadImage(final Uri imageUri, String folder, final uploadListener listener) {
        //"path/to/mountains.jpg"
        // File or Blob

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();

        // Upload file and metadata to the path 'images/mountains.jpg'
        UploadTask uploadTask = storageRef.child("images/" + folder + "/" + imageUri.getLastPathSegment()).putFile(imageUri, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                listener.uploadProgress(imageUri, taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.uploadFailed();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                listener.uploadSuccessful(imageUri);
            }
        });
    }

    public interface uploadListener {
        void uploadFailed();
        void uploadSuccessful(Uri uploadedFileUri);
        void uploadProgress(Uri fileUri, long bytesTransferred, long totalBytesCount);
    }

}
