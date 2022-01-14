package com.example.watchteacher.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.AddTeacherActivity;
import com.example.watchteacher.EditProfileActivity;
import com.example.watchteacher.LoginActivity;
import com.example.watchteacher.ManagerRegisterActivity;
import com.example.watchteacher.R;
import com.example.watchteacher.TeacherMainActivity;
import com.example.watchteacher.TeacherReportsActivity;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.ViewHolder>{


    Context context;
    private List<User> teachers;

    // RecyclerView recyclerView;
    public TeachersAdapter(Context context, ArrayList<User> teachers) {
        this.context = context;
        this.teachers = teachers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.teacher_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User teacher = teachers.get(position);




        holder.name.setText(teacher.getName());

        //call on click to notify main activity to switch to teacher details fragment
        holder.itemView.setOnClickListener(v -> {
            Intent teacherReport = new Intent(context, TeacherReportsActivity.class);
            teacherReport.putExtra("id", teacher.getId());
            teacherReport.putExtra("name", teacher.getName());
            teacherReport.putExtra("phone", teacher.getPhone());
            //teacher's current lat and lon
            teacherReport.putExtra("lat", teacher.getLat());
            teacherReport.putExtra("lon", teacher.getLon());
            context.startActivity(teacherReport);
        });

        holder.itemView.setOnLongClickListener(v -> {

            LayoutInflater factory = LayoutInflater.from(context);
            final View view = factory.inflate(R.layout.teacher_update_delete_tools_dialog, null);
            final AlertDialog teacherUpdateDeleteDialog = new AlertDialog.Builder(context).create();
            teacherUpdateDeleteDialog.setCancelable(true);
            teacherUpdateDeleteDialog.setView(view);

            TextView edit = view.findViewById(R.id.edit_btn);
            TextView delete = view.findViewById(R.id.delete_btn);


            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditProfileActivity.class);
                    intent.putExtra("userId", teacher.getId());
                    intent.putExtra("title", context.getResources().getString(R.string.edit_teacher_profile));
                    context.startActivity(intent);
                    teacherUpdateDeleteDialog.dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    removeOrder(position);

                    removeTeacher(teacher.getId());

                    teacherUpdateDeleteDialog.dismiss();
                }
            });
            teacherUpdateDeleteDialog.show();

            return true;
        });



    }

    private void removeTeacher(int id) {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/delete-user.php")
                .addBodyParameter("user_id", String.valueOf(id))
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

                            //if no error in response
                            if (obj.getInt("status") == 1) {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(context, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }


    private void removeOrder(int index) {
        teachers.remove(index);
        notifyItemRemoved(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.name);
        }
    }





}
