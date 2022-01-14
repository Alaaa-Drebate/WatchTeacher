package com.example.watchteacher.managerfragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.LoginActivity;
import com.example.watchteacher.R;
import com.example.watchteacher.api.Constants;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;


public class ReportProblemFragment extends Fragment {

    EditText mTitleET, mDetailsET;
    Button mSendBtn;


    public static final String TAG = "reportProblemFragment";


    public ReportProblemFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_problem, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSendBtn = view.findViewById(R.id.send_report_btn);
        mTitleET = view.findViewById(R.id.title);
        mDetailsET = view.findViewById(R.id.details);


        mSendBtn.setOnClickListener(v -> {
            mSendBtn.setEnabled(false);
            if(validateUserData()){
                sendReport();
            }
        });
    }

    private boolean validateUserData() {

        //first getting the values
        final String title = mTitleET.getText().toString();
        final String details = mDetailsET.getText().toString();

        //checking if username is empty
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(requireContext(), "report title must not be empty!", Toast.LENGTH_SHORT).show();
            mSendBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(details)) {
            Toast.makeText(requireContext(), "please specify your problem in details so we can fix it as soon as possible!", Toast.LENGTH_LONG).show();
            mSendBtn.setEnabled(true);
            return false;
        }

        return true;

    }


    private void sendReport() {

        String myId = String.valueOf(SharedPrefManager.getInstance(requireContext()).getUserId());

        final ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        //first getting the values
        final String title = mTitleET.getText().toString();
        final String details = mDetailsET.getText().toString();

        AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/complaints/add.php")
                .addBodyParameter("title", title)
                .addBodyParameter("content", details)
                .addBodyParameter("user_id", myId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();

                        try {
                            //converting response to json object
                            JSONObject obj = response;
                            Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            //if no error in response
                            if (obj.getInt("status") == 1) {
                                mSendBtn.setEnabled(true);
                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                mSendBtn.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(requireContext(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}