package com.example.gloovito.ui.locales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Producto;

import java.util.List;

public class ProductosRecyclerViewAdapter extends RecyclerView.Adapter<ProductosRecyclerViewAdapter.ViewHolder> {

    private final List<Producto> mValues;
    private final Context context;
    private final OnProductosClickListener listener;

    public ProductosRecyclerViewAdapter(List<Producto> items, Context contexto, OnProductosClickListener listener2) {
        mValues = items;
        context = contexto;
        listener = listener2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.producto_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues.get(position).getImagenURL().equals("default")) {
            Glide.with(context)
                    .load(R.drawable.iconcomida)
                    .circleCrop()
                    .into(holder.mImagen);
        } else {
            Glide.with(context)
                    .load(mValues.get(position).getImagenURL())
                    .circleCrop()
                    .into(holder.mImagen);
        }
        holder.nombre.setText(mValues.get(position).getNombre());
        holder.precio.setText(mValues.get(position).getPrecio().toString());
        holder.cantidad.setText(mValues.get(position).getStock()+"/");
        holder.producto = mValues.get(position);
    }
    public interface OnProductosClickListener {
        public void anadir(Producto p,String cantidad);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImagen;
        public final TextView nombre,precio,cantidad;
        public final EditText cantidadComprar;
        public final Button anadir;
        public Producto producto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImagen = (ImageView) view.findViewById(R.id.imageViewImagenProducto);
            nombre = (TextView) view.findViewById(R.id.textViewNombreProducto);
            precio = (TextView) view.findViewById(R.id.textViewProductoPrecio);
            cantidad = (TextView) view.findViewById(R.id.textViewProductoCantidad);
            anadir = view.findViewById(R.id.button_anadir_producto);
            cantidadComprar = view.findViewById(R.id.editTextNumberCantidad);
            anadir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.anadir(producto,cantidadComprar.getText().toString());
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombre.getText() + "'";
        }
    }
}