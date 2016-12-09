package ch.epfl.sweng.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.uiobjects.SquareImageView;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;


/**
 * Created by jeremie on 09.11.16.
 */

public class MockImageProvider {

    @Mock
    ImageProvider mockImageProvider;


    public ImageProvider getMockImageProvider(){

        mockImageProvider = Mockito.mock(ImageProvider.class);
        initUploadImage();

        return mockImageProvider;
    }

    public void initdownloadImage(final Drawable drawable) {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Context context = (Context) args[0];
                String folder = (String) args[1];
                LinearLayout imageLayout = (LinearLayout) args[2];
                List<String> imagesList = (List<String>) args[2];

                for(String imageName: imagesList)  {
                    ImageView imageView = new ImageView(context);
                    imageLayout.addView(imageView);
                    imageView.setImageDrawable(drawable);
                }

                return null;
            }
        }).when(mockImageProvider).downloadImage(any(Context.class), any(String.class), any(LinearLayout.class), any(List.class));
    }

    public void initdownloadUserImage(final Drawable drawable) {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Context context = (Context) args[0];
                ImageView imageView = (ImageView) args[3];

                imageView = new ImageView(context);
                imageView.setImageDrawable(drawable);

                return null;
            }
        }).when(mockImageProvider).downloadUserImage(any(Context.class), any(String.class), any(String.class), any(ImageView.class), any(ImageProvider.downloadListener.class));
    }



    public void initpreviewImage(final Drawable previewImage){

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Context context = (Context) args[0];
                String folder = (String) args[1];
                ImageView imageView = (ImageView) args[2];
                String imageName = (String) args[2];

                Bitmap resource = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_icon);

                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);

                imageView.setImageDrawable(previewImage);

                return null;
            }
        }).when(mockImageProvider).previewImage(any(Context.class), any(String.class), any(ImageView.class), any(String.class));
    }

    public void initUploadImage() {

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                final Uri imageUri = (Uri) args[0];
                String folder = (String) args[1];
                final ImageProvider.uploadListener listener = (ImageProvider.uploadListener) args[2];

                int time = (int) (Math.random() * 10000);

                final Handler handler = new Handler();
                Runnable updateRunnable;

                updateRunnable = new Runnable() {
                    @Override
                    public void run() {
                        listener.uploadProgress(imageUri, 10, 10);
                        listener.uploadSuccessful(imageUri);
                    }
                };

                handler.postDelayed(updateRunnable, time);

                return null;
            }
        }).when(mockImageProvider).UploadImage(any(Uri.class), any(String.class), any(ImageProvider.uploadListener.class));


    }
}