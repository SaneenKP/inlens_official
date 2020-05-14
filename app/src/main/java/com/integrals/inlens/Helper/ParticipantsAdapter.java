package com.integrals.inlens.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.integrals.inlens.MainActivity;
import com.integrals.inlens.Models.PhotographerModel;
import com.integrals.inlens.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int VIEW_TYPE_PHOTOGRAPHER=0,VIEW_TYPE_ADD_BUTTON=1;
    List<PhotographerModel> photographersList;
    Context context;
    Dialog qrcodeDialog;
    MainActivity activity;


    public ParticipantsAdapter(List<PhotographerModel> photographersList,
                               Context context,
                               MainActivity activity,Dialog qrcodeDialog) {
        this.photographersList = photographersList;
        this.context = context;
        this.qrcodeDialog = qrcodeDialog;
        this.activity=activity;
    }

    @Override
    public int getItemViewType(int position) {
        if(photographersList.get(position).getName().equals("add") &&photographersList.get(position).getId().equals("add") &&photographersList.get(position).getImgUrl().equals("add") )
        {
            return VIEW_TYPE_ADD_BUTTON;

        }
        else
        {
            return VIEW_TYPE_PHOTOGRAPHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==VIEW_TYPE_PHOTOGRAPHER)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.member_card,parent,false);
            return new ParticipantsViewHolder(view);
        }
        else if(viewType ==VIEW_TYPE_ADD_BUTTON)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.member_add_card,parent,false);
            return new AddParticipantsViewHolder(view);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ParticipantsViewHolder)
        {
            ParticipantsViewHolder viewHolder = (ParticipantsViewHolder) holder;
            if(photographersList.get(position).getName().length() > 6)
            {
                String name =photographersList.get(position).getName().substring(0,5)+"...";
                viewHolder.PName.setText(name);

            }
            else
            {
                viewHolder.PName.setText(photographersList.get(position).getName());

            }

            RequestOptions rq = new RequestOptions().placeholder(R.drawable.ic_account_circle_24dp);
            Glide.with(context).load(photographersList.get(position).getImgUrl()).apply(rq).into(viewHolder.PImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(photographersList.get(position).getId().contentEquals(activity.getCurrentUserId())){
                        PhotographerFragementBottomSheetNA photographerFragementBottomSheetNA = new PhotographerFragementBottomSheetNA();
                        photographerFragementBottomSheetNA.show(((FragmentActivity) activity).getSupportFragmentManager(), photographerFragementBottomSheetNA.getTag());

                    }else {
                        PhotographerFragementBottomSheetAdmin photographerFragementBottomSheetAdmin = new PhotographerFragementBottomSheetAdmin();
                        photographerFragementBottomSheetAdmin.show(((FragmentActivity) activity).getSupportFragmentManager(), photographerFragementBottomSheetAdmin.getTag());
                         }
                    // IF Admin on PhotographerFragementBottomSheetAdmin
                    // IF Not Admin photographerFragementBottomSheetNotAdmin
                    }
            });


        }
        else if(holder instanceof  AddParticipantsViewHolder)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    qrcodeDialog.show();

                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return photographersList.size();
    }


}

class ParticipantsViewHolder extends RecyclerView.ViewHolder {

    CircleImageView PImage;
    TextView PName;

    public ParticipantsViewHolder(View itemView) {
        super(itemView);

        PImage = itemView.findViewById(R.id.participants_profile_pic);
        PName = itemView.findViewById(R.id.participants_username);
    }
}

class AddParticipantsViewHolder extends RecyclerView.ViewHolder {

    public AddParticipantsViewHolder(View itemView) {
        super(itemView);

    }
}