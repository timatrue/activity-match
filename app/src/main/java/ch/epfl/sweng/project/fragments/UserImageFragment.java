package ch.epfl.sweng.project.fragments;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.User;
import ch.epfl.sweng.project.UserProfile;


public class UserImageFragment extends DialogFragment {


    private final int PICK_IMAGE_REQUEST = 2;

    ImageView validationImage;

    ProgressBar uploadProgress;

    final Object semaphore = new Object();

    private ImageProvider mImageProvider;
    private DataProvider mDataProvider;

    protected LinearLayout rootView;

    Button okButton;
    Button editButton;

    TextView uploadRateText;
    TextView uploadRate;

    ImageView userImageView;

    User user;

    Bitmap imageBitmap;

    boolean highqualityloaded = false;

    RelativeLayout uploadLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (LinearLayout) inflater.inflate(R.layout.user_profile_image, container, false);

        uploadProgress = (ProgressBar) rootView.findViewById(R.id.uploadProgress);
        uploadProgress.setMax(100);

        uploadRateText = (TextView) rootView.findViewById(R.id.userImageUploadText);
        uploadRate = (TextView) rootView.findViewById(R.id.userImageUploadRate);

        okButton = (Button) rootView.findViewById(R.id.userImageOk);
        okButton.setOnClickListener(okListener);

        editButton = (Button) rootView.findViewById(R.id.userImageEdit);
        editButton.setOnClickListener(editListener);


        validationImage = (ImageView) rootView.findViewById(R.id.uploadedImage);

        uploadLayout = (RelativeLayout) rootView.findViewById(R.id.userImageUploadLayout);



        synchronized (semaphore) {
            userImageView = (ImageView) rootView.findViewById(R.id.userImage);

            if(imageBitmap != null) {
                setImageWithProperScale(userImageView, imageBitmap);
            }

            if(user != null) {

                mImageProvider.downloadUserImage(getActivity(), user.getId(), user.getPhotoLink(), userImageView, new ImageProvider.downloadListener() {
                    @Override
                    public void downloadFailed() {

                    }

                    @Override
                    public void downloadSucessful() {
                        highqualityloaded = true;
                    }
                });
              }
        }

        getDialog().setTitle(R.string.event_filter_title);

        //dismiss();

        return rootView;
    }




    View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    View.OnClickListener editListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                if(imageUri != null) {
                    mImageProvider.UploadUserImage(imageUri, user.getId(), uploadListener);
                    userImageView.setVisibility(View.GONE);
                    uploadLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    ImageProvider.uploadListener uploadListener = new ImageProvider.uploadListener() {
        @Override
        public void uploadFailed() {
            //TODO Implements on failed
        }

        @Override
        public void uploadSuccessful(Uri uploadedFileUri) {
            mDataProvider.changeUserImage(uploadedFileUri.getLastPathSegment());
            user.setPhotoLink(uploadedFileUri.getLastPathSegment());

            mImageProvider.downloadUserImage(getActivity(), user.getId(), user.getPhotoLink(), userImageView, new ImageProvider.downloadListener() {
                @Override
                public void downloadFailed() {

                }

                @Override
                public void downloadSucessful() {
                    userImageView.setVisibility(View.VISIBLE);
                    uploadLayout.setVisibility(View.GONE);
                    ((UserProfile) getActivity()).updateUser(user, true);
                }
            });
        }

        @Override
        public void uploadProgress(Uri fileUri, long bytesTransferred, long totalBytesCount) {
            int rate = (int) ((double) bytesTransferred/totalBytesCount * 100);
//            uploadRate.setText(getActivity().getString(R.string.progress, rate));
        }
    };


    public void setImageProvider(ImageProvider imageProvider) {
        this.mImageProvider = imageProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.mDataProvider = dataProvider;
    }


    private void setImageWithProperScale(final ImageView userImageView, final Bitmap imageBitmap) {
        userImageView.post(new Runnable() {
            @Override
            public void run() {
                if(highqualityloaded) {
                    return;
                }
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

    public void setUser(User user) {
        synchronized (semaphore) {
            this.user = user;
            if(userImageView != null) {
                mImageProvider.downloadUserImage(getActivity(), user.getId(), user.getPhotoLink(), userImageView, new ImageProvider.downloadListener() {
                    @Override
                    public void downloadFailed() {

                    }

                    @Override
                    public void downloadSucessful() {
                        highqualityloaded = true;
                    }
                });
            }
        }
    }

    public void setImage(Bitmap image) {
        synchronized (semaphore) {
            this.imageBitmap = image;
            if (userImageView != null) {
                setImageWithProperScale(userImageView, imageBitmap);
            }
        }
    }
}