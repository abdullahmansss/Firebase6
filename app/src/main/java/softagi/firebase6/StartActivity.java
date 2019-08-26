package softagi.firebase6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import softagi.firebase6.Models.UserModel;

public class StartActivity extends AppCompatActivity
{
    TextView email_txt,username_txt,mobile_txt,address_txt;
    String email,username,mobile,address,id;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initViews();
        getData(id);
    }

    private void getData(String id)
    {
        databaseReference.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                email = userModel.getEmail();
                username = userModel.getUsername();
                mobile = userModel.getMobile();
                address = userModel.getAddress();

                email_txt.setText(email);
                username_txt.setText(username);
                mobile_txt.setText(mobile);
                address_txt.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void initViews()
    {
        email_txt = findViewById(R.id.email_txt);
        username_txt = findViewById(R.id.username_txt);
        mobile_txt = findViewById(R.id.mobile_txt);
        address_txt = findViewById(R.id.address_txt);

        auth  = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        id = auth.getUid();
    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
    }

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void delete(View view)
    {
        databaseReference.child("Users").child(id).removeValue();

    }
}
