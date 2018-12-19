package com.getcivix.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterUserPage extends Activity implements View.OnClickListener{

    Button buttonSignUp;
    Button buttonReturnToSignIn;
    Spinner spinnerInterest;
    ArrayAdapter<CharSequence> adapter;

    /* Declare variables for the widgets */
    EditText editTextRegisterEmail, editTextCreatePassword;

    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_page);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonReturnToSignIn=findViewById(R.id.buttonReturnToSignIn);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextCreatePassword = findViewById(R.id.editTextCreatePassword);
        spinnerInterest=(Spinner)findViewById(R.id.spinnerInterest);
        adapter=ArrayAdapter.createFromResource(this,R.array.interest_arrays,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        //Make buttons listening to clicks
        buttonSignUp.setOnClickListener(this);
        buttonReturnToSignIn.setOnClickListener(this);

        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSignUp) {


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

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterUserPage.this, "The registration failed. Please, review your credentials.", Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });

        }

        else if(v ==   buttonReturnToSignIn) {

            Intent intentLogin = new Intent(RegisterUserPage.this, LoginPage.class);
            startActivity(intentLogin);
        }
    }
}
