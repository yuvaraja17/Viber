package com.example.demo_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_project.Adapter.UserAdapter;
import com.example.demo_project.Models.Status;
import com.example.demo_project.Models.Users;
import com.example.demo_project.Models.UsersStatus;
import com.example.demo_project.databinding.ActivityHomeBinding;
import com.example.demo_project.databinding.AfterLogoutBinding;
import com.example.demo_project.databinding.SampleDialogBinding;
import com.example.demo_project.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity { FloatingActionButton floatingActionButton;
ActivityHomeBinding binding;
//TopStatusAdapter statusAdapter;
ArrayList<UsersStatus> usersStatuses;
FirebaseDatabase database;
SharedPreferences sp;
private FirebaseAuth mauth;
TabLayout tabLayout;
ProgressDialog p;
Users Statususer;

ProgressDialog dialog;
UserAdapter userAdapter;
//UserAdapter newuserAdapter;
ArrayList<Users> list= new ArrayList<>();
//ArrayList<Users> newList=new ArrayList<Users>();
//ArrayList<String> ui=new ArrayList<>();
String[] interest ={"Product Training","Friends","Animation","Art","Blogging","books","Board games","calligraphy","chatting","cooking","computer","dance"
,"Drawing","Drama","Dating","Fashion","gaming","graphic design","gym","hacking","magic","pets","yoga","Zumba"};
int selectedPosition=-1;
String mUID;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // tabLayout=(TabLayout)findViewById(R.id.main_tabs);
       // tabLayout.showContextMenu();
    getSupportActionBar().setTitle("Interest_Messenger");
    binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//    binding.statusList.showShimmerAdapter();
        mauth= FirebaseAuth.getInstance();

       // showstartDialog();

        changeStatusBarColor();
          database= FirebaseDatabase.getInstance();


    p = new ProgressDialog(HomeActivity.this);
    p.setTitle("Scanning..");
    p.setMessage("Searching for Friends with same interest");
    p.setCancelable(false);

    dialog=new ProgressDialog(this);
    dialog.setTitle("Uploading Status....");
    dialog.setCancelable(false);


    usersStatuses=new ArrayList<>();
    userAdapter=new UserAdapter(this,list);
  //  newuserAdapter=new UserAdapter(this,list);
   // statusAdapter=new TopStatusAdapter(this,usersStatuses);
    sp = PreferenceManager.getDefaultSharedPreferences(this);
    String id = sp.getString("UID", "");

    LinearLayoutManager layoutManager=new LinearLayoutManager(this);;
    layoutManager.setOrientation(RecyclerView.HORIZONTAL);
//    binding.statusList.setLayoutManager(layoutManager);
   // binding.statusList.setAdapter(statusAdapter);
    binding.recyclerView.setAdapter(userAdapter);
 //   binding.recyclerView.setAdapter(newuserAdapter);
/**
ui.clear();
    //existing user(Important)
  database.getReference().child("chats").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            newList.clear();
            String str= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            for(DataSnapshot datasnapshot:snapshot.getChildren())
            {
                String str1=datasnapshot.getKey();
                assert str1 != null;
                String reciever=str1.substring(0,28);
                str1=str1.substring(28);

                if(str.equals(str1)&&(!ui.contains(reciever)))
                {
                    ui.add(reciever);
                }
            }
           // if(newList.size()==0)
           // Toast.makeText(HomeActivity.this,"Please select your interest to match with friends",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
  for(int i=0;i<ui.size();i++) {
      database.getReference().child("Users").child(ui.get(i)).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              Users user = snapshot.getValue(Users.class);
              newList.add(user);
              newuserAdapter.notifyDataSetChanged();


          }

          @Override
          public void onCancelled(@NonNull @NotNull DatabaseError error) {

          }

      });
      if(ui.size()!=0)
          Toast.makeText(HomeActivity.this,"Please select your interest to match with friends",Toast.LENGTH_SHORT).show();
  }

**/


    //Intial message
    if (list.size()==0) {
       binding.recyclerView.showShimmerAdapter();
        binding.recyclerView.setVisibility(View.GONE);
        binding.recyclerView.hideShimmerAdapter();
       // binding.notfoundanimae.setVisibility(View.GONE);
       // binding.welcomeanimae.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.VISIBLE);
    }
    else {
        binding.recyclerView.showShimmerAdapter();
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);
      //  binding.notfoundanimae.setVisibility(View.GONE);
      //  binding.welcomeanimae.setVisibility(View.GONE);
    }


    binding.floating.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
          switch (event.getAction()) {
              case MotionEvent.ACTION_DOWN: {
                  v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                  v.invalidate();
                  break;
              }
              case MotionEvent.ACTION_UP: {
                  v.getBackground().clearColorFilter();
                  v.invalidate();
                  break;
              }
          }
          return false;
      }
  });

    binding.floating.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Select Interest");

             builder.setSingleChoiceItems(interest,selectedPosition , new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();



                 }
             });
             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     binding.recyclerView.showShimmerAdapter();
                     Users user=new Users();
                     database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Interest").setValue(interest[selectedPosition]);
                      user.setInterest(interest[selectedPosition]);
                     dialog.dismiss();
                      p.show();

                     Query query = database.getReference().child("Users").orderByChild("Interest").equalTo(interest[selectedPosition]);
                     query.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                             list.clear();


                                 for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                     if (FirebaseAuth.getInstance().getUid().equals(dataSnapshot.getKey())) {
                                         continue;
                                     }
                                    else {

                                         Users user = dataSnapshot.getValue(Users.class);
                                         user.setUserId(dataSnapshot.getKey());
                                         list.add(user);
                                     }
                                 }
                                 if(list.size()==0)
                                 {
                                     binding.recyclerView.setVisibility(View.GONE);
                                   //  binding.notfoundanimae.setVisibility(View.VISIBLE);
                                   //  binding.welcomeanimae.setVisibility(View.GONE);
                                     binding.emptyView.setVisibility(View.GONE);
                                     Toast.makeText(HomeActivity.this, "No user found with "+interest[selectedPosition]+" interest.Please select any other interest.", Toast.LENGTH_SHORT).show();

                                 }
                                 else
                                 {
                                     mUID=user.getUserId();
                                     binding.recyclerView.hideShimmerAdapter();
                                     binding.recyclerView.setVisibility(View.VISIBLE);
                                     binding.emptyView.setVisibility(View.GONE);
                                  //   binding.notfoundanimae.setVisibility(View.GONE);
                                  //   binding.welcomeanimae.setVisibility(View.GONE);
                                 }

                                     userAdapter.notifyDataSetChanged();

                                 p.dismiss();


                             }

                         @Override
                         public void onCancelled(@NonNull @NotNull DatabaseError error) {
                               Toast.makeText(HomeActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();
                         }
                     });
                 }

             });
             builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                 }
             });
            builder.create().show();
        }

    });
/**
    //For showing status
    database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()) {
                usersStatuses.clear();
                for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                    UsersStatus status = new UsersStatus();
                    status.setName(storySnapshot.child("name").getValue(String.class));
                    status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                    status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                    ArrayList<Status> statuses = new ArrayList<>();

                    for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                        Status sampleStatus = statusSnapshot.getValue(Status.class);
                        statuses.add(sampleStatus);
                    }

                    status.setStatuses(statuses);
                    usersStatuses.add(status);
                }
               binding.statusList.hideShimmerAdapter();
                statusAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

         }
    });











    //For getting status.
    database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Statususer = snapshot.getValue(Users.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

**/



//Bottom navigation
    binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

            switch(item.getItemId())
            {
                case R.id.status_bottom:
                    Toast.makeText(HomeActivity.this, "this activity will be updated soon", Toast.LENGTH_SHORT).show();
                    /**
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent,75);**/
            }

            return false;
        }
    });
    updateToken(FirebaseInstanceId.getInstance().getToken());
}

public void updateToken(String token){
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
    Token mToken = new Token(token);
    ref.child(mUID).setValue(mToken);
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UsersStatus userStatus = new UsersStatus();
                                    userStatus.setName(Statususer.getUserName());
                                    userStatus.setProfileImage(Statususer.getProfilePicture());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage", userStatus.getProfileImage());
                                    obj.put("lastUpdated", userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId= FirebaseAuth.getInstance().getUid();
        if(currentId != null)
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();

        if(currentId != null)
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorDarkAccent));
        }
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.Search:
                Toast.makeText(this,"Search feature would be Enabled soon.",Toast.LENGTH_SHORT).show();
                break;

            case R.id.GroupChat:
                startActivity(new Intent(HomeActivity.this,GroupChatActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                break;


            case R.id.MyProfile:
                Intent i=new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                break;
            case R.id.LogOut:
                AfterLogoutBinding dialogbinding1=AfterLogoutBinding.inflate(getLayoutInflater());
                AlertDialog d=new AlertDialog.Builder(this)
                        .setView(dialogbinding1.getRoot())
                        .setCancelable(false)
                        .create();

                d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                d.getWindow().setWindowAnimations(R.style.DailogAnimation);
                dialogbinding1.CancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                dialogbinding1.LogoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mauth.signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                        Toast.makeText(HomeActivity.this,"User Logged Out",Toast.LENGTH_SHORT).show();
                    }
                });
                d.show();
                break;
            case R.id.Settings:
                startActivity(new Intent(HomeActivity.this,allSettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showstartDialog(){
    SampleDialogBinding dialogBinding=SampleDialogBinding.inflate(getLayoutInflater());
  AlertDialog d=new AlertDialog.Builder(this)
       .setView(dialogBinding.getRoot())
          .setCancelable(false)
          .create();

         d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         d.getWindow().setWindowAnimations(R.style.DailogAnimation);
        dialogBinding.OkayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     d.dismiss();
            }
        });
     d.show();
    }

}