package com.getcivix.app;

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
import com.getcivix.app.Models.User;
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


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFragment" ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //  user data
    private EditText mEmailInput;
    private EditText mUserNameInput;
    private EditText mUseGenderInput;
    private EditText mUserInterestInput;
    private Button mRegisterButton;

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

    private TextView edtFileName;
    private TextView reportPictureFileName;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;




    private String userId;


    ProgressDialog progressDialog;
    private String uploadedProfileImageKey = null;
    private String uplaodedReportImageKey = null;

    private boolean isProfliePictureChoosing = false;
    private boolean isReportPictureChoosing = false;



    //private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
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
        View mView =  inflater.inflate(R.layout.fragment_profile, null);
        mEmailInput = mView.findViewById(R.id.editTextEnterEmail);
        mUserNameInput = mView.findViewById(R.id.editTextEnterUsername);
        mUseGenderInput = mView.findViewById(R.id.editTextEnterGender);
        mUserInterestInput = mView.findViewById(R.id.editTextEnterInterests);
        mComment =  mView.findViewById(R.id.editTextEnterYourComment);
        mEditTextEnterCategory =  mView.findViewById(R.id.editTextEnterCategory);
        edtFileName = mView.findViewById(R.id.textViewAttachPicture);
        mRegisterButton = mView.findViewById(R.id.buttonRegisterUser);
        mSubmitReportButton = mView.findViewById(R.id.buttonSubmitReport);
        mButtonAttachPicture = mView.findViewById(R.id.buttonAttachPicture);
        mButtonUploadPicture = mView.findViewById(R.id.buttonUplaodPicture);

        mButtonAttachReportPicture = mView.findViewById(R.id.buttonAttachReportPicture);
        mButtonUploadReportPicture = mView.findViewById(R.id.buttonUploadReportPicture);
        reportPictureFileName = mView.findViewById(R.id.textViewAttachReportPicture);

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

        // Save / update the user
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mUserNameInput.getText().toString();
                String email = mEmailInput.getText().toString();
                String gender = mUseGenderInput.getText().toString();
                String interest = mUserInterestInput.getText().toString();
                Integer credibility = 0;
                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, email,gender,interest, uploadedProfileImageKey);
                } else {
                    Log.d("Profile fragment", "User already exists");
                    updateUser(name, email,gender,interest,credibility, uploadedProfileImageKey);
                }
            }
        });

        mButtonAttachPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoosingFile();
                isProfliePictureChoosing = true;
            }
        });

        mButtonUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isProfliePictureChoosing)
                    uploadFile(true);
            }
        });

        mButtonAttachReportPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoosingFile();
                isReportPictureChoosing = true;
            }
        });

        mButtonUploadReportPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReportPictureChoosing)
                    uploadFile(false);
            }
        });


        // Save report
        mSubmitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = mComment.getText().toString();
                String categoryType = mEditTextEnterCategory.getText().toString();
                Long reportTime = System.currentTimeMillis();
                LatLng reportLocation = StaticConstants.reportLocation;
                String reporterId = userId;
                Category category = null;
                if(categoryType.equals("1"))
                    category =  new Category("1",1, "red");
                else if(categoryType.equals("2"))
                    category =  new Category("2",2, "gren");
                else if(categoryType.equals("3"))
                    category =  new Category("3",3, "yellow");

                createReport(comment,category,reportTime,reportLocation,reporterId,uplaodedReportImageKey );

            }
        });

        return mView;
    }


    private void uploadFile(final boolean isProfilePicture) {
        isProfliePictureChoosing = false;
        isReportPictureChoosing = false;
        if (fileUri != null) {
            String fileName;
            if(isProfilePicture)
                fileName = edtFileName.getText().toString();
            else
                fileName = reportPictureFileName.getText().toString();

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
            uploadedProfileImageKey = url;
        else
            uplaodedReportImageKey = url;
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


        // On report submission, credibility is set at 1. In will increase in future
        // when other users like the report
        ReportModel report = new ReportModel(comment,category, reportTime, location, reporterId,uplaodedReportImageKey,1);

        mFirebaseDatabaseReport.child(reportId).setValue(report);
    }


    /**
     * Creating new user node under 'users'
     */
    private void createUser(String userName, String email, String gender, String interest, String uplaodedProfileImageKey) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabaseUser.push().getKey();
        }

        User user = new User(userName, email, gender, interest, uplaodedProfileImageKey);

        mFirebaseDatabaseUser.child(userId).setValue(user);
    }

    private void updateUser(String userName, String email, String gender, String interest, Integer credibility,String uplaodedProfileImageKe) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(userName))
            mFirebaseDatabaseUser.child(userId).child("userName").setValue(userName);

        if (!TextUtils.isEmpty(email))
            mFirebaseDatabaseUser.child(userId).child("email").setValue(email);

        if (!TextUtils.isEmpty(gender))
            mFirebaseDatabaseUser.child(userId).child("gender").setValue(gender);

        if (!TextUtils.isEmpty(interest))
            mFirebaseDatabaseUser.child(userId).child("interest").setValue(interest);


        if (!TextUtils.isEmpty(uplaodedProfileImageKe))
            mFirebaseDatabaseUser.child(userId).child("uplaodedProfileImageKe").setValue(uplaodedProfileImageKe);

        mFirebaseDatabaseUser.child(userId).child("credibility").setValue(credibility);
    }





}
