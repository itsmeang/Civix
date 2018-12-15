package com.getcivix.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



import com.getcivix.app.R;

import java.util.Set;

public class SidewalkReport extends Activity{

    

 @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sidewalk_report);

   final Button myButton = findViewById(R.id.mybutton);

    //myButton.setOnClickListener(new View.OnClickListener() {


     myButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             showMyDialog (myButton);
         }
     });


}

    private void showMyDialog(Button myButton) {
    }


    private void showMyDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_report_screen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        Button btnBtmLeft = (Button) dialog.findViewById(R.id.btnBtmLeft);
        Button btnBtmRight = (Button) dialog.findViewById(R.id.btnBtmRight);

        btnBtmLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnBtmRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do whatever you want here
            }
        });

        /**
         * if you want the dialog to be specific size, do the following
         * this will cover 85% of the screen (85% width and 85% height)
         */
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.85);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.85);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        dialog.show();
    }


}