package com.example.gloovito.ui.locales;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Linea;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductosFragment extends Fragment implements ProductosRecyclerViewAdapter.OnProductosClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private Local l;
    private ImageView imagenLocal;
    private TextView nombre,direccion;
    private ArrayList<Producto>lista;
    private SwipeRefreshLayout swipeLayout;
    public ProductosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_productos, container, false);
        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container_productos);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.reciclerViewProductos);
        lista = new ArrayList<>();
        Bundle b = this.getArguments();
        if(b != null){
            l = (Local) b.get("local");
        }
        if(l != null) {
            imagenLocal = view.findViewById(R.id.imageViewProductosLocal);
            nombre = view.findViewById(R.id.textViewNombreLocalProductos);
            direccion = view.findViewById(R.id.textViewDireccionLocalProductos);
            if(!l.getImagenURL().equals("default")) {
                Glide.with(getContext())
                        .load(l.getImagenURL())
                        .circleCrop()
                        .into(imagenLocal);
            } else {
                Glide.with(getContext())
                        .load(R.drawable.defaultimage)
                        .circleCrop()
                        .into(imagenLocal);
            }
            nombre.setText(l.getNombre());
            direccion.setText(l.getDireccion());
            cargarProductos();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            Toast.makeText(getContext(),R.string.errorlocal, Toast.LENGTH_SHORT).show();
        }
    }
    public void cargarProductos(){
        FirebaseDatabase.getInstance().getReference("locales").child(l.getIdlocal()).child("productos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista.clear();
                for (DataSnapshot prod : snapshot.getChildren()) {
                    Producto p = prod.getValue(Producto.class);
                    if (p != null)
                        lista.add(p);
                }
                cargaLista();
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void cargaLista(){
        recyclerView.setAdapter(new ProductosRecyclerViewAdapter(lista,getContext(),this));
    }

    @Override
    public void anadir(Producto p,String cantidad) {
        try {
            int cantidadAnterior = 0;
            Linea linea = new Linea();
            //Obtiene la ultima linea
            if(((MainActivity) getActivity()).carrito.isEmpty()){
                linea.setNumlinea("1");
            } else
                linea.setNumlinea("" + (Integer.parseInt(((MainActivity) getActivity()).carrito.get(((MainActivity) getActivity()).carrito.size()-1).getNumlinea())+1));
            for (Linea li : ((MainActivity) getActivity()).carrito){
                if(li.getProductoid().equals(p.getIdproducto()) && li.getLocalid().equals(l.getIdlocal())){
                    cantidadAnterior += li.getCantidad();
                }
            }
            int cantidadComprar = Integer.parseInt(cantidad);
            cantidadAnterior += cantidadComprar;
            //Comprueba que no se estan añadiendo mas productos que los que hay en stock
            if(cantidadAnterior <= p.getStock()) {
                linea.setCantidad(cantidadComprar);
                linea.setLocal(l.getNombre());
                linea.setLocalid(l.getIdlocal());
                linea.setProductoid(p.getIdproducto());
                linea.setProducto(p.getNombre());
                linea.setPrecio(p.getPrecio());
                linea.setSubtotal(p.getPrecio()*cantidadComprar);
                ((MainActivity) getActivity()).carrito.add(linea);
                Toast.makeText(getContext(),R.string.addedcorrect, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),R.string.nostock, Toast.LENGTH_SHORT).show();
            }
        }catch(NumberFormatException ex){
            Toast.makeText(getContext(),R.string.errornumber, Toast.LENGTH_SHORT).show();
        }
    }
    public void onStart(){
        super.onStart();
        ((MainActivity)getActivity()).fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        cargarProductos();
    }
}