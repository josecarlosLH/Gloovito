package com.example.gloovito.ui.carro;

import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Linea;
import com.example.gloovito.modelo.Pedido;
import com.example.gloovito.ui.locales.LocalesRecyclerViewAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CarritoFragment extends Fragment implements LineasRecyclerViewAdapter.OnLineasClickListener{
    private RecyclerView recyclerView;
    private Button confirmar;
    private TextView total;
    private SimpleDateFormat sdf;

    public CarritoFragment() {
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
        return inflater.inflate(R.layout.fragment_carrito, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        recyclerView = view.findViewById(R.id.recyclerViewCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        confirmar = view.findViewById(R.id.button_confirmar_carrito);
        total = view.findViewById(R.id.textViewTotalCarrito);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.buy)
                        .setMessage(R.string.confirmbuy)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmarPedido();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();

            }
        });
    }
    public void onStart(){
        super.onStart();
        ((MainActivity)getActivity()).fab.setVisibility(View.INVISIBLE);
        cargarLista();
    }
    public void cargarLista(){
        Double totalNum = 0.0;
        for (Linea l : ((MainActivity) getActivity()).carrito){
            totalNum += l.getSubtotal();
        }
        total.setText(totalNum.toString()+"€");
        recyclerView.setAdapter(new LineasRecyclerViewAdapter( ((MainActivity) getActivity()).carrito,getContext(),this));
    }
    @Override
    public void eliminar(Linea l) {
        for(int i = 0 ; i<((MainActivity) getActivity()).carrito.size();i++){
            if(((MainActivity) getActivity()).carrito.get(i).getNumlinea().equals(l.getNumlinea())){
                ((MainActivity) getActivity()).carrito.remove(i);
            }
        }
        cargarLista();
    }

    @Override
    public void editar(Linea l) {
        Bundle b = new Bundle();
        b.putString("idproducto",l.getProductoid());
        b.putSerializable("lineas",l);
        Navigation.findNavController(getView()).navigate(R.id.action_carritoFragment_to_editarLineaFragment,b);
    }
    public void confirmarPedido(){
        if(!((MainActivity) getActivity()).carrito.isEmpty()){
            Pedido p = new Pedido();
            p.setFecha(sdf.format(new Date()));
            p.setLineas(((MainActivity) getActivity()).carrito);
            double total = 0.0;
            for(Linea l : ((MainActivity) getActivity()).carrito){
                total += l.getSubtotal();
            }
            p.setTotal(total);
            if(total  <= ((MainActivity) getActivity()).user.getCartera() ) {
                String id = FirebaseDatabase.getInstance().getReference("pedidos").child(((MainActivity) getActivity()).user.getId()).push().getKey();
                p.setIdpedido(id);
                p.setEstado("Revision");
                p.setMensajeEstado("");
                p.setIdUsuario(((MainActivity) getActivity()).user.getId());
                FirebaseDatabase.getInstance().getReference("pedidos").child(((MainActivity) getActivity()).user.getId()).child(id).setValue(p);
                ((MainActivity) getActivity()).carrito.clear();
                cargarLista();
                ((MainActivity) getActivity()).user.setCartera(((MainActivity) getActivity()).user.getCartera()-total);
                ((MainActivity) getActivity()).user.setReserva(((MainActivity) getActivity()).user.getReserva()+total);
                FirebaseDatabase.getInstance().getReference("usuarios").child(((MainActivity) getActivity()).user.getId()).setValue(((MainActivity) getActivity()).user);
                Toast.makeText(getContext(), R.string.ordercorrec, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.notenoughtmoney, Toast.LENGTH_SHORT).show();
            }
        }
    }
}