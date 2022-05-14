package com.example.demo_project.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.demo_project.ChatAcitvity;
import com.example.demo_project.Models.Users;
import com.example.demo_project.R;
import com.example.demo_project.databinding.ShowUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ImageView block;
    ArrayList<Users> list;
    FirebaseAuth firebaseAuth;
    String myUid;
    Context context;


    public UserAdapter(Context context, ArrayList<Users> list) {
        this.list = list;
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.show_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Users users = list.get(position);
        final String hisUID = list.get(position).getUserId();
        Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.userpic).into(holder.binding.profileImage);

        final ImagePopup imagePopup = new ImagePopup(context);
        imagePopup.setWindowHeight(800); // Optional
        imagePopup.setWindowWidth(800); // Optional
        imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
        imagePopup.setFullScreen(false); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        ImageView imageView = (ImageView) holder.binding.profileImage;
        if (users.getProfilePicture() != null)
            imagePopup.initiatePopupWithPicasso(users.getProfilePicture());
        else
            imagePopup.initiatePopup(imageView.getDrawable());    // Load Image from Drawable


        holder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();

            }
        });

        holder.binding.block.setImageResource(R.drawable.ic_baseline_block_24);
        checkIsBlocked(hisUID, holder, position);


        holder.binding.UserName.setText(users.getUserName());

        //Last Message
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + users.getUserId();
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time;
                            if (snapshot.child("lastMsgTime").getValue(Long.class) != null) {
                                time = snapshot.child("lastMsgTime").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                holder.binding.Time.setText(dateFormat.format(new Date(time)));
                            }
                            holder.binding.LastMessage.setText(lastMsg);
                        } else {
                            holder.binding.LastMessage.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ChatAcitvity.class);
//                intent.putExtra("UserId", users.getUserId());
//                intent.putExtra("ProfilePic", users.getProfilePicture());
//                intent.putExtra("UserName", users.getUserName());
                isBlockedORNot(hisUID);
//                context.startActivity(intent);

            }
        });
        //blocking
        holder.binding.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).isBlocked()) {
                    unBlockUser(hisUID);
                } else {
                    blockUser(hisUID);
                }
            }
        });
    }

    private void isBlockedORNot(String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(hisUID).child("BlockedUser").orderByChild("uid").equalTo(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.exists()) {
                        Toast.makeText(context, "Your are Blocked By the user Can't Message.....", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(context, ChatAcitvity.class);
//                    intent.putExtra("UserId", users.getUserId());
//                    intent.putExtra("ProfilePic", users.getProfilePicture());
//                    intent.putExtra("UserName", users.getUserName());
//                    isBlockedORNot(hisUID);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIsBlocked(String hisUID, ViewHolder holder, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(myUid).child("BlockedUser").orderByChild("uid").equalTo(hisUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.exists()) {
                        holder.binding.block.setImageResource(R.drawable.ic_baseline_block_24);
                        list.get(position).setBlocked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void blockUser(String hisUID) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("UID", hisUID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUser").child(hisUID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //blocked Successfull
                Toast.makeText(context, "Blocked Successfull", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed to block
                Toast.makeText(context, "Fail to Block the user.....", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unBlockUser(String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(myUid).child("BlockedUser").orderByChild("UID").equalTo(hisUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.exists()) {
                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "UNBLOCKED Successfully...", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "FAIl to UNBlock...........", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ShowUserBinding binding;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            block = itemView.findViewById(R.id.block);
            binding = ShowUserBinding.bind(itemView);
        }
    }
}
