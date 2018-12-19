package com.getcivix.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.getcivix.app.Models.ReportModel;
import com.getcivix.app.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "NotificationFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //  user data
    private TextView mTextViewSurname;
    private TextView mTextViewEmail;
    private TextView mTextViewGender;
    private TextView mTextViewInterest;
    //private TextView mTextViewCredibility;

    private TextView mTextViewReportTime;
    private TextView mTextViewReportStreetAddress;
    private TextView mTextViewReportCategory;


    private Button mButtonViewUser;
    private Button mButtonSubmitReport;


    private DatabaseReference mFirebaseDatabaseUser;
    private DatabaseReference mFirebaseDatabaseReport;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;
    private ImageView mImageViewReportPicture;
    private ImageView mImageViewProfilePicture;

    // report data

    //  editTextEnterYourComment  buttonSubmitReport

    public NotificationFragment() {
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
        View mView =  inflater.inflate(R.layout.fragment_notification, null);
        mTextViewSurname = mView.findViewById(R.id.textViewSurname);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewGender = mView.findViewById(R.id.textViewGender);
        mTextViewInterest = mView.findViewById(R.id.textViewInterest);
        //mTextViewCredibility = mView.findViewById(R.id.textViewCredibility);
        mImageViewProfilePicture =  mView.findViewById(R.id.imageViewProfilePicture);


        mTextViewReportTime =  mView.findViewById(R.id.textViewReportTime);
        mTextViewReportStreetAddress =  mView.findViewById(R.id.textViewReportStreetAddress);
        mTextViewReportCategory =  mView.findViewById(R.id.textViewReportCategory);
        mImageViewReportPicture =  mView.findViewById(R.id.imageViewReportPicture);


//      mProfilePictureInput = mView.findViewById(R.id.textViewAttachPicture);
        mButtonViewUser = mView.findViewById(R.id.buttonViewUser);
        mButtonSubmitReport = mView.findViewById(R.id.buttonSubmitReport);

        userId = StaticConstants.userID;


        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabaseUser = mFirebaseInstance.getReference("users");

        // get reference to 'eventReport' node
        mFirebaseDatabaseReport = mFirebaseInstance.getReference("eventReport");


        mButtonViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserChangeListener();
            }
        });

        mButtonSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReportChangeListener();
            }
        });


        return mView;
    }


    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabaseUser.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.toString());

                // Display newly updated name and email
                mTextViewSurname.setText(user.userName);
                mTextViewEmail.setText(user.email);
                mTextViewGender.setText(user.gender);
                mTextViewInterest.setText(user.interest);
                //mTextViewCredibility.setText(String.valueOf(user.credibility));

//                Picasso.get().load(user.uplaodedProfileImageKey).into(mImageViewProfilePicture);

                Picasso.get()
                        .load(user.uplaodedProfileImageKey)
                        .error(R.drawable.common_google_signin_btn_icon_dark)
                        .into(mImageViewProfilePicture);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }


    /**
     * User data change listener
     */
    private void addReportChangeListener() {
        // User data change listener
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query lastQuery = databaseReference.child("eventReport").orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ReportModel report = data.getValue(ReportModel.class);

                    // Check for null
                    if (report == null) {
                        Log.e(TAG, "User data is null!");
                        return;
                    }

                    Log.e(TAG, "User data is changed!" + report.toString() );

                    // Display newly updated name and email
                    mTextViewReportTime.setText(String.valueOf(report.reportTime)); // pelase use a time mili to readable time converter
                    mTextViewReportStreetAddress.setText(report.reportLocation);
                    mTextViewReportCategory.setText(report.comment);

                    Picasso.get()
                            .load(report.uploadedReportImageKey)
                            .error(R.drawable.common_google_signin_btn_icon_dark)
                            .into(mImageViewReportPicture);


                    //  Picasso.get().load(report.uplaodedReportImageKey).into(mImageViewReportPicture);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });

    }

}
