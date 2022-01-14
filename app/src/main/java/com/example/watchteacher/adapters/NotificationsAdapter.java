package com.example.watchteacher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchteacher.R;
import com.example.watchteacher.model.Report;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{

    Context context;
    ArrayList<Report> reports;

    public NotificationsAdapter(Context context, ArrayList<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the usual shit
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.notification_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Report report = reports.get(position);



        holder.teacherName.setText(report.getTeacherName());
        holder.time.setText(report.getTime());
        holder.report.setText(report.getReport());




    }

    @Override
    public int getItemCount() {
        return reports.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView teacherName;
        public TextView report;
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            this.teacherName = itemView.findViewById(R.id.name);
            this.report = itemView.findViewById(R.id.report);
            this.time = itemView.findViewById(R.id.time);
        }
    }
}
