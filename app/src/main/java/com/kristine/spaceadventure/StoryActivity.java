package com.kristine.spaceadventure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity {
    private DatabaseReference rootRef;
    private List<Chapters> list;
    private int storyPosition, storyId;
    private TextView story;
    private Button left, right, finish, next;
    private ConstraintLayout layout;
    private FirebaseAuth mAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //dabū FirebaseUser un Database references
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //dabū view elementus
        story = findViewById(R.id.textView_story);
        left = findViewById(R.id.button_left);
        right = findViewById(R.id.button_right);
        finish = findViewById(R.id.button_finish);
        next = findViewById(R.id.button_next);
        layout = findViewById(R.id.story_layout);

        //dabū MainActivity padotos elementus
        Bundle extras = getIntent().getExtras();
        storyId = extras.getInt("STORY_ID");
        final int chapterSize = extras.getInt("CHAPTER_SIZE");
        storyPosition = extras.getInt("STORY_POSITION");

        //izsauc referenci uz stāsta nodaļu (chapter)
        rootRef.child("stories").child(String.valueOf(storyId)).child("chapters").addValueEventListener(new ValueEventListener() {
            /** dabū datus no DB un veic to apstrādi */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                //piesaista sarakstam elementus no izvēlēta stāsta (story)
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chapters chapters = ds.getValue(Chapters.class);
                    list.add(chapters);
                }

                //piesaista srory tekstu
                story.setText(list.get(storyPosition).getPlot());
                //loadBackground(list.get(startSize).getImage_url(), getApplicationContext());

                //pārbauda, vai izvēlētā sākuma pozīcija ir mazāka par nodaļu (chapter) skaitu
                if (storyPosition < chapterSize) {
                    //pārbauda, vai ir izvēles opcijas stāstam
                    if (list.get(storyPosition).isOptions()) {
                        //atspējo next pogu
                        next.setEnabled(false);

                        //parāda izvēles opcijas pogas ar tekstu
                        setOptionText(list.get(storyPosition).getOption_text().getOption_first(),
                                list.get(storyPosition).getOption_text().getOption_second());

                        //parāda izvēlēto rezultātu
                        setOptionAnswer(left, list.get(storyPosition).getOption_text().
                                getOption_story().getStory_first(), storyPosition, chapterSize);
                        setOptionAnswer(right, list.get(storyPosition).getOption_text().
                                getOption_story().getStory_second(), storyPosition, chapterSize);
                    }
                //izpildās, ja sākuma pozīcija ir vienāda ar nodaļu skaitu
                } else {
                    //izpildia to pašu funckiju kas iepriekš (var uzrakstīt efektīvāk)
                    if (list.get(storyPosition).isOptions()) {
                        next.setEnabled(false);

                        setOptionText(list.get(storyPosition).getOption_text().getOption_first(),
                                list.get(storyPosition).getOption_text().getOption_second());

                        setOptionAnswer(left, list.get(storyPosition).getOption_text().
                                getOption_story().getStory_first(), storyPosition, chapterSize);
                        setOptionAnswer(right, list.get(storyPosition).getOption_text().
                                getOption_story().getStory_second(), storyPosition, chapterSize);
                    }

                    //paslēpj left un right pogas
                    left.setVisibility(View.GONE);
                    right.setVisibility(View.GONE);

                    //atspējo next pogu, jo ir iziets story
                    next.setEnabled(false);
                    //parāda finish pogu, lai izietu no story
                    finish.setVisibility(View.VISIBLE);

                }

                //nodrošina next pogas funkcionalitāti (veic tādas pašas pārbaudes)
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storyPosition++;
                        //loadBackground(list.get(startSize).getImage_url(), getApplicationContext());
                        if (storyPosition < chapterSize){
                            story.setText(list.get(storyPosition).getPlot());

                            if (list.get(storyPosition).isOptions()){
                                setOptionText(list.get(storyPosition).getOption_text().getOption_first(),
                                        list.get(storyPosition).getOption_text().getOption_second());

                                setOptionAnswer(left, list.get(storyPosition).getOption_text().
                                        getOption_story().getStory_first(), storyPosition, chapterSize);
                                setOptionAnswer(right, list.get(storyPosition).getOption_text().
                                        getOption_story().getStory_second(), storyPosition, chapterSize);

                            } else {
                                left.setVisibility(View.GONE);
                                right.setVisibility(View.GONE);
                            }
                            //storyPosition++;
                        } else {
                            story.setText(list.get(storyPosition).getPlot());

                            if (list.get(storyPosition).isOptions()) {
                                setOptionText(list.get(storyPosition).getOption_text().getOption_first(),
                                        list.get(storyPosition).getOption_text().getOption_second());

                                setOptionAnswer(left, list.get(storyPosition).getOption_text().
                                        getOption_story().getStory_first(), storyPosition, chapterSize);
                                setOptionAnswer(right, list.get(storyPosition).getOption_text().
                                        getOption_story().getStory_second(), storyPosition, chapterSize);

                            } else {
                                left.setVisibility(View.GONE);
                                right.setVisibility(View.GONE);

                                next.setEnabled(false);
                                finish.setVisibility(View.VISIBLE);
                            }

                        }


                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        //iziet no story aktivitātes
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        //izviet no story aktivitātes(pieejams jebkurā brīdī)
        findViewById(R.id.button_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    /** izsauc kad iziet no aktivitātes */
    @Override
    protected void onStop() {
        super.onStop();
        User user = new User(storyPosition);
        //uzstāda jaunu postition vērtību DB, kur lietotājs ir palicis
        rootRef.child("users").child(mAuth.getUid()).child(String.valueOf(storyId)).setValue(user);
    }

    /** uzstādā izvēlētas opcijas funkcionalitāti */
    public void setOptionAnswer(Button button, final String text, final int start, final int end){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                next.setEnabled(true);
                story.setText(text);

                if (start == end) {
                    next.setEnabled(false);
                    finish.setVisibility(View.VISIBLE);
                }
            }
        });



    }

    /** uzstādā izvēlētas opcijas funkcionalitāti */
    public void setOptionText(String textFirst, String textSecond){
        left.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
        next.setEnabled(false);
        left.setText(textFirst);
        right.setText(textSecond);
    }

    /** Ielādē background img (pagaidām netiek lietots) */
    public void loadBackground(String url, Context context){
        Glide.with(context).asBitmap().load(url).apply(new RequestOptions().
                diskCacheStrategy(DiskCacheStrategy.RESOURCE).fitCenter()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                final Drawable dr = new BitmapDrawable(resource);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.setBackgroundDrawable(dr);
                    }
                });

            }
        });



    }

    /** aizver aktivitāti, ja back poga nospiesta */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}

