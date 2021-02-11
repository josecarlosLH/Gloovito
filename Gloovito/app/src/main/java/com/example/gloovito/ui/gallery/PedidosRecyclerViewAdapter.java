package com.example.gloovito.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Pedido;

import java.util.List;

public class PedidosRecyclerViewAdapter extends RecyclerView.Adapter<PedidosRecyclerViewAdapter.ViewHolder> {

    private final List<Pedido> mValues;
    private final Context context;
    private final OnPedidosClickListener listener;

    public PedidosRecyclerViewAdapter(List<Pedido> items, Context contexto, OnPedidosClickListener listener2) {
        mValues = items;
        context = contexto;
        listener = listener2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.local_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.fecha.setText(mValues.get(position).getFecha());
        holder.estado.setText(mValues.get(position).getEstado());
        holder.id.setText(mValues.get(position).getIdpedido());
        holder.pedido = mValues.get(position);
    }
    public interface OnPedidosClickListener {
        public void irPedido(Pedido p);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView fecha,estado,id;
        public final Button detalle;
        public Pedido pedido;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            fecha = (TextView) view.findViewById(R.id.textViewFechaPedido);
            estado = view.findViewById(R.id.textViewEstadoPedido);
            id = view.findViewById(R.id.textViewIdPedido);
            detalle = view.findViewById(R.id.button_detalle_pedido);
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.irPedido(pedido);
                }
            });
        }
    }
}