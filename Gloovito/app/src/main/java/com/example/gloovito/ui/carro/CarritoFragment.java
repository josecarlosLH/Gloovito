package com.example.gloovito.ui.carro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Linea;
import com.example.gloovito.ui.locales.LocalesRecyclerViewAdapter;

public class CarritoFragment extends Fragment implements LineasRecyclerViewAdapter.OnLineasClickListener{
    private RecyclerView recyclerView;

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
        recyclerView = view.findViewById(R.id.recyclerViewCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    public void onStart(){
        super.onStart();
        cargarLista();
    }
    public void cargarLista(){
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
        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_productosFragment,b);
    }
}