package softagi.firebase6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity
{
    EditText email_field,password_field;
    String email,password;

    FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
    }

    private void initViews()
    {
        email_field = findViewById(R.id.email_field);
        password_field = findViewById(R.id.password_field);

        auth = FirebaseAuth.getInstance();
    }

    public void signin(View view)
    {
        email = email_field.getText().toString();
        password = password_field.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "enter email", Toast.LENGTH_SHORT).show();
            email_field.requestFocus();
            return;
        }

        if (password.length() < 6)
        {
            Toast.makeText(getApplicationContext(), "password is too short", Toast.LENGTH_SHORT).show();
            password_field.requestFocus();
            return;
        }

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        login(email,password);
    }

    private void login(String email, String password)
    {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            startActivity(intent);
                            finish();
                        } else
                        {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
