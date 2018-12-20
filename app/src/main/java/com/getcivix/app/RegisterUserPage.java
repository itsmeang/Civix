package com.getcivix.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.support.constraint.Constraints.TAG;

public class RegisterUserPage extends Activity implements View.OnClickListener{

    Button buttonSignUp;
    Button buttonReturnToSignIn;
    Button buttonAttachPicture;
    Button buttonUploadPicture;
    Spinner spinnerInterest;
    ArrayAdapter<CharSequence> adapter;
    private RadioGroup gender_radio_group;
    private RadioButton selected_radio_btn;


    /* Declare variables for the widgets */
    EditText editTextRegisterEmail, editTextCreatePassword, editTextRegisterUsername;

    private DatabaseReference mFirebaseDatabaseUser;
    //private DatabaseReference mFirebaseDatabaseReport;
    private FirebaseDatabase mFirebaseInstance;


    // image upload

    private Uri fileUri;

    private DatabaseReference mDataReference;
    private StorageReference imageReference;
    private StorageReference fileRef;

    private TextView textViewAttachPicture;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;




    private String userId;


    ProgressDialog progressDialog;
    private String uploadedProfileImageKey = null;
    //private String uploadedReportImageKey = null;

    private boolean isProfliePictureChoosing = false;
    //private boolean isReportPictureChoosing = false;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_page);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonReturnToSignIn=findViewById(R.id.buttonReturnToSignIn);
        editTextRegisterUsername= findViewById(R.id.editTextRegisterUsername);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        //Toast.makeText(RegisterUserPage.this, editTextRegisterEmail.getText().toString(), Toast.LENGTH_SHORT).show();
        editTextCreatePassword = findViewById(R.id.editTextCreatePassword);
        //Toast.makeText(RegisterUserPage.this, "Password exists!!!!!!" + editTextCreatePassword.getText().toString(), Toast.LENGTH_SHORT).show();

        spinnerInterest=(Spinner)findViewById(R.id.spinnerInterest);
        adapter=ArrayAdapter.createFromResource(this,R.array.interest_arrays,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_radio_group=findViewById(R.id.gender_radio_group);


        int selectedId = gender_radio_group.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        selected_radio_btn = (RadioButton) findViewById(selectedId);

        spinnerInterest.setAdapter(adapter);
        spinnerInterest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(),parent.getItemIdAtPosition(position)+" selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textViewAttachPicture = findViewById(R.id.textViewAttachPicture);
        buttonAttachPicture = findViewById(R.id.buttonAttachPicture);
        buttonUploadPicture = findViewById(R.id.buttonUploadPicture);

        //Make buttons listening to clicks
        buttonSignUp.setOnClickListener(this);
        buttonReturnToSignIn.setOnClickListener(this);
        buttonAttachPicture.setOnClickListener(this);
        buttonUploadPicture.setOnClickListener(this);

        //userId = StaticConstants.userID;

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // get reference to 'users' node
        mFirebaseDatabaseUser = mFirebaseInstance.getReference("users");

        // get reference to 'eventReport' node
        //mFirebaseDatabaseReport = mFirebaseInstance.getReference("eventReport");

        // get reference to image
        mDataReference = FirebaseDatabase.getInstance().getReference("images");
        imageReference = FirebaseStorage.getInstance().getReference().child("images");
        fileRef = null;
        progressDialog = new ProgressDialog(this);

    }


    private void uploadFile(final boolean isProfilePicture) {
        isProfliePictureChoosing = false;
        //isReportPictureChoosing = false;
        if (fileUri != null) {
            String fileName;
            fileName = textViewAttachPicture.getText().toString();
            if (!validateInputFileName(fileName)) {
                return;
            }

            /*
            if(isProfilePicture)
                fileName = textViewAttachPicture.getText().toString();
            else
                fileName = reportPictureFileName.getText().toString();

            if (!validateInputFileName(fileName)) {
                return;
            }
            */


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

                            Toast.makeText(RegisterUserPage.this, "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();

                            Toast.makeText(RegisterUserPage.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(RegisterUserPage.this, "No File!", Toast.LENGTH_LONG).show();
        }
    }

    private void writeNewImageInfoToDB(String name, String url, boolean isProfilePicture) {
        UploadInfo info = new UploadInfo(name, url);

        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(info);
        if(isProfilePicture)
            uploadedProfileImageKey = url;
        else
            return;
            //uploadedReportImageKey = url;
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
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean validateInputFileName(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(RegisterUserPage.this, "Enter file name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /*

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

    */


    /**
     * Creating new user node under 'users'
     */
    private void createUser(String userUid,String userName, String email, String gender, String interest, String uplaodedProfileImageKey) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabaseUser.push().getKey();
        }

        User user = new User(userUid,userName, email, gender, interest, uplaodedProfileImageKey);

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




    @Override
    public void onClick(View v) {

        if (v==buttonAttachPicture){
            showChoosingFile();
            isProfliePictureChoosing = true;
        }

        else if (v==buttonUploadPicture){
            if(isProfliePictureChoosing)
                uploadFile(true);

        }
        else if(v == buttonSignUp) {

            /*An account is created with User Email and Password as entered
            the edit texts in case the entries are valid.
            A Success message is returned on successful registration.
            A Fail message is returned on unsuccessful registration*/

            mAuth.createUserWithEmailAndPassword(editTextRegisterEmail.getText().toString(), editTextCreatePassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterUserPage.this, "You are successfully registered. You can now sign-in", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();

                                String userUid=user.getUid();
                                String name = editTextRegisterUsername.getText().toString();
                                String email = editTextRegisterEmail.getText().toString();
                                String gender = selected_radio_btn.getText().toString();
                                String interest = spinnerInterest.getSelectedItem().toString();
                                createUser(userUid,name, email,gender,interest, uploadedProfileImageKey);
                                // Check for already existed userId
                                /*
                                if (TextUtils.isEmpty(userId)) {
                                    createUser(name, email,gender,interest, uploadedProfileImageKey);
                                } else {
                                    Log.d("Profile fragment", "User already exists");
                                    updateUser(name, email,gender,interest,credibility, uploadedProfileImageKey);
                                }*/


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterUserPage.this, "The registration failed. Please, review your credentials.", Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });



        }

        else if(v ==   buttonReturnToSignIn) {

            //Toast.makeText(RegisterUserPage.this, editTextRegisterEmail.getText().toString(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(RegisterUserPage.this, editTextCreatePassword.getText().toString(), Toast.LENGTH_SHORT).show();

            Intent intentLogin = new Intent(RegisterUserPage.this, LoginPage.class);
            startActivity(intentLogin);
        }
    }
}
