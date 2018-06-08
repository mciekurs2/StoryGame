package com.kristine.spaceadventure;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dabū FirebaseUser un Database references
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        //nodefinē recylcerView un uzstāda tam LayoutManager opcijas
        recyclerView = findViewById(R.id.recyclerView_main);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        //init medodi
        displayStories();

    }

    /** Nodrošina core funkcionaliāti story saņemšanai un no apstrādei */
    public void displayStories(){
        //izveido peprasiijumu uz DB
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("stories");

        //piesaista Stories klasi un pieprasijumu FirebaseRecyclerOptions
        FirebaseRecyclerOptions<Stories> options = new FirebaseRecyclerOptions.
                Builder<Stories>().setQuery(query, Stories.class).build();

        //izveido adapteri ar metodem, kas atbild par RecyclerView funkcionalitāti
        adapter = new FirebaseRecyclerAdapter<Stories, StoriesHolder>(options) {
            @NonNull
            @Override
            public StoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //piesaista costume view pie recyclerView katra elementa
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_layout_main, parent, false);
                return new StoriesHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final StoriesHolder holder, final int position, @NonNull final Stories model) {
                //izveidojam jaunu sarakstu
                final List<User> list = new ArrayList<>();

                //parāda story nosaukumu sarakstā
                holder.textView.setText(model.getTittle());

                if (!model.isRed()){
                    holder.cont.setEnabled(false);
                    holder.reset.setEnabled(false);
                    holder.textView.append(" (in dev.)");
                }

                //piesaista funkcionalitāti pie cont pogas
                holder.cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izveido referenci uz saglabāto pozīciju stāstā
                        rootRef.child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //klausās izmaiņas un dabū sarakstu to saglabātajām pozīcijām
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    //piesaista katru pozīciju sarakstam
                                    user = ds.getValue(User.class);
                                    list.add(user);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });

                        /* Metode nodrošina pārbaudi un 1000 milsec, vai dati ir ielādējušies vai nē.
                         Ja metode nebūtu izveidota, tad visu laiku atvērtos jauna aktivitāte, kad
                         būtu reģistrētas izmaiņas datubāzē */
                        final Handler handler = new Handler();
                        final int delay = 100; //gaidīšanas laiks
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!list.isEmpty()){
                                    //izveido jaunu intent un padod extra mainīgos StoryActivity
                                    Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("PROJECT_ID", position);
                                    extras.putInt("STORY_POSITION", list.get(position).getPosition());
                                    extras.putInt("STORY_ID", model.getPosition_on_tree());
                                    extras.putInt("CHAPTER_SIZE", model.getChapter_size());
                                    intent.putExtras(extras);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    handler.postDelayed(this, delay);
                                }
                            }
                        }, delay);


                    }
                });

                //ļauj lietotājam resetot saglabāto pozīciju stāstā
                holder.reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = new User(0);
                        //saglabā vērtību DB
                        rootRef.child("users").child(mAuth.getUid()).child(String.valueOf(position)).setValue(user);
                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    /** pārbauda vai izmaiņas nav notikušas sarakstā */
    @Override
    protected void onStart() {
        super.onStart();
        //ja internets nav pieejams, aizver aplikāciju
        if (isNetworkAvailable(getApplicationContext())) {
            finish();
        }
        adapter.startListening();
    }

    //piesaista itemView objektus, no single_layout_main
    class StoriesHolder extends RecyclerView.ViewHolder{
        TextView textView;
        Button reset;
        Button cont;

        StoriesHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_main_storyName);
            reset = itemView.findViewById(R.id.button_reset);
            cont = itemView.findViewById(R.id.button_continue);
        }
    }

    /** piesaista menu pie toolbar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    /** nodrošina funckionalitāti elementiem */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Pārbuada vai internets ir pieejams */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    Log.i("Class", info[i].getState().toString());
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
