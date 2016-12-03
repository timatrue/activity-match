package ch.epfl.sweng.project.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.Image;
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

import ch.epfl.sweng.project.CreateActivity;
import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;



public class CreateValidationFragment extends DialogFragment {
    Button okButton;
    TextView validationText;
    TextView uploadRateText;

    ImageView validationImage;

    ProgressBar uploadProgress;

    int progressRate = 0;
    int nbFilesToUpload = 1;
    int nbFilesUploaded = 0;


    Map<Uri, Long> imagesUploadedMap = new HashMap<>();
    Map<Uri, Long> imagesToUploadMap = new HashMap<>();


    private ImageProvider mImageProvider;
    private DataProvider mDataProvider;

    private LinearLayout rootView;

    CreateActivity baseActivity;

    private final Handler handler = new Handler();
    private Runnable updateRunnable;

    private boolean uploadFinished = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseActivity = (CreateActivity) getActivity();
        rootView = (LinearLayout) inflater.inflate(R.layout.creation_validation_layout, container, false);

        uploadProgress = (ProgressBar) rootView.findViewById(R.id.uploadProgress);
        uploadProgress.setMax(100);

        validationText = (TextView) rootView.findViewById(R.id.createValidationUploadText);
        uploadRateText = (TextView) rootView.findViewById(R.id.createValidationUploadRate);
        okButton = (Button) rootView.findViewById(R.id.createValidationOkButton);

        validationImage = (ImageView) rootView.findViewById(R.id.uploadedImage);

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if(isAdded()) {
                    synchronized (handler) {
                        updateProgress();
                        if (nbFilesToUpload != nbFilesUploaded) {
                            handler.postDelayed(this, 40);
                        }
                    }
                }
            }
        };
        updateRunnable.run();

        getDialog().setTitle(R.string.event_filter_title);

        //dismiss();

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                if(uploadFinished) {
                    dismiss();
                    baseActivity.finish();
                }
                else
                {
                    synchronized (handler) {
                        handler.removeCallbacks(updateRunnable);
                        dismiss();
                        super.onBackPressed();
                    }
                }
            }
        };
    }


    public void setImagesUriList(List<Uri> imagesUriList) {
        for(Uri uri: imagesUriList) {
            imagesUploadedMap.put(uri, new Long(0));
            imagesToUploadMap.put(uri, new Long(0));
        }
        nbFilesToUpload = imagesUriList.size() + 1;
    }

    public void uploadActivity(DeboxActivity activity, boolean creation)
    {
        //Push the activity on the DB
        String activityKey;
        if (creation) {
            activityKey = mDataProvider.pushActivity(activity);
        } else {
            activityKey = mDataProvider.updateActivity(activity);
        }
        nbFilesUploaded++;

        //Upload all selected images in a folder corresponding to activity id
        for(Uri uri :imagesUploadedMap.keySet()) {
            mImageProvider.UploadImage(uri, activityKey, uploadListener);
        }

    }

    private void updateProgress() {

        long uploadedLength = 0;
        for(Long uploaded :imagesUploadedMap.values()) {
            uploadedLength += uploaded;
        }

        long uploadLength = 0;
        for(Long uploaded :imagesToUploadMap.values()) {
            uploadLength += uploaded;
        }

        progressRate = (int)(uploadedLength*100.0/uploadLength);
        String uploadRate = new StringBuilder(getString(R.string.creation_rate_text)).append((progressRate + "%")).toString();
        uploadRateText.setText(uploadRate);

        if(nbFilesUploaded == nbFilesToUpload) {
            handler.removeCallbacks(updateRunnable);
            progressRate = 100;
            uploadFinished = true;
            validationText.setText(getString(R.string.creation_validation_complete_text));
            uploadProgress.setVisibility(View.INVISIBLE);
            uploadRateText.setVisibility(View.INVISIBLE);
            validationImage.setVisibility(View.VISIBLE);
            okButton.setVisibility(View.VISIBLE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    baseActivity.finish();
                }
            });
        }

    }

    ImageProvider.uploadListener uploadListener = new ImageProvider.uploadListener() {
        @Override
        public void uploadFailed() {
            //TODO Implements on failed
        }

        @Override
        public void uploadSuccessful(Uri uploadedFileUri) {
            nbFilesUploaded++;
            imagesToUploadMap.put(uploadedFileUri, imagesToUploadMap.get(uploadedFileUri));
        }

        @Override
        public void uploadProgress(Uri fileUri, long bytesTransferred, long totalBytesCount) {
            imagesUploadedMap.put(fileUri, bytesTransferred);
            imagesToUploadMap.put(fileUri, totalBytesCount);
        }
    };



    public void setImageProvider(ImageProvider imageProvider) {
        this.mImageProvider = imageProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.mDataProvider = dataProvider;
    }
}