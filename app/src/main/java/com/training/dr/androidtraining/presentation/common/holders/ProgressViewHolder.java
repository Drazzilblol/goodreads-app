package com.training.dr.androidtraining.presentation.common.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.ulils.Utils;


public class ProgressViewHolder extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;

    public ProgressViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
    }

    public void onBindData(boolean show, int rvSize) {
        if (show) {
            if (rvSize == 0) {
                progressBar.setPadding(0, Utils.getScreenHeight() / 3, 0, 0);
            } else {
                progressBar.setPadding(0, 0, 0, 0);
            }
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
