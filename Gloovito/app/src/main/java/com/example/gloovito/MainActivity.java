package com.example.gloovito;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.gloovito.modelo.Linea;
import com.example.gloovito.modelo.Pedido;
import com.example.gloovito.modelo.Usuario;
import com.example.gloovito.ui.gallery.PedidosFragment;
import com.example.gloovito.ui.locales.LocalesFragment;
import com.example.gloovito.ui.slideshow.CuentaFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public Usuario user;
    public DrawerLayout drawer;
    public ArrayList<Linea> carrito;
    public ArrayList<Pedido> pedidos;
    public FloatingActionButton fab;
    public boolean logueado;
    public DatabaseReference pedidosDB,usuario;
    public ValueEventListener usuarioListener;
    public ChildEventListener pedidosListener;
    private AppBarConfiguration mAppBarConfiguration;
    public NavController navController ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        logueado = false;
        pedidosDB = FirebaseDatabase.getInstance().getReference("pedidos");
        pedidosListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("add");
                Pedido pedido = snapshot.getValue(Pedido.class);
                if(pedido != null){
                    pedidos.add(pedido);
                }

                Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(navHostFragment != null) {
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(navHostFragment.getChildFragmentManager().getFragments().size()-1);
                    if(fragment != null)
                        if(fragment instanceof PedidosFragment) {
                            ((PedidosFragment) fragment).pedidos = new ArrayList<Pedido>(pedidos);
                            Collections.reverse(((PedidosFragment) fragment).pedidos);
                            ((PedidosFragment) fragment).recargarLista();
                        }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("hace modificar");
                Pedido pedido = snapshot.getValue(Pedido.class);
                boolean cancelado = false;
                boolean completado = false;
                for(int i=0 ; i<pedidos.size() ; i++) {
                    if(pedidos.get(i).getIdpedido().equals(pedido.getIdpedido())){
                        pedidos.set(i,pedido);
                        if(pedido.getEstado().equals("Cancelado"))
                            cancelado = true;
                        else if(pedido.getEstado().equals("Completado"))
                            completado = true;
                    }
                }
                if(cancelado & completado){
                    Toast.makeText(getApplicationContext(),R.string.orderchange,Toast.LENGTH_SHORT).show();
                } else if(cancelado){
                    Toast.makeText(getApplicationContext(),R.string.ordercanceled,Toast.LENGTH_SHORT).show();
                } else if(completado){
                    Toast.makeText(getApplicationContext(),R.string.ordercomplete,Toast.LENGTH_SHORT).show();
                }
                Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(navHostFragment != null) {
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(navHostFragment.getChildFragmentManager().getFragments().size()-1);
                    if(fragment != null)
                        if(fragment instanceof PedidosFragment) {
                            ((PedidosFragment) fragment).pedidos = new ArrayList<Pedido>(pedidos);
                            Collections.reverse(((PedidosFragment) fragment).pedidos);
                            ((PedidosFragment) fragment).recargarLista();
                        }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Pedido pedido = snapshot.getValue(Pedido.class);
                for(int i = 0; i<pedidos.size(); i++){
                    if(pedidos.get(i).getIdpedido().equals(pedido.getIdpedido())){
                        pedidos.remove(i);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usuario = FirebaseDatabase.getInstance().getReference("usuarios");
        usuarioListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Usuario.class);
                Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(navHostFragment != null) {
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(navHostFragment.getChildFragmentManager().getFragments().size()-1);
                    if(fragment != null)
                        if(fragment instanceof CuentaFragment) {
                            ((CuentaFragment) fragment).cargarUsuario();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_global_carritoFragment);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void login(){
        carrito = new ArrayList<>();
        pedidos = new ArrayList<>();
        logueado = true;
    }
    public void onPause() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            usuario.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(usuarioListener);
            pedidosDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(pedidosListener);
        }
        super.onPause();
    }
    public void onResume() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            pedidosDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("idPedido").addChildEventListener(pedidosListener);
            usuario.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(usuarioListener);
        }
        super.onResume();
    }
    public void onBackPressed(){
        if(navController.getPreviousBackStackEntry() != null && navController.getPreviousBackStackEntry().getDestination().getLabel().equals(getString(R.string.login))){
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.logout)
                    .setMessage(R.string.confirmlogout)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                                usuario.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(usuarioListener);
                                pedidosDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(pedidosListener);
                            }
                            MainActivity.super.onBackPressed();
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else
            super.onBackPressed();
    }

}