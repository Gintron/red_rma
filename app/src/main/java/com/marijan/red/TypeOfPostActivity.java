package com.marijan.red;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class TypeOfPostActivity extends AppCompatActivity {
    Button articleButton, media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_type_of_post);


        articleButton = findViewById(R.id.article_button);
        media = findViewById(R.id.media_button);
        articleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeOfPostActivity.this, CreateArticleActivity.class);
                startActivity(intent);
            }
        });
        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeOfPostActivity.this, CreateMediaActivity.class);
                startActivity(intent);
            }
        });
    }
}
