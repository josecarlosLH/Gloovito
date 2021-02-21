package com.example.gloovito.ui.slideshow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Local;
import com.example.gloovito.modelo.Movimiento;

import java.util.List;

public class CuentaRecyclerViewAdapter extends RecyclerView.Adapter<CuentaRecyclerViewAdapter.ViewHolder> {

    private final List<Movimiento> mValues;
    private final Context context;

    public CuentaRecyclerViewAdapter(List<Movimiento> items, Context contexto) {
        mValues = items;
        context = contexto;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movimiento_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.idMov.setText(mValues.get(position).getMovimientoId());
        holder.valor.setText(String.valueOf(mValues.get(position).getDinero()));
        holder.estado.setText(mValues.get(position).getEstado());
        switch (mValues.get(position).getEstado()) {
            case "Cancelado":
                holder.mView.setBackgroundColor(context.getResources().getColor(R.color.design_default_color_error));
                break;
            case "Completado":
                holder.mView.setBackground(context.getResources().getDrawable(R.drawable.background_boton_login));
                break;
        }
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView idMov,valor,estado;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            idMov = (TextView) view.findViewById(R.id.textViewIdMovimiento);
            valor = (TextView) view.findViewById(R.id.textViewValorMovimiento);
            estado = (TextView) view.findViewById(R.id.textViewEstadoMovimiento);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + idMov.getText() + "'";
        }
    }
}