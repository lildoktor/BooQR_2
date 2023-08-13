package com.example.nice_login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    boolean switcher = false;
    FirebaseAuth fAuth;
    String uid;
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
        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();
        if (dataList.get(position).getDataImage().equals(""))
            holder.recImage.setImageResource(R.drawable.books);
        else
            Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDesc.setText(dataList.get(position).getDataDesc());

        holder.recCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity2.class);
            intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
            intent.putExtra("title", dataList.get(holder.getAdapterPosition()).getDataTitle());
            context.startActivity(intent);
        });

        holder.option.setOnClickListener(view -> {
            if (!holder.edit.isShown()) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);
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
            new AlertDialog.Builder(context)
                    .setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete the collection: " + dataList.get(holder.getAdapterPosition()).getDataTitle() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        holder.delete.setVisibility(View.GONE);
                        holder.edit.setVisibility(View.GONE);
                        String imgURL = dataList.get(holder.getAdapterPosition()).getDataImage();
                        String key = dataList.get(holder.getAdapterPosition()).getKey();

                        DatabaseReference nodeToDelete = FirebaseDatabase.getInstance().getReference(uid).child(key);

                        nodeToDelete.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot timestampSnapshot : dataSnapshot.getChildren()) {
                                    if (timestampSnapshot.hasChild("dataPath")) {
                                        String contentUrl = timestampSnapshot.child("dataPath").getValue(String.class);
                                        FirebaseStorage.getInstance().getReferenceFromUrl(contentUrl).delete().addOnFailureListener(e -> Log.e("TAG", "onFailure: did not delete file: " + e.getMessage()));
                                    }
                                }
                                if (!"".equals(imgURL))
                                    FirebaseStorage.getInstance().getReferenceFromUrl(imgURL).delete().addOnFailureListener(e -> Log.e("TAG", "onFailure: did not delete collection image: " + e.getMessage()));
                                nodeToDelete.removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(context, "Failed to delete collection: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
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
