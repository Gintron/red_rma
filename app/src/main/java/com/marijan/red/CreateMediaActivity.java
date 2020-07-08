package com.marijan.red;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.marijan.red.Model.VideoM;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;



public class CreateMediaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_VIDEO_REQUEST = 33;
    private static final int PICK_IMAGE_REQUEST = 43;

    ImageView mediaImage;
    VideoView videoView;
    EditText description;
    Spinner category;
    Button publishMedia, imageBtn, videoBtn;
    TextView ctgTxt;

    Uri mImageUri, videoUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    String miUrlOk = "";
    String selectedString, type;
    boolean isPublic = true;
    RadioGroup radioGroup;
    MediaController mediaController;
    private long size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_media);
        mediaImage = findViewById(R.id.media);
        videoView = findViewById(R.id.video_view);
        category = findViewById(R.id.spinner_m);
        publishMedia = findViewById(R.id.publish_media_btn);
        description = findViewById(R.id.media_disc);
        ctgTxt = findViewById(R.id.ctg_m_txt);
        radioGroup = findViewById(R.id.media_radio_group);
        imageBtn = findViewById(R.id.img_btn);
        videoBtn = findViewById(R.id.video_btn);
        isWriteStoragePermissionGranted();

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        //setting up videoView and media controller
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        //choose video from gallery
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_VIDEO_REQUEST);
            }
        });

        //choose image from gallery
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }


        });
        publishMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sTitle = description.getText().toString();
                if(TextUtils.isEmpty(sTitle)) 
                    Toast.makeText(CreateMediaActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();

                else
                    uploadImage_10();
            }
        });

        showCategory();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.media_private_radio_button:
                        type = "followers";
                        isPublic = false;
                        hideCategory();
                        break;
                    case R.id.media_public_radio_button:
                        isPublic = true;
                        showCategory();
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();
            mediaImage.setImageURI(mImageUri);
            videoView.setVisibility(View.INVISIBLE);
        }
        if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mediaImage.setVisibility(View.INVISIBLE);
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            Log.d("URI", videoUri.toString());
        }

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

        try{

            if(videoUri != null){

                final StorageReference reference = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(videoUri));
                String destPath = this.getExternalFilesDir(null).getPath();

                uploadTask = reference.putFile(videoUri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
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
                            hashMap.put("videoUrl", miUrlOk);
                            hashMap.put("postimage", miUrlOk);
                            if(isPublic) {
                                hashMap.put("category", selectedString);
                            }else{
                                hashMap.put("category", "Friends");
                            }
                            hashMap.put("userid", myid);
                            hashMap.put("title", description.getText().toString());
                            hashMap.put("type", "video");
                            hashMap.put("publisher", Persitance.currentUserName);
                            hashMap.put("time", ServerValue.TIMESTAMP);
                            reference.child(articleid).setValue(hashMap);

                            pd.dismiss();


                            finish();

                        } else {
                            Toast.makeText(CreateMediaActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateMediaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(CreateMediaActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
            Bitmap actualImage1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            actualImage1.compress(Bitmap.CompressFormat.JPEG, 30, baos);
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
                            if(isPublic) {
                                hashMap.put("category", selectedString);
                            }else{
                                hashMap.put("category", "Friends");
                            }
                            hashMap.put("userid", myid);
                            hashMap.put("title", description.getText().toString());
                            hashMap.put("type", "image");
                            hashMap.put("publisher", Persitance.currentUserName);
                            hashMap.put("time", ServerValue.TIMESTAMP);
                            reference.child(articleid).setValue(hashMap);

                            pd.dismiss();


                            finish();

                        } else {
                            Toast.makeText(CreateMediaActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateMediaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(CreateMediaActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedString = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

