package com.example.maxime.noteshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SmartContentFragment extends Fragment {

    private static final String REQUEST = "request";
    private static final String RESULT = "result";

    public void setArguments(String request, String result) {
        Bundle args=new Bundle();
        args.putString(REQUEST, request);
        args.putString(RESULT, result);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.element_smart_content, container, false);
        TextView requestView = (TextView) rootView.findViewById(R.id.smart_content_request);
        TextView resultView = (TextView) rootView.findViewById(R.id.smart_content_result);
        requestView.setText(getArguments().getString(REQUEST));
        resultView.setText(getArguments().getString(RESULT));
        ImageView readIcon = (ImageView) rootView.findViewById(R.id.read_icon);
        readIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SmartContentActivity.class);
                intent.putExtra(REQUEST, getArguments().getString(REQUEST));
                intent.putExtra(RESULT, getArguments().getString(RESULT));
                startActivity(intent);
            }
        });
        return rootView;
    }
}
