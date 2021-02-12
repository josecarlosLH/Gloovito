package com.example.gloovito;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.gloovito.modelo.Linea;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Pedido;
import com.example.gloovito.modelo.Producto;
import com.example.gloovito.modelo.Usuario;
import com.example.gloovito.ui.gallery.PedidosFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Usuario user;
    public DrawerLayout drawer;
    public ArrayList<Linea> carrito;
    public ArrayList<Pedido> pedidos;
    public FloatingActionButton fab;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        carrito = new ArrayList<>();
        pedidos = new ArrayList<>();
        fab = findViewById(R.id.fab);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_global_carritoFragment);
            }
        });
        /*ArrayList<Producto> productos= new ArrayList<>();
        productos.add(new Producto("0","Bebida azucarada","Cocacola",1.00,5));
        productos.add(new Producto("1","Cerveza lata 50cl","Alhambra",0.50,60));
        String id = FirebaseDatabase.getInstance().getReference("locales").push().getKey();
        Local local = new Local("Panaderia pepe","Calle sol. Granada",id,productos);
        FirebaseDatabase.getInstance().getReference("locales").child(id).setValue(local);*/
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
        final View view = this.getCurrentFocus();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("pedidos").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Pedido pedido = snapshot.getValue(Pedido.class);
                if(pedido != null){
                    pedidos.add(pedido);
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
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}