package com.example.livechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendList;
    private EditText searchEt;
    private String str = "";
    private DatabaseReference userRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        searchEt = findViewById(R.id.search_user_text);
        findFriendList = findViewById(R.id.find_friends_list);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchEt.getText().toString().equals(""))
                {
                    Toast.makeText(FindPeopleActivity.this, "Please write Name..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   str = s.toString();
                   onStart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = null;

        if (str.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef, Contacts.class)
                    .build();
        }
        else {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef
                            .orderByChild("name")
                    .startAt(str)
                    .endAt(str + "\uf0ff")
                    ,Contacts.class)
                    .build();

        }
        FirebaseRecyclerAdapter<Contacts, NotificationActivity.FindFriendsViewHolder>firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Contacts, NotificationActivity.FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationActivity.FindFriendsViewHolder holder, int position, @NonNull Contacts model) {
                holder.userNameTxt.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.profileImageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id= getRef(position).getKey();
                        Intent intent = new Intent(FindPeopleActivity.this,ProfileActivity.class);

                        intent.putExtra("visit_user_id",visit_user_id);
                        intent.putExtra("profile_image",model.getImage());
                        intent.putExtra("profile_name",model.getName());
                        startActivity(intent);





                    }
                });

            }

            @NonNull
            @Override
            public NotificationActivity.FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,parent,false);
                NotificationActivity.FindFriendsViewHolder viewHolder= new NotificationActivity.FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        findFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class  NotificationViewHolder extends RecyclerView.ViewHolder{

        Button cancelBtn;
        Button acceptBtn;
        TextView userNameTxt;
        Button   videoCallBtn;
        ImageView profileImageView;
        RelativeLayout cardView;


        public  NotificationViewHolder(@NonNull View itemView){

            super(itemView);



            userNameTxt = itemView.findViewById(R.id.image_contact);
            videoCallBtn = itemView.findViewById(R.id.call_btn);
            profileImageView = itemView.findViewById(R.id.image_contact);
            cardView= itemView.findViewById(R.id.card_view1);
        }
    }
}
