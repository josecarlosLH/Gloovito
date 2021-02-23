package com.example.gloovito.ui.slideshow;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Movimiento;
import com.example.gloovito.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CuentaFragment extends Fragment implements CuentaRecyclerViewAdapter.OnMovimientoClickListener {
    private TextView dinero, reservado,correoCuenta;
    private EditText ingreso, usuario;
    private Button recargar,actualizar;
    private Usuario user;
    private RecyclerView recvMov;
    private ArrayList<Movimiento> movimientos;
    private DatabaseReference refMov;
    private ValueEventListener listenerRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cuenta, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usuario = view.findViewById(R.id.editTextTextNombreUsuarioDetalleUsuario);
        dinero = view.findViewById(R.id.textViewDineroCartera);
        reservado = view.findViewById(R.id.textViewDineroReservado);
        recargar = view.findViewById(R.id.button_cargar_cartera);
        ingreso = view.findViewById(R.id.editTextNumberDecimal);
        actualizar = view.findViewById(R.id.button_actualizar_nombre);
        correoCuenta = view.findViewById(R.id.textViewCuentaCorreoElectronico);
        movimientos = new ArrayList<>();
        recvMov = view.findViewById(R.id.recviewMovimientos);
        recvMov.setLayoutManager(new LinearLayoutManager(getContext()));
        listenerRef= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimientos.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    movimientos.add(child.getValue(Movimiento.class));
                }
                Collections.reverse(movimientos);
                cargarInterfaz();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarNombre();
            }
        });
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.movementconfirm)
                        .setMessage(R.string.confirmcharge)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cargarCartera();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        user = ((MainActivity)getActivity()).user;
        ((MainActivity)getActivity()).fab.setVisibility(View.INVISIBLE);
        refMov = FirebaseDatabase.getInstance().getReference("movimientos").child(user.getId());
        refMov.addValueEventListener(listenerRef);
        cargarUsuario();
    }
    @Override
    public void onStop() {
        super.onStop();
        refMov.removeEventListener(listenerRef);
    }
    public void cargarInterfaz(){
        recvMov.setAdapter(new CuentaRecyclerViewAdapter(movimientos,getContext(),this));
    }
    public void cargarUsuario(){
        user = ((MainActivity)getActivity()).user;
        if(user != null) {
            usuario.setText(user.getNombre());
            dinero.setText(user.getCartera().toString());
            correoCuenta.setText(user.getMail());
            reservado.setText(user.getReserva().toString());
        }
    }
    public void cargarCartera(){
        if(user != null){
            try {
                Movimiento mov = new Movimiento();
                Double ingresoValor = Double.valueOf(ingreso.getText().toString());
                if (ingresoValor > 0){
                    mov.setClienteId(user.getId());
                    mov.setDinero(ingresoValor);
                    mov.setEstado("Revision");
                    mov.setMovimientoId(FirebaseDatabase.getInstance().getReference("movimientos").child(user.getId()).push().getKey());
                    FirebaseDatabase.getInstance().getReference("movimientos").child(user.getId()).child(mov.getMovimientoId()).setValue(mov);
                    cargarInterfaz();
                    Toast.makeText(getContext(),R.string.enteredcorrect,Toast.LENGTH_SHORT).show();
                }
            }catch(NumberFormatException ex){
                Toast.makeText(getContext(),R.string.errornumber,Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void actualizarNombre(){
        if(usuario.getText().toString().isEmpty()){
            Toast.makeText(getContext(),R.string.emptyuser,Toast.LENGTH_SHORT).show();
        } else {
            Usuario user2 = ((MainActivity)getActivity()).user;
            user2.setNombre(usuario.getText().toString());
            FirebaseDatabase.getInstance().getReference("usuarios").child(user.getId()).setValue(user2);
            cargarInterfaz();
            Toast.makeText(getContext(),R.string.updatedcorrect, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void cancelar(Movimiento m) {
        System.out.println("Entra");
        m.setEstado("Cancelado");
        FirebaseDatabase.getInstance().getReference("movimientos").child(user.getId()).child(m.getMovimientoId()).setValue(m);
    }
}