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
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder>{


    Context context;
    private List<Report> reports;

    // RecyclerView recyclerView;
    public ReportsAdapter(Context context, ArrayList<Report> reports) {
        this.context = context;
        this.reports = reports;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the usual shit
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.report_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Report report = reports.get(position);


        StringBuilder builder = new StringBuilder();
        builder.append(report.getTeacherName());
        builder.append(" : ");
        builder.append(report.getReport());
        builder.append(" : ");
        builder.append(report.getDateTime());

        holder.report.setText(builder.toString());

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView report;

        public ViewHolder(View itemView) {
            super(itemView);
            this.report = itemView.findViewById(R.id.report);
        }
    }





}
