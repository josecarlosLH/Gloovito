package com.example.gloovito.modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Pedido implements Serializable {
    String idpedido, fecha, estado, mensajeEstado;
    Double total;
    ArrayList<Linea> lineas;

    public Pedido() {
    }

    public Pedido(String idpedido, String fecha, String estado,String mensajeEstado, Double total, ArrayList<Linea> lineas) {
        this.idpedido = idpedido;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.lineas = lineas;
        this.mensajeEstado = mensajeEstado;
    }

    public String getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(String idpedido) {
        this.idpedido = idpedido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public ArrayList<Linea> getLineas() {
        return lineas;
    }

    public void setLineas(ArrayList<Linea> lineas) {
        this.lineas = lineas;
    }

    public String getMensajeEstado() {
        return mensajeEstado;
    }

    public void setMensajeEstado(String mensajeEstado) {
        this.mensajeEstado = mensajeEstado;
    }
}
