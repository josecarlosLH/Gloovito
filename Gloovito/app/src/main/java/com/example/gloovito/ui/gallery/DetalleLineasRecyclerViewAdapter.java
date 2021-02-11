package com.example.gloovito.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gloovito.R;
import com.example.gloovito.modelo.Linea;

import java.util.List;

public class DetalleLineasRecyclerViewAdapter extends RecyclerView.Adapter<DetalleLineasRecyclerViewAdapter.ViewHolder> {

    private final List<Linea> mValues;
    private final Context context;

    public DetalleLineasRecyclerViewAdapter(List<Linea> items, Context contexto) {
        mValues = items;
        context = contexto;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nombreLocal.setText(mValues.get(position).getLocal());
        holder.nombreProducto.setText(mValues.get(position).getProducto());
        holder.precioycantidad.setText(mValues.get(position).getPrecio()+"€ X "+mValues.get(position).getCantidad() );
        holder.subtotal.setText(mValues.get(position).getSubtotal().toString());
        holder.numLinea.setText(mValues.get(position).getNumlinea());
        holder.linea = mValues.get(position);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nombreLocal,nombreProducto,precioycantidad,subtotal,numLinea;
        public Linea linea;
        public Button boton,edit;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nombreLocal = (TextView) view.findViewById(R.id.textViewNombreLocalLinea);
            nombreProducto = (TextView) view.findViewById(R.id.textViewNombreProductoLinea);
            precioycantidad = (TextView) view.findViewById(R.id.textViewPrecioYCantidadProductoLinea);
            subtotal = (TextView) view.findViewById(R.id.textViewSubtotalLinea);
            numLinea = (TextView) view.findViewById(R.id.textViewNumLinea);
            boton = view.findViewById(R.id.button_eliminar_producto);
            edit = view.findViewById(R.id.button_editar_linea);
            boton.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
        }

    }
}