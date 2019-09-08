package softagi.firebase6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{
    CircleImageView circleImageView;
    EditText email_field,username_field,password_field,confirm_password_field,mobile_field,address_field;
    String email,username,password,c_password,mobile,address;
    Uri photopath;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews()
    {
        circleImageView = findViewById(R.id.profile_picture);
        email_field = findViewById(R.id.email_field);
        username_field = findViewById(R.id.username_field);
        password_field = findViewById(R.id.password_field);
        confirm_password_field = findViewById(R.id.confirm_password_field);
        mobile_field = findViewById(R.id.mobile_field);
        address_field = findViewById(R.id.address_field);

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null)
        {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);
            finish();
        }

        circleImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1,1)
                        .start(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            photopath = result.getUri();

            Picasso.get()
                    .load(photopath)
                    .into(circleImageView);
        }
    }

    public void already(View view)
    {
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }

    public void register(View view)
    {
        email = email_field.getText().toString();
        username = username_field.getText().toString();
        password = password_field.getText().toString();
        c_password = confirm_password_field.getText().toString();
        mobile = mobile_field.getText().toString();
        address = address_field.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "enter email", Toast.LENGTH_SHORT).show();
            email_field.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(getApplicationContext(), "enter username", Toast.LENGTH_SHORT).show();
            username_field.requestFocus();
            return;
        }

        if (password.length() < 6)
        {
            Toast.makeText(getApplicationContext(), "password is too short", Toast.LENGTH_SHORT).show();
            password_field.requestFocus();
            return;
        }

        if (!c_password.equals(password))
        {
            Toast.makeText(getApplicationContext(), "password is not matching", Toast.LENGTH_SHORT).show();
            confirm_password_field.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile))
        {
            Toast.makeText(getApplicationContext(), "enter mobile number", Toast.LENGTH_SHORT).show();
            mobile_field.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address))
        {
            Toast.makeText(getApplicationContext(), "enter your address", Toast.LENGTH_SHORT).show();
            address_field.requestFocus();
            return;
        }

        if (photopath == null)
        {
            Toast.makeText(getApplicationContext(), "select photo", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        createUser(email,password,username,mobile,address);
    }

    private void createUser(final String email, String password, final String username, final String mobile, final String address)
    {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            String uId = task.getResult().getUser().getUid();
                            uploadImage(email,username,mobile,address,uId);
                        } else
                            {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                    }
                });
    }

    private void uploadImage(final String email, final String username, final String mobile, final String address, final String uId)
    {
        UploadTask uploadTask;
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + photopath.getLastPathSegment());
        uploadTask = storageReference.putFile(photopath);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if (task.isSuccessful())
                {
                    Uri image_uri = task.getResult();
                    String image_url = image_uri.toString();

                    addUser(email,username,mobile,address,uId,image_url);
                }
            }
        });
    }

    private void addUser(String email, String username, String mobile, String address, String id,String photo)
    {
        UserModel userModel = new UserModel(email,username,mobile,address,photo,id);
        
        databaseReference.child("Users").child(id).setValue(userModel);

        progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
    }
}