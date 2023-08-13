package com.example.nice_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    FloatingActionButton addImage, addVideo, addQuestion, addAudio, addDrag;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass2> dataList;
    MyAdapter2 adapter;
    SearchView searchView;
    String key, uid;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        addImage = findViewById(R.id.addImage);
        addVideo = findViewById(R.id.addVideo);
        addQuestion = findViewById(R.id.addQuestion);
        addAudio = findViewById(R.id.addAudio);
        addDrag = findViewById(R.id.addDrag);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");
            setTitle(bundle.getString("title"));
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity2.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter2(MainActivity2.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference(uid).child(key);
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                    if (itemSnapshot.hasChild("lastEdit")) { // Check for a unique field
                        DataClass2 dataClass = itemSnapshot.getValue(DataClass2.class);
                        dataClass.setKey(itemSnapshot.getKey());
                        dataList.add(dataClass);
                    }
                }
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        addVideo.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, UploadActivity2.class);
            intent.putExtra("Key", key);
            startActivity(intent);
        });

        addImage.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, UploadActivity3.class);
            intent.putExtra("Key", key);
            startActivity(intent);
        });

        addAudio.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, UploadActivity4.class);
            intent.putExtra("Key", key);
            startActivity(intent);
        });

        addQuestion.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, UploadActivity5.class);
            intent.putExtra("Key", key);
            startActivity(intent);
        });

        addDrag.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, UploadActivity6.class);
            intent.putExtra("Key", key);
            startActivity(intent);
        });

    }

    public void searchList(String text) {
        ArrayList<DataClass2> searchList = new ArrayList<>();
        for (DataClass2 dataClass : dataList) {
            if (dataClass.getTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.option_1) {
            fAuth.signOut();
            Intent intent = new Intent(MainActivity2.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove ValueEventListener to prevent further callbacks
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}