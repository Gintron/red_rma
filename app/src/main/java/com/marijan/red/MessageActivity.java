package com.marijan.red;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marijan.Profile;
import com.marijan.red.Adapter.MessageAdapter;
import com.marijan.red.Fragments.APIService;
import com.marijan.red.Model.Chats;
import com.marijan.red.Model.Messages;
import com.marijan.red.Notifications.Client;
import com.marijan.red.Notifications.Data;
import com.marijan.red.Notifications.MyResponse;
import com.marijan.red.Notifications.Sender;
import com.marijan.red.Notifications.Token;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements
        MessageAdapter.ClickListener{

    ImageView profile_image;
    TextView username;

    FirebaseUser currentUser;
    DatabaseReference reference;
    private StorageReference mImageStorage;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Messages> mchat;

    RecyclerView recyclerView;

    Intent intent;

    String receiverId,receiverName,receiverImage="default",senderId,
            senderName,messageTime,type,from,senderImage="default";
    String isSeen="false";

    APIService apiService;

    ImageView sendImage;
    private static final int GALLERY_PICK =1;

    byte notify = 0;

    private LinearLayoutManager mLinearLayout;


    Dialog imageDialog;
    PhotoView dialogImage;
    ImageView exit_btn,save_btn;
    String imageUrl;

    String MESSAGE_KEY;
    public static final String ALL_CHATS="all_chats";
    public static final String ALL_MESSAGES="all_messaages";
    SimpleDateFormat dateFormat;
    boolean IS_CHAT_EXISTS=false;
    static final int NUMBER_OF_ITEMS_TO_LOAD=10;
    static final int REFRESHING=1,SEND=2;
    int FROM;
    static int PAGE_NUMBER=1;
    SwipeRefreshLayout swipeRefreshLayout;
    Query query;
    ChildEventListener listener;

    String download_url;
    ProgressBar progressBar,progressBarSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });



        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        mchat = new ArrayList<>();
        progressBar=findViewById(R.id.progressbar);
        progressBarSend=findViewById(R.id.progressBarSend);
        swipeRefreshLayout=findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mLinearLayout = new LinearLayoutManager(getApplicationContext());
        mLinearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager( mLinearLayout);


        profile_image = findViewById(R.id.profile_image_right);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        sendImage = findViewById(R.id.image_select);
        text_send = findViewById(R.id.text_send);


        intent = getIntent();
        from = intent.getStringExtra("from");
        receiverId = intent.getStringExtra("receiverId");
        receiverName = intent.getStringExtra("receiverName");
        receiverImage = intent.getStringExtra("receiverImage");
        if(receiverImage==null){ receiverImage="default"; }
        senderId=currentUser.getUid();




        MESSAGE_KEY= currentUser.getUid()+receiverId;

        if(receiverId.equals(currentUser.getUid())){

            receiverId=intent.getStringExtra("senderId");
            receiverName = intent.getStringExtra("senderName");
            receiverImage = intent.getStringExtra("senderImage");
            senderId=intent.getStringExtra("receiverId");
            if(receiverImage==null){ receiverImage="default"; }

            MESSAGE_KEY= receiverId+currentUser.getUid();
        }

        if(from.equals("notification")){

            String messageKey=intent.getStringExtra("messageKey");

            MESSAGE_KEY= messageKey;
        }




        if(from.equals("users")||from.equals("notification")){

            btn_send.setVisibility(View.GONE);
            sendImage.setVisibility(View.GONE);
            progressBarSend.setVisibility(View.VISIBLE);
            text_send.setEnabled(false);
            checkIfChatExists();
        }
        else{IS_CHAT_EXISTS=true;
        messageSeen();
        }



        messageAdapter = new MessageAdapter(MessageActivity.this, mchat, receiverImage);
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.setOnItemClickListener(MessageActivity.this);


        mImageStorage = FirebaseStorage.getInstance().getReference();

        isWriteStoragePermissionGranted();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notify = 1;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    newMessage(msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        username.setText(receiverName);
        if (receiverImage.equals("default")){
            profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(this).load(receiverImage).apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(profile_image);
        }


        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_PICK);
            }
        });


        readMessages();
        fullImageDialog();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mchat.clear();
                PAGE_NUMBER++;
                FROM=REFRESHING;
                readMessages();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, Profile.class);

                intent.putExtra("idProfile",receiverId);


               startActivity(intent);
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, Profile.class);

                intent.putExtra("idProfile",receiverId);


                startActivity(intent);
            }
        });
    }


    void messageSeen(){

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child(ALL_CHATS).child(MESSAGE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()) {
                    Chats chats=dataSnapshot.getValue(Chats.class);
                    if(!chats.getLastSenderId().equals(currentUser.getUid())&&chats.getSeen().equals("false")){


                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("seen", "true");
                        reference.child(ALL_CHATS).child(MESSAGE_KEY).updateChildren(updates);
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }

    private void newMessage(final String message) {

        reference = FirebaseDatabase.getInstance().getReference();

        Date currentTime = Calendar.getInstance().getTime();


        messageTime=dateFormat.format(currentTime);
        senderName= Persitance.currentUserName;
        senderImage=Persitance.currentUserImage;
        if(senderImage==null){
            senderImage="default";
        }

        FROM=SEND;

        isSeen="false";
        type="text";


        sendMessage(message);


    }


    void sendMessage(final String message){



        String pushKey=reference.child(ALL_MESSAGES).child(MESSAGE_KEY).push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", currentUser.getUid());
        hashMap.put("receiver", receiverId);
        hashMap.put("message", message);
        hashMap.put("messageSeen", isSeen);
        hashMap.put("type", type);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("lastSender", currentUser.getUid());
        hashMap.put("messageId", pushKey);


        reference.child(ALL_MESSAGES).child(MESSAGE_KEY).child(pushKey).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    if(IS_CHAT_EXISTS) {

                        Map<String, Object> updates = new HashMap<String, Object>();


                        updates.put("lastMessage", message);
                        updates.put("time", messageTime);
                        updates.put("lastSenderId",currentUser.getUid());
                        updates.put("seen", "false");
                        updates.put("type", "text");


                       reference.child(ALL_CHATS).child(MESSAGE_KEY).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    sendNotification(message);
                                    //readMessages(0);




                                }
                                else
                                {
                                    Toast.makeText(MessageActivity.this,"Something wrong",Toast.LENGTH_LONG).show();

                                }

                            }
                        });
                    }
                    else
                    {

                        Chats chats=new Chats(currentUser.getUid(),receiverId,senderName,receiverName,message,type,"false",currentUser.getUid(),messageTime,receiverImage,senderImage);

                        reference.child(ALL_CHATS).child(MESSAGE_KEY).setValue(chats).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    sendNotification(message);





                                } else {

                                    Toast.makeText(MessageActivity.this,"Something wrong",Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                    }

                } else {

                    Toast.makeText(MessageActivity.this,"Something wrong",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void sendNotifiaction(final String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    String type = "Message";
                    Data data = new Data(currentUser.getUid(),Persitance.currentUserName,Persitance.currentUserImage, R.mipmap.ic_launcher, username+": "+message, "New Message",
                            receiver,MESSAGE_KEY, type, "0");


                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(MessageActivity.this, "Could not send the message", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readMessages(){

        reference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference messageRef=reference.child(ALL_MESSAGES).child(MESSAGE_KEY);

        query=messageRef.orderByChild("timestamp").limitToLast(PAGE_NUMBER*NUMBER_OF_ITEMS_TO_LOAD);

        query.keepSynced(false);

        //if(listener!=null){
         //   messageRef.removeEventListener(listener);
        //}
       listener=query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if(dataSnapshot.exists()) {


                    Messages messages = dataSnapshot.getValue(Messages.class);


                    mchat.add(messages);

                    messageAdapter.notifyDataSetChanged();


                    if (FROM == REFRESHING) {
                        recyclerView.smoothScrollToPosition(10);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        recyclerView.smoothScrollToPosition(mchat.size() - 1);

                    }

                    if (!messages.getLastSender().equals(currentUser.getUid()) && messages.getMessageSeen().equals("false")) {

                        Map<String, Object> updates = new HashMap<String, Object>();


                        String pushKey = messages.getMessageId();
                        updates.put("messageSeen", "true");


                        reference.child(ALL_MESSAGES).child(MESSAGE_KEY).child(pushKey).updateChildren(updates);
                    }

                }




            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



    }



    String TAG = "Permission";


    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    byte[] compressImage(Uri selectedImage){
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(
                    selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bmp = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {

            e.printStackTrace();
        }

        return  byteArray;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK&& resultCode == RESULT_OK){

            sendImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            Uri selectedImageURI = data.getData();


            final StorageReference filePath = mImageStorage.child("message_images/"+selectedImageURI.getLastPathSegment());

            final UploadTask uploadTask=filePath.putBytes(compressImage(selectedImageURI));

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Toast.makeText(MessageActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    uploadImage(uploadTask,filePath);
                    notify = 2;

                    sendNotification("Has sent you a image");

                }
            });




        }

    }


    void sendNotification(String msg){

        if (notify==1) {
            sendNotifiaction(receiverId, Persitance.currentUserName, msg);
        }else if (notify ==2){
            sendNotifiaction(receiverId, Persitance.currentUserName, msg);
        }
        notify = 0;
    }


    void checkIfChatExists(){


        reference = FirebaseDatabase.getInstance().getReference();

        reference.child(ALL_CHATS).child(MESSAGE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBarSend.setVisibility(View.GONE);
                btn_send.setVisibility(View.VISIBLE);
                sendImage.setVisibility(View.VISIBLE);
                text_send.setEnabled(true);


                if(dataSnapshot.exists()) {
                    IS_CHAT_EXISTS = true;
                    Chats chats=dataSnapshot.getValue(Chats.class);
                    if(!chats.getLastSenderId().equals(currentUser.getUid())&&chats.getSeen().equals("false")){

                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("seen", "true");
                        reference.child(ALL_CHATS).child(MESSAGE_KEY).updateChildren(updates);
                    }

                }
                else{IS_CHAT_EXISTS = false; }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MessageActivity.this, "Something wrong try again!", Toast.LENGTH_LONG).show();

                finish();

            }
        });



    }

    @Override
    public void onItemClick(int position, View v) {

        String message_type = mchat.get(position).getType();

        if(message_type.equals("image")) {

            imageUrl=mchat.get(position).getMessage();

            Glide.with(this).load(imageUrl).into(dialogImage);

            imageDialog.show();
        }
    }


    class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String image_url = params[0];
                URL url = new URL(image_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                return myBitmap;
            }catch (Exception e){

            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {


            if(bitmap!=null) {
                Toast.makeText(MessageActivity.this,"Download Complete",Toast.LENGTH_SHORT).show();

                saveBitmapToLocal(bitmap);
            }

            super.onPostExecute(bitmap);
        }
    }

    public static String saveBitmapToLocal(Bitmap bm) {
        String path = null;
        try {
            File file = createImageFileName();
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            path = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    public static File createImageFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMAGE_" + timestamp + "_";
        File imageFile = File.createTempFile(prepend, ".jpg", createImageFolder());
        return imageFile;
    }

    public static File createImageFolder() {
        File destFile = new File(Environment.getExternalStorageDirectory().toString());
        File mImageFolder = new File(destFile, "Red");
        if (!mImageFolder.exists()) {
            mImageFolder.mkdirs();
        }
        return mImageFolder;
    }

    void fullImageDialog(){

        imageDialog=new Dialog(this,android.R.style.Theme_NoTitleBar_Fullscreen);
        imageDialog.setContentView(R.layout.image_dialog);
        exit_btn=imageDialog.findViewById(R.id.exit_btn);
        dialogImage=imageDialog.findViewById(R.id.imageView);
        save_btn=imageDialog.findViewById(R.id.save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DownloadImage downloadImage=new DownloadImage();
                downloadImage.execute(imageUrl.replace(" ","%20"));
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });
    }


    void uploadImage(UploadTask uploadTask,final StorageReference ref) {


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }


                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                     download_url = task.getResult().toString();
                     continueUploadImage(download_url);
                } else {

                }
            }
        });


    }


    void continueUploadImage(final String download_url){

        Date currentTime = Calendar.getInstance().getTime();
        messageTime = dateFormat.format(currentTime);
        type = "image";
        isSeen = "false";

        reference = FirebaseDatabase.getInstance().getReference();

        String pushKey = reference.child(ALL_MESSAGES).child(MESSAGE_KEY).push().getKey();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", currentUser.getUid());
        hashMap.put("receiver", receiverId);
        hashMap.put("message", download_url);
        hashMap.put("messageSeen", isSeen);
        hashMap.put("type", type);
        hashMap.put("timestamp", ServerValue.TIMESTAMP);
        hashMap.put("lastSender", currentUser.getUid());
        hashMap.put("messageId", pushKey);

        reference.child(ALL_MESSAGES).child(MESSAGE_KEY).child(pushKey).setValue(hashMap);

        if (IS_CHAT_EXISTS) {

            Map<String, Object> updates = new HashMap<String, Object>();


            updates.put("lastMessage", download_url);
            updates.put("time", messageTime);
            updates.put("lastSenderId", currentUser.getUid());
            updates.put("seen", "false");
            updates.put("type", "image");


            reference.child(ALL_CHATS).child(MESSAGE_KEY).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        sendNotification(download_url);


                        sendImage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    }

                }
            });
        }
        if (!IS_CHAT_EXISTS) {
            Chats chats = new Chats(currentUser.getUid(), receiverId, senderName, receiverName, download_url, type, isSeen, messageTime, currentUser.getUid(), receiverImage, Persitance.currentUserImage);

            reference.child(ALL_CHATS).child(MESSAGE_KEY).setValue(chats).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {



                        sendImage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        sendNotification(download_url);

                    } else {

                        Toast.makeText(MessageActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();

                    }

                }
            });
        }
    }
}

