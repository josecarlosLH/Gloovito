package com.example.gloovito.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CuentaFragment extends Fragment {
    private TextView dinero, reservado, usuario;
    private EditText ingreso;
    private Button recargar;
    private Usuario user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cuenta, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usuario = view.findViewById(R.id.textViewNombreUsuarioPerfil);
        dinero = view.findViewById(R.id.textViewDineroCartera);
        reservado = view.findViewById(R.id.textViewDineroReservado);
        recargar = view.findViewById(R.id.button_cargar_cartera);
        ingreso = view.findViewById(R.id.editTextNumberDecimal);
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarCartera();
            }
        });
    }
    public void onStart(){
        super.onStart();
        user = ((MainActivity)getActivity()).user;
        ((MainActivity)getActivity()).fab.setVisibility(View.INVISIBLE);
        cargarInterfaz();
    }
    public void cargarInterfaz(){
        if(user != null){
            usuario.setText(user.getNombre());
            dinero.setText(user.getCartera().toString());
            reservado.setText(user.getReserva().toString());
        }
    }
    public void cargarCartera(){
        if(user != null){
            try {
                Double ingresoValor = Double.valueOf(ingreso.getText().toString());
                if (ingresoValor > 0){
                    Usuario user2 = ((MainActivity)getActivity()).user;
                    user2.setCartera(user2.getCartera()+ingresoValor);
                    user = user2;
                    FirebaseDatabase.getInstance().getReference("usuarios").child(user.getId()).setValue(user2);
                    cargarInterfaz();
                    Toast.makeText(getContext(),R.string.enteredcorrect,Toast.LENGTH_SHORT).show();
                }
            }catch(NumberFormatException ex){
                Toast.makeText(getContext(),R.string.errornumber,Toast.LENGTH_SHORT).show();
            }
        }
    }
}