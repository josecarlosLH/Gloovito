package com.example.gloovito.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Pedido;

import java.util.ArrayList;

public class PedidosFragment extends Fragment implements PedidosRecyclerViewAdapter.OnPedidosClickListener{
    public ArrayList<Pedido> pedidos;
    public RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pedidos, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void recargarLista(){
        recyclerView.setAdapter(new PedidosRecyclerViewAdapter(pedidos,getContext(),this));
    }
    public void onStart(){
        super.onStart();
        pedidos = new ArrayList<>(((MainActivity)getActivity()).pedidos);
        recargarLista();
    }
    @Override
    public void irPedido(Pedido p) {
        Bundle b = new Bundle();
        b.putSerializable("pedido",p);
        Navigation.findNavController(getView()).navigate(R.id.action_nav_gallery_to_detallePedidoFragment,b);
    }
}