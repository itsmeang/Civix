package com.getcivix.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getcivix.app.Models.Category;
import com.getcivix.app.Models.ReportModel;
import com.getcivix.app.Models.UploadInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


public class PotholesReport extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PotholesReport" ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String categoryName="Potholes";
    private String categoryType="Roads";
    private String categoryTypeColor="Green";

    // report data

    //  editTextEnterYourComment  buttonSubmitReport
    private EditText mComment;
    private EditText mEditTextEnterCategory;
    private Button mButtonAttachPicture;
    private Button mButtonUploadPicture;

    private Button mSubmitReportButton;
    private Button mButtonAttachReportPicture;
    private Button mButtonUploadReportPicture;

    private DatabaseReference mFirebaseDatabaseUser;
    private DatabaseReference mFirebaseDatabaseReport;
    private FirebaseDatabase mFirebaseInstance;


    // image upload

    private Uri fileUri;

    private DatabaseReference mDataReference;
    private StorageReference imageReference;
    private StorageReference fileRef;

    private TextView textViewAttachReportPicture;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;




    private String userId;


    ProgressDialog progressDialog;
    private String uploadedReportImageKey = null;

    private boolean isReportPictureChoosing = false;

    public Activity thisActivity;



    //private OnFragmentInteractionListener mListener;

    public PotholesReport() {
        // Required empty public constructor
    }

    public void setThisActivity(Activity thisActivity) {
        this.thisActivity = thisActivity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View mView =  inflater.inflate(R.layout.fragment_potholes_report, null);
        mComment =  mView.findViewById(R.id.editTextEnterYourComment);
        mEditTextEnterCategory =  mView.findViewById(R.id.editTextEnterCategory);
        mSubmitReportButton = mView.findViewById(R.id.buttonSubmitReport);
        mButtonAttachReportPicture = mView.findViewById(R.id.buttonAttachReportPicture);
        mButtonUploadReportPicture = mView.findViewById(R.id.buttonUploadReportPicture);
        textViewAttachReportPicture = mView.findViewById(R.id.textViewAttachReportPicture);

        userId = StaticConstants.userID;




        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabaseUser = mFirebaseInstance.getReference("users");

        // get reference to 'eventReport' node
        mFirebaseDatabaseReport = mFirebaseInstance.getReference("eventReport");

        // get reference to image
        mDataReference = FirebaseDatabase.getInstance().getReference("images");
        imageReference = FirebaseStorage.getInstance().getReference().child("images");
        fileRef = null;
        progressDialog = new ProgressDialog(this.getActivity());


        // Save report
        mSubmitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = mComment.getText().toString();
                Long reportTime = System.currentTimeMillis();
                LatLng reportLocation = StaticConstants.reportLocation;
                String reporterId = userId;
                Category category = null;
                category =  new Category(categoryName,categoryType, categoryTypeColor);
                createReport(comment,category,reportTime,reportLocation,reporterId,uploadedReportImageKey );
                Toast.makeText(getContext(), "Your report was successfully submitted.", Toast.LENGTH_SHORT).show();
                MapFragment fragment=new MapFragment();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);


            }
        });

        return mView;
    }


    private void uploadFile(final boolean isProfilePicture) {
        //isProfliePictureChoosing = false;
        isReportPictureChoosing = false;
        if (fileUri != null) {
            String fileName;
            fileName = textViewAttachReportPicture.getText().toString();

            if (!validateInputFileName(fileName)) {
                return;
            }


            final String[] name = new String[1];
            String url;

            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            fileRef = imageReference.child(fileName + "." + getFileExtension(fileUri));
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            name[0] = taskSnapshot.getMetadata().getName();

                            Log.e(TAG, "Name: " + name[0]);



                            Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();

                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            // percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused!");
                        }
                    });



            // get downlaod link

            UploadTask   uploadTask = fileRef.putFile(fileUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL


                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "downloadUri: " + downloadUri.toString());
                        writeNewImageInfoToDB(name[0], downloadUri.toString(),isProfilePicture);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });




        } else {
            Toast.makeText(this.getActivity(), "No File!", Toast.LENGTH_LONG).show();
        }
    }

    private void writeNewImageInfoToDB(String name, String url, boolean isProfilePicture) {
        UploadInfo info = new UploadInfo(name, url);

        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(info);
        if(isProfilePicture)
            return;
        else
            uploadedReportImageKey = url;
    }

    private void showChoosingFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean validateInputFileName(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this.getActivity(), "Enter file name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createReport(String comment, Category category, Long reportTime, LatLng reportLocation, String reporterId, String uplaodedReportImageKey) {


        String reportId = mFirebaseDatabaseReport.push().getKey();

        String location;

        if(null != reportLocation){
            location  = reportLocation.toString();
        }else{
            location = "lat/lng: (23.7258262,90.4211494)";
        }



        ReportModel report = new ReportModel(comment,category, reportTime, location, reporterId,uplaodedReportImageKey,1);

        mFirebaseDatabaseReport.child(reportId).setValue(report);
    }

}
