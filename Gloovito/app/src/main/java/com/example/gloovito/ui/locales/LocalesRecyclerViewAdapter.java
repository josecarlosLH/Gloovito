package com.example.gloovito.ui.locales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gloovito.R;
import com.example.gloovito.modelo.Local;

import java.util.List;

public class LocalesRecyclerViewAdapter extends RecyclerView.Adapter<LocalesRecyclerViewAdapter.ViewHolder> {

    private final List<Local> mValues;
    private final Context context;
    private final OnLocalesClickListener listener;

    public LocalesRecyclerViewAdapter(List<Local> items, Context contexto, OnLocalesClickListener listener2) {
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
        if (mValues.get(position).getImagenURL().equals("default")) {
            holder.mImagen.setImageResource(R.drawable.defaultimage);
        } else {
            Glide.with(context)
                    .load(mValues.get(position).getImagenURL())
                    .into(holder.mImagen);
        }
        holder.nombre.setText(mValues.get(position).getNombre());
        holder.local = mValues.get(position);
        holder.direccion.setText(mValues.get(position).getDireccion());
    }
    public interface OnLocalesClickListener {
        public void irProductos(Local l);
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImagen;
        public final TextView nombre,direccion;
        public Local local;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImagen = (ImageView) view.findViewById(R.id.imageViewImagenLocal);
            nombre = (TextView) view.findViewById(R.id.textViewNombreLocal);
            direccion = view.findViewById(R.id.textViewDireccion);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.irProductos(local);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombre.getText() + "'";
        }
    }
}