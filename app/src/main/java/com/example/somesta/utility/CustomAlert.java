package com.example.somesta.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.somesta.R;

public class CustomAlert extends AlertDialog.Builder {


    public CustomAlert(Context context,String title,String message) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View viewDialog = inflater.inflate(R.layout.alert_view, null, false);

        TextView titleTextView = (TextView)viewDialog.findViewById(R.id.title);
        titleTextView.setText(title);
        TextView messageTextView = (TextView)viewDialog.findViewById(R.id.message);
        messageTextView.setText(message);
        this.setCancelable(true);
        this.setView(viewDialog);

    } }
