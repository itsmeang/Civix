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

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View mView = inflater.inflate(R.layout.fragment_profile, null);

        return mView;

    }



}
