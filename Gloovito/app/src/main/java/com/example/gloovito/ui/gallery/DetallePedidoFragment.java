package com.example.gloovito.ui.gallery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Pedido;
import com.example.gloovito.ui.carro.LineasRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetallePedidoFragment extends Fragment {
    private RecyclerView recyclerView;
    private Pedido pedido;
    private Button cancelar,reiniciar;
    private TextView idPedido,fechaPedido,estadoPedido;
    public DetallePedidoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_pedido, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idPedido = view.findViewById(R.id.textViewIdPedidoDetalle);
        fechaPedido = view.findViewById(R.id.textViewFechaPedidoDetalle);
        estadoPedido = view.findViewById(R.id.textViewEstadoPedidoDetalle);
        recyclerView = view.findViewById(R.id.recViewDetallePedido);
        cancelar = view.findViewById(R.id.buttonCancelarPedido);
        reiniciar = view.findViewById(R.id.buttonReiniciarPedido);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarPedido();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        pedido = (Pedido) getArguments().get("pedido");
        cancelar.setEnabled(false);
        if(pedido != null){
            cargarInterfaz();
        }
    }
    public void cargarInterfaz(){
        idPedido.setText(pedido.getIdpedido());
        fechaPedido.setText(pedido.getFecha());
        if(pedido.getEstado().equals("Revision")) {
            estadoPedido.setText(R.string.review);
            cancelar.setEnabled(true);
        }
        else if(pedido.getEstado().equals("Cancelado")){
            estadoPedido.setText(R.string.canceled);
        } else if(pedido.getEstado().equals("Completado")){
            estadoPedido.setText(R.string.completed);
        }
        recargarLista();
    }
    public void recargarLista(){
        recyclerView.setAdapter(new DetalleLineasRecyclerViewAdapter( pedido.getLineas(),getContext()));
    }
    public void cancelarPedido(){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pedido.getIdpedido());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Pedido ped= dataSnapshot.getValue(Pedido.class);
                System.out.println("Hace evento");
                if(ped != null){
                    if (ped.getEstado().equals("Revision")){
                        ped.setEstado("Cancelado");
                        FirebaseDatabase.getInstance().getReference("pedidos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pedido.getIdpedido()).setValue(ped);
                        pedido = ped;
                        cargarInterfaz();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
