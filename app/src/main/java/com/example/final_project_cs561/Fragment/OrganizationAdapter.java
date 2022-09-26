package com.example.final_project_cs561.Fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project_cs561.Model.UserData;
import com.example.final_project_cs561.Organization.RequestActivity;
import com.example.final_project_cs561.R;

import java.util.ArrayList;

public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.MyViewHolder>{
   private Context context;
   private ArrayList<UserData> userData;
   public OrganizationAdapter(Context context, ArrayList<UserData> userData){
       this.context = context;
       this.userData = userData;
   }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       final UserData userData = this.userData.get(position);
       holder.tvUserName.setText(userData.getUserName());
       holder.tvEmail.setText(userData.getUserEmail());
       holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,RequestActivity.class);
                intent.putExtra("userName",userData.getUserName());
                intent.putExtra("userEmail",userData.getUserEmail());
                intent.putExtra("isClient",true);
                intent.putExtra("isApproved",!userData.isPending());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, tvEmail;
        public LinearLayout llMain;

        public MyViewHolder(View view) {
            super(view);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvEmail = (TextView) view.findViewById(R.id.tvEmail);
            llMain = view.findViewById(R.id.llMain);
        }
    }
}
