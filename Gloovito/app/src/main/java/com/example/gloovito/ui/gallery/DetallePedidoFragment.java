package com.example.gloovito.ui.gallery;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Linea;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Pedido;
import com.example.gloovito.modelo.Producto;
import com.example.gloovito.ui.ChargeFragment;
import com.example.gloovito.ui.carro.LineasRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

public class DetallePedidoFragment extends Fragment {
    private RecyclerView recyclerView;
    private Pedido pedido;
    private Button cancelar,reiniciar;
    private TextView idPedido,fechaPedido,estadoPedido,mensajePedido,totalPedido;
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
        totalPedido = view.findViewById(R.id.textViewTotalDetallePedido);
        mensajePedido = view.findViewById(R.id.textViewEstadoPedidoMensaje);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarPedido();
            }
        });
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.restart)
                        .setMessage(R.string.recreate)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reiniciarPedido();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
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
        totalPedido.setText(pedido.getTotal().toString()+"€");
        switch (pedido.getEstado()) {
            case "Revision":
                estadoPedido.setText(R.string.review);
                estadoPedido.setBackgroundColor(getResources().getColor(R.color.design_default_color_on_primary));
                cancelar.setEnabled(true);
                break;
            case "Cancelado":
                estadoPedido.setText(R.string.canceled);
                estadoPedido.setBackgroundColor(getResources().getColor(R.color.design_default_color_error));
                estadoPedido.setTextColor(Color.WHITE);
                break;
            case "Completado":
                estadoPedido.setText(R.string.completed);
                estadoPedido.setBackgroundColor(getResources().getColor(R.color.design_default_color_secondary));
                break;
        }
        if(!pedido.getMensajeEstado().isEmpty())
            mensajePedido.setText(pedido.getMensajeEstado());
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
                        ped.setMensajeEstado("Cancelado por el usuario");
                        FirebaseDatabase.getInstance().getReference("pedidos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pedido.getIdpedido()).setValue(ped);
                        pedido = ped;
                        ((MainActivity) getActivity()).user.setCartera(((MainActivity) getActivity()).user.getCartera()+ped.getTotal());
                        ((MainActivity) getActivity()).user.setReserva(((MainActivity) getActivity()).user.getReserva()-ped.getTotal());
                        FirebaseDatabase.getInstance().getReference("usuarios").child(((MainActivity) getActivity()).user.getId()).setValue(((MainActivity) getActivity()).user);
                        cargarInterfaz();
                    }
                } else {
                    Toast.makeText(getContext(),R.string.ordernoexist,Toast.LENGTH_SHORT);
                    ((MainActivity)getActivity()).onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void reiniciarPedido(){
        final AlertDialog alert = new AlertDialog.Builder(getContext()).create();
        GifImageView imagen = new GifImageView(getContext());
        imagen.setImageResource(R.drawable.cargando);
        alert.setView(imagen);
        alert.show();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locales");
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getActivity() != null) {
                    ((MainActivity) getActivity()).carrito.clear();
                    HashMap<String, Local> locales = new HashMap<>();
                    for (DataSnapshot localsnap : dataSnapshot.getChildren()) {
                        Local loc = localsnap.getValue(Local.class);
                        locales.put(localsnap.getKey(), loc);
                    }
                    for (Linea linea : pedido.getLineas()) {
                        Producto p = null;
                        Local local1 = locales.get(linea.getLocalid());
                        ArrayList<Producto> productos = null;
                        if (local1 != null) {
                            productos = local1.getProductos();
                        }
                        if (productos != null)
                            for (Producto prod : productos) {
                                if (prod.getIdproducto().equals(linea.getProductoid())) {
                                    p = prod;
                                }
                            }
                        if (p != null) {
                            if (linea.getCantidad() <= p.getStock()) {
                                linea.setPrecio(p.getPrecio());
                                linea.setSubtotal(linea.getPrecio() * linea.getCantidad());
                                ((MainActivity) getActivity()).carrito.add(linea);
                            }
                        }
                    }
                    alert.dismiss();
                    Navigation.findNavController(getView()).navigate(R.id.action_global_carritoFragment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                alert.dismiss();
            }
        };
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ref.removeEventListener(listener);
                if(getActivity() != null) {
                    ((MainActivity) getActivity()).carrito.clear();
                    Toast.makeText(getContext(), R.string.canceled, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ref.addListenerForSingleValueEvent(listener);
    }
}
