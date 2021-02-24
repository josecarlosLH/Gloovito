package com.example.gloovito.ui.locales;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gloovito.MainActivity;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.ui.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocalesFragment extends Fragment implements LocalesRecyclerViewAdapter.OnLocalesClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Local> locales;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_locales, container, false);
        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container);
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
        recyclerView = view.findViewById(R.id.reciclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locales = new ArrayList<>();
        cargarLocales();
    }
    public void cargarLocales(){
        FirebaseDatabase.getInstance().getReference("locales").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locales.clear();
                for(DataSnapshot dat : dataSnapshot.getChildren()){
                    locales.add(dat.getValue(Local.class));
                }
                swipeLayout.setRefreshing(false);
                cargarLista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void cargarLista(){
        recyclerView.setAdapter(new LocalesRecyclerViewAdapter(locales,getContext(),this));
    }
    public void onStart(){
        super.onStart();
        ((MainActivity)getActivity()).fab.setVisibility(View.VISIBLE);
    }
    @Override
    public void irProductos(Local l) {
        Bundle b = new Bundle();
        b.putSerializable("local",l);
        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_productosFragment,b);
    }

    @Override
    public void onRefresh() {
        cargarLocales();
    }
}