package ch.epfl.sweng.project.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.sweng.project.CreateActivity;
import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;


public class UserImageFragment extends DialogFragment {

    ImageView validationImage;

    ProgressBar uploadProgress;

    Object semaphore = new Object();

    private ImageProvider mImageProvider;
    private DataProvider mDataProvider;

    private LinearLayout rootView;

    Button okButton;
    Button editButton;

    private boolean uploadFinished = false;

    TextView uploadRateText;

    Bitmap imageBitmap;

    ImageView userImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (LinearLayout) inflater.inflate(R.layout.user_profile_image, container, false);

        uploadProgress = (ProgressBar) rootView.findViewById(R.id.uploadProgress);
        uploadProgress.setMax(100);

        uploadRateText = (TextView) rootView.findViewById(R.id.userImageUploadRate);
        okButton = (Button) rootView.findViewById(R.id.userImageOk);
        okButton = (Button) rootView.findViewById(R.id.userImageEdit);

        validationImage = (ImageView) rootView.findViewById(R.id.uploadedImage);

        synchronized (semaphore) {
            userImageView = (ImageView) rootView.findViewById(R.id.userImage);
            if(imageBitmap != null) {
                setImageWithProperScale(userImageView, imageBitmap);
              }
        }

        getDialog().setTitle(R.string.event_filter_title);

        //dismiss();

        return rootView;
    }



    ImageProvider.uploadListener uploadListener = new ImageProvider.uploadListener() {
        @Override
        public void uploadFailed() {
            //TODO Implements on failed
        }

        @Override
        public void uploadSuccessful(Uri uploadedFileUri) {

        }

        @Override
        public void uploadProgress(Uri fileUri, long bytesTransferred, long totalBytesCount) {

        }
    };


    public void setImageProvider(ImageProvider imageProvider) {
        this.mImageProvider = imageProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.mDataProvider = dataProvider;
    }

    public void setImage(Bitmap image) {
        synchronized (semaphore) {
            this.imageBitmap = image;
            if(userImageView != null) {
                setImageWithProperScale(userImageView, imageBitmap);
            }
        }
    }

    private void setImageWithProperScale(final ImageView userImageView, final Bitmap imageBitmap) {
        userImageView.post(new Runnable() {
            @Override
            public void run() {
                int imageHeight = imageBitmap.getHeight();
                int imageWidth = imageBitmap.getHeight();
                int height = userImageView.getHeight();
                int width = userImageView.getHeight();
                double scale1 = height/(double)imageHeight;
                double scale2 = width/(double)imageWidth;
                final double scale = Math.min(scale1, scale2);
                userImageView.setImageBitmap(
                        Bitmap.createScaledBitmap(imageBitmap,
                        (int) (imageBitmap.getWidth()*scale),
                        (int) (imageBitmap.getHeight()*scale),
                        false));
            }
        });
    }
}