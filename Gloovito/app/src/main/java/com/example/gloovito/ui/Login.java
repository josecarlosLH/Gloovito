package com.example.gloovito.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Login extends Fragment {
    private GoogleSignInClient mGoogleSignInClient;
    private Button b_reg,b_login;
    private SignInButton b_google;
    private EditText pass,mail;

    public Login() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this.requireContext(), gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 3);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 3) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Usuario user = new Usuario();
                            user.setId(task.getResult().getUser().getUid());
                            if(task.getResult().getUser().getDisplayName() != null)
                                user.setNombre(task.getResult().getUser().getDisplayName());
                            else
                                user.setNombre(task.getResult().getUser().getEmail().substring(0,task.getResult().getUser().getEmail().indexOf("@")));
                            user.setMail(task.getResult().getUser().getEmail());
                            user.setCartera(0.0);
                            user.setReserva(0.0);
                            anadirUsuario(user);
                            ((MainActivity)getActivity()).login();
                            Intent i = new Intent(getContext(), SplashScreenActivity.class);
                            startActivity(i);
                            Navigation.findNavController(getView()).navigate(R.id.action_login_to_nav_home);
                        } else {
                            Snackbar.make(getView(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity)requireActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) requireActivity()).drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() != null) {
            b_reg = this.getActivity().findViewById(R.id.b_reg);
            b_login = this.getActivity().findViewById(R.id.b_login);
            b_google = this.getActivity().findViewById(R.id.sign_in_button);
            b_google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
            mail = this.getActivity().findViewById(R.id.editTextTextEmailAddress);
            pass = this.getActivity().findViewById(R.id.editTextTextPassword);
            b_reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mail.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                mail.getText().toString(),
                                pass.getText().toString()
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Usuario user = new Usuario();
                                    user.setId(task.getResult().getUser().getUid());
                                    if (task.getResult().getUser().getDisplayName() != null)
                                        user.setNombre(task.getResult().getUser().getDisplayName());
                                    else
                                        user.setNombre(task.getResult().getUser().getEmail().substring(0, task.getResult().getUser().getEmail().indexOf("@")));
                                    user.setMail(task.getResult().getUser().getEmail());
                                    user.setCartera(0.0);
                                    user.setReserva(0.0);
                                    anadirUsuario(user);
                                    Toast.makeText(getContext(), R.string.registercorrect, Toast.LENGTH_SHORT).show();
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getContext(), R.string.accountexisterror, Toast.LENGTH_SHORT).show();
                                } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(getContext(), R.string.weakpass, Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }

                }
            });
            b_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mail.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                                mail.getText().toString(),
                                pass.getText().toString()
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    ((MainActivity) getActivity()).login();
                                    Toast.makeText(getContext(), R.string.logincorrect, Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getContext(), SplashScreenActivity.class);
                                    startActivity(i);
                                    Navigation.findNavController(getView()).navigate(R.id.action_login_to_nav_home);
                                } else {
                                    Toast.makeText(getContext(), R.string.errorlogin, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            ((MainActivity) getActivity()).fab.setVisibility(View.INVISIBLE);
        } else {
            this.onViewCreated(view,savedInstanceState);
        }
    }
    public void anadirUsuario(final Usuario user){

        FirebaseDatabase.getInstance().getReference("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean existe = false;
                for (DataSnapshot a : dataSnapshot.getChildren()){
                    if(a.getKey().equals(user.getId())){
                        existe = true;
                    }
                }
                if(!existe)
                    FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
}