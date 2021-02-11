package com.example.gloovito.ui.carro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Linea;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditarLineaFragment extends Fragment {
    private Linea l;
    private Producto prod;
    private String idProducto;
    private TextView nombre,cantidad, precioArticulo;
    private Button boton;
    private EditText cantidadElegida;
    public EditarLineaFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_linea, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nombre = view.findViewById(R.id.textViewNombreProductoLineaEditar);
        cantidad = view.findViewById(R.id.textViewCantidadEditarLinea);
        cantidadElegida = view.findViewById(R.id.editTextTextPersonName);
        precioArticulo = view.findViewById(R.id.textViewPrecioEditarLinea);
        boton = view.findViewById(R.id.button_confirmar_editar);
        Bundle b = this.getArguments();
        if(b != null) {
            l = (Linea) b.get("lineas");
            idProducto = (String) b.get("idproducto");
        }
        FirebaseDatabase.getInstance().getReference("locales").child(l.getLocalid()).child("productos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Producto p = data.getValue(Producto.class);
                    if(p.getIdproducto().equals(idProducto)){
                        prod = p;
                    }
                }
                cargarInterfaz();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void cargarInterfaz(){
        nombre.setText(prod.getNombre());
        cantidad.setText(prod.getStock()+"/");
        precioArticulo.setText(prod.getPrecio()+" €");
        cantidadElegida.setText(String.valueOf(l.getCantidad()));
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int cantidaActualizar = Integer.parseInt(cantidadElegida.getText().toString());
                    if(cantidaActualizar < prod.getStock() && cantidaActualizar > 0) {
                        l.setCantidad(cantidaActualizar);
                        l.setSubtotal(cantidaActualizar * l.getPrecio());
                        for (int i=0; i<((MainActivity) getActivity()).carrito.size();i++){
                            if(((MainActivity) getActivity()).carrito.get(i).getNumlinea().equals(l.getNumlinea())){
                                ((MainActivity) getActivity()).carrito.set(i,l);
                            }
                        }
                    }
                }catch(NumberFormatException ex){
                    Toast.makeText(getContext(),R.string.errornumber,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onStart(){
        super.onStart();
        ((MainActivity)getActivity()).fab.setVisibility(View.INVISIBLE);
    }
}