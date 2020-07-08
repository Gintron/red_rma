package com.marijan.red;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class CreateArticleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 44;

    ImageView articleImage;
    EditText title, content;
    Spinner category;
    Button publishArticleBtn;
    TextView ctgTxt;
    RadioGroup radioGroup;

    Uri mImageUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    String miUrlOk = "";
    String selectedString;
    boolean isPublic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        radioGroup = findViewById(R.id.article_radio_group);
        articleImage = findViewById(R.id.article_image);
        title = findViewById(R.id.name_of_article);
        content= findViewById(R.id.name_of_content);
        category = findViewById(R.id.spinner_m);
        ctgTxt = findViewById(R.id.category_txt);
        publishArticleBtn = findViewById(R.id.publish_article_btn);

        isWriteStoragePermissionGranted();
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        publishArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sTitle = title.getText().toString();
                String sContent = content.getText().toString();

                if(TextUtils.isEmpty(sTitle)){
                    Toast.makeText(CreateArticleActivity.this, "Enter a title", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(sContent)){
                    Toast.makeText(CreateArticleActivity.this, "Enter text", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadImage_10();
                }
            }
        });
        articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        showCategory();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.article_private_radio_button:
                        isPublic = false;
                        hideCategory();
                        break;
                    case R.id.article_public_radio_button:
                        isPublic = true;
                        showCategory();
                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();

            articleImage.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedString = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadImage_10(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();

        File actualImage = new File(mImageUri.getPath());

        try{

            Bitmap compressedImage = new Compressor(this)
                    .setQuality(30)
                    .compressToBitmap(actualImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] finalImage = baos.toByteArray();
            if (mImageUri != null){
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                uploadTask = fileReference.putBytes(finalImage);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            miUrlOk = downloadUri.toString();

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                            String articleid = reference.push().getKey();
                            String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("postid", articleid);
                            hashMap.put("postimage", miUrlOk);
                            hashMap.put("title", title.getText().toString());
                            hashMap.put("text", content.getText().toString());
                            if(isPublic) {
                                hashMap.put("category", selectedString);
                            }else{
                                hashMap.put("category", "Friends");
                            }
                            hashMap.put("userid", myid);
                            hashMap.put("type", "article");
                            hashMap.put("publisher", Persitance.currentUserName);
                            hashMap.put("time", ServerValue.TIMESTAMP);

                            reference.child(articleid).setValue(hashMap);

                            pd.dismiss();

                            //startActivity(new Intent(PostActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(CreateArticleActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateArticleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(CreateArticleActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }


    }
    private void showCategory(){
        category.setVisibility(View.VISIBLE);
        ctgTxt.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

    }
    private void hideCategory(){
        category.setVisibility(View.INVISIBLE);
        ctgTxt.setVisibility(View.INVISIBLE);
    }
    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }
}
