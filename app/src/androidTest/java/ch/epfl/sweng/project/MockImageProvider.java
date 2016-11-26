package ch.epfl.sweng.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    public void initpreviewImage(final Drawable previewImage){

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Context context = (Context) args[0];
                String folder = (String) args[1];
                View childLayout = (View) args[2];
                String imageName = (String) args[2];

                SquareImageView imageView = (SquareImageView) childLayout.findViewById(R.id.activityImage);

                imageView.setImageDrawable(previewImage);

                return null;
            }
        }).when(mockImageProvider).previewImage(any(Context.class), any(String.class), any(LinearLayout.class), any(String.class));
    }

    public void initUploadImage() {

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                final Uri imageUri = (Uri) args[0];
                String folder = (String) args[1];
                final ImageProvider.uploadListener listener = (ImageProvider.uploadListener) args[2];

                listener.uploadProgress(imageUri, 10, 10);
                listener.uploadSuccessful(imageUri);

                return null;
            }
        }).when(mockImageProvider).UploadImage(any(Uri.class), any(String.class), any(ImageProvider.uploadListener.class));


    }
}