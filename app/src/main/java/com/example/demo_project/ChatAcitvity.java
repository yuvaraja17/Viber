package com.example.demo_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo_project.Adapter.ChatAdapter;
import com.example.demo_project.Models.MessagesModel;
import com.example.demo_project.databinding.ActivityChatAcitvityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatAcitvity extends AppCompatActivity {
ActivityChatAcitvityBinding binding;
ChatAdapter adapter;
ArrayList<MessagesModel> messages;
FirebaseDatabase database;
   String senderRoom,recieverRoom;
FirebaseAuth mauth;
FirebaseStorage storage;
ProgressDialog dialog;
    String senderUid;
    String recieverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changeStatusBarColor();
   database = FirebaseDatabase.getInstance();
     storage= FirebaseStorage.getInstance();
     dialog=new ProgressDialog(this);
     dialog.setMessage("Uploading image....");
     dialog.setCancelable(false);



   getSupportActionBar().hide();

   senderUid= FirebaseAuth.getInstance().getUid();
   recieverUid=getIntent().getStringExtra("UserId");
        String userName=getIntent().getStringExtra("UserName");
        String ProfilePic=getIntent().getStringExtra("ProfilePic");


           senderRoom=senderUid+recieverUid;
           recieverRoom=recieverUid+senderUid;





       binding.name.setText(userName);
      Picasso.get().load(ProfilePic).placeholder(R.drawable.avatar).into(binding.profile);

      binding.backArrow.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             finish();
          }
      });

        database.getReference().child("presence").child(recieverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String status=snapshot.getValue(String.class);
                    if(!status.isEmpty())
                    {
                        if(status.equals("Offline")){
                            binding.status.setVisibility(View.GONE);
                        }
                        else
                        {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//Typing status
        final Handler handler=new Handler();
binding.messageBox.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Override
    public void afterTextChanged(Editable s) {
        database.getReference().child("presence").child(senderUid).setValue("typing..");
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(userStoppedTyping,1000);
    }
    Runnable userStoppedTyping =new Runnable(){

        @Override
        public void run() {
            database.getReference().child("presence").child(senderUid).setValue("Online");
        }
    };
});

      messages=new ArrayList<>();
      adapter=new ChatAdapter(messages,this,senderRoom,recieverRoom);
      binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);



        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    MessagesModel message=dataSnapshot.getValue(MessagesModel.class);
                  message.setMessageId(dataSnapshot.getKey());
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });





        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String msg= binding.messageBox.getText().toString();
              Date date=new Date();
               MessagesModel message=new MessagesModel(msg,senderUid,date.getTime());

               binding.messageBox.setText("");

               String randomKey=database.getReference().push().getKey();

                HashMap<String,Object> lastMsgObj=new HashMap<>();
                lastMsgObj.put("lastMsg",message.getMessage());
               lastMsgObj.put("lastMsgTime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(recieverRoom).updateChildren(lastMsgObj);


                database.getReference().child("chats")
                       .child(senderRoom)
                       .child("messages")
                       .child(randomKey)
                       .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {

                       database.getReference().child("chats")
                       .child(recieverRoom)
                               .child("messages")
                               .child(randomKey)
                               .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {

                           }
                       });
                   }
               });
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==25)
        {
            if(data!=null)
            {
                if(data.getData()!=null)
                {
                    Uri selectedImage=data.getData();
                    Calendar calender=Calendar.getInstance();
                    StorageReference reference=storage.getReference().child("chats").child(calender.getTimeInMillis()+"");
                    dialog.show();
                   reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful())
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                     String filePath=uri.toString();
                                      String msg= binding.messageBox.getText().toString();
             Date date=new Date();
               MessagesModel message=new MessagesModel(msg,senderUid,date.getTime());
               message.setMessage("photo");
               message.setImageUrl(filePath);
               binding.messageBox.setText("");

               String randomKey=database.getReference().push().getKey();

                                        HashMap<String,Object> lastMsgObj=new HashMap<>();
                                        lastMsgObj.put("lastMsg",message.getMessage());
                                  lastMsgObj.put("lastMsgTime",date.getTime());

                                       database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(recieverRoom).updateChildren(lastMsgObj);


               database.getReference().child("chats")
                       .child(senderRoom)
                       .child("messages")
                       .child(randomKey)
                       .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       database.getReference().child("chats")
                       .child(recieverRoom)
                               .child("messages")
                               .child(randomKey)
                               .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {

                           }
                       });


                   }
               });
            }
        });

                                    }
                                });
                        }

                }
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId= FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }




    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorDarkAccent));
        }
    }






}