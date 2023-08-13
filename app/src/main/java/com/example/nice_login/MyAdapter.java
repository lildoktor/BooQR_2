package com.example.nice_login;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    boolean switcher = false;
    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDesc.setText(dataList.get(position).getDataDesc());

        holder.recCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity2.class);
            intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
            context.startActivity(intent);
        });

        holder.option.setOnClickListener(view -> {
            if (!switcher) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
                switcher = true;
            } else {
                holder.delete.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);
                switcher = false;
            }
        });

        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
            intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
            intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
            intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
            context.startActivity(intent);
        });


        holder.delete.setOnClickListener(view -> {

            //TODO: delete subdirectory

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UID");
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageReference = storage.getReferenceFromUrl(dataList.get(holder.getAdapterPosition()).getDataImage());
            storageReference.delete().addOnSuccessListener(unused -> {
                reference.child(dataList.get(holder.getAdapterPosition()).getKey()).removeValue().addOnSuccessListener(unused1 -> {
                    holder.delete.setVisibility(View.GONE);
                    holder.edit.setVisibility(View.GONE);
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView recImage, option, delete, edit;
    TextView recTitle, recDesc;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.recDesc);
        recTitle = itemView.findViewById(R.id.recTitle);

        option = itemView.findViewById(R.id.dot);
        delete = itemView.findViewById(R.id.delete);
        edit = itemView.findViewById(R.id.edit);
    }
}
