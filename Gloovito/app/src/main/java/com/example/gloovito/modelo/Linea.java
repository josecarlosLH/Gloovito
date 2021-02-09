package com.example.gloovito.modelo;

public class Linea {
    String numlinea,local,producto;
    int cantidad;
    Double precio,subtotal;

    public Linea() {
    }

    public Linea(String numlinea, String local, String producto, int cantidad, Double precio, Double subtotal) {
        this.numlinea = numlinea;
        this.local = local;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
    }

    public String getNumlinea() {
        return numlinea;
    }

    public void setNumlinea(String numlinea) {
        this.numlinea = numlinea;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
