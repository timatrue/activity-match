package ch.epfl.sweng.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;


class MockImageProvider {

    @Mock
    ImageProvider mockImageProvider;


    ImageProvider getMockImageProvider(){

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

    private void initUploadImage() {

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