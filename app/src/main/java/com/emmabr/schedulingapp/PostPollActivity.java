package com.emmabr.schedulingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class PostPollActivity extends AppCompatActivity {

    String groupID;

    EditText etPollTitle;
    EditText etAddOption;
    EditText etNumber;

    ArrayList<String> optionsText;

    TextView tvPollOptions;

    ImageView ivPlus;
    ImageView ivMultiply;

    Button bPostPoll;
    Button bCancelPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_poll);

        groupID = getIntent().getStringExtra("groupID");

        etPollTitle = findViewById(R.id.etPollTitle);
        etAddOption = findViewById(R.id.etAddOption);
        etNumber = findViewById(R.id.etNumber);

        optionsText = new ArrayList<>();

        tvPollOptions = findViewById(R.id.tvPollOptions);
        tvPollOptions.setText(optionsText.toString());

        ivPlus = findViewById(R.id.ivPlus);
        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAddOption.getText().toString().isEmpty())
                    Toast.makeText(PostPollActivity.this, "Option is empty!", Toast.LENGTH_LONG).show();
                else {
                    optionsText.add(etAddOption.getText().toString());
                    tvPollOptions.setText(optionsText.toString());
                    etAddOption.setText("");
                }
            }
        });
        ivMultiply = findViewById(R.id.ivMultiply);
        ivMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNumber.getText().toString().isEmpty())
                    Toast.makeText(PostPollActivity.this, "No number entered!", Toast.LENGTH_LONG).show();
                else
                    try {
                        optionsText.remove(Integer.parseInt(etNumber.getText().toString()) - 1);
                        tvPollOptions.setText(optionsText.toString());
                        etNumber.setText("");
                    } catch (IndexOutOfBoundsException badNumber) {
                        Toast.makeText(PostPollActivity.this, "There is no option at that position!", Toast.LENGTH_LONG).show();
                    } catch (NumberFormatException notANumber) {
                        Toast.makeText(PostPollActivity.this, "Only use numbers in this field!", Toast.LENGTH_LONG).show();
                    }
            }
        });

        bPostPoll = findViewById(R.id.bPostPoll);
        bPostPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!etPollTitle.getText().toString().isEmpty())
                            if (optionsText.size() > 1) {
                                Date date = new Date();
                                Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), null, null, "Poll: " + etPollTitle.getText().toString(), Long.toString(date.getTime()));
                                saveMessage(message, groupID);
                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childData : dataSnapshot.getChildren())
                                            if (childData.hasChild("pollTitle") && !childData.hasChild("options"))
                                                for (String option : optionsText)
                                                    childData.child("options").child(option).child("text").getRef().setValue(option);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Intent intent = new Intent(PostPollActivity.this, GroupActivity.class);
                                intent.putExtra("groupID", groupID);
                                intent.putExtra("up", true);
                                startActivity(intent);
                                finish();
                            } else if (optionsText.size() == 0)
                                Toast.makeText(PostPollActivity.this, "No options!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(PostPollActivity.this, "Only one option!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(PostPollActivity.this, "A Title is needed!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        bCancelPoll = findViewById(R.id.bCancelPoll);
        bCancelPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostPollActivity.this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                intent.putExtra("up", true);
                startActivity(intent);
                finish();
            }
        });
    }
}
