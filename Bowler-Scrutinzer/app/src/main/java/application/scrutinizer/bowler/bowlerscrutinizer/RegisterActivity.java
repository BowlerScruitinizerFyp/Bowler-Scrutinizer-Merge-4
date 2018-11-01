package application.scrutinizer.bowler.bowlerscrutinizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtEmailAddress,name;
    private EditText txtPassword,phone;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        phone = (EditText) findViewById(R.id.edit_text_phone);
        name = (EditText) findViewById(R.id.full_name);
        txtEmailAddress = (EditText) findViewById(R.id.txtEmailRegistration);
        txtPassword = (EditText) findViewById(R.id.txtPasswordRegistration);
        firebaseAuth = FirebaseAuth.getInstance();
//        validate();
    }




    public  void validate(){
        if(name.getText().toString().trim().equalsIgnoreCase("")) {
            name.setError("Enter Valid Name");
        }
        if(txtEmailAddress.getText().toString().trim().equalsIgnoreCase("")) {
            txtEmailAddress.setError("Enter valid Email");
        }
        if(txtPassword.getText().toString().trim().equalsIgnoreCase("")) {
            txtPassword.setError("Enter Valid Password");
        }

        if(phone.getText().toString().trim().equalsIgnoreCase("")){
            phone.setError("Enter Valid phone number");
        }

    }



    public void btnRegistrationUser_Click(View v) {

        if(name.getText().toString().trim().equalsIgnoreCase("")) {
            name.setError("Enter Valid Name");
        }
        if(txtEmailAddress.getText().toString().trim().equalsIgnoreCase("")) {
            txtEmailAddress.setError("Enter valid Email");
        }
        if(txtPassword.getText().toString().trim().equalsIgnoreCase("")) {
            txtPassword.setError("Enter Valid Password");
        }

        if(phone.getText().toString().trim().equalsIgnoreCase("")){
            phone.setError("Enter Valid phone number");
        }

        else {

            final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Processing...", true);


            (firebaseAuth.createUserWithEmailAndPassword(txtEmailAddress.getText().toString(), txtPassword.getText().toString()))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();


                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(i);
                            } else {
                                Log.e("ERROR", task.getException().toString());
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }
    public void txt_click(View v){
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
