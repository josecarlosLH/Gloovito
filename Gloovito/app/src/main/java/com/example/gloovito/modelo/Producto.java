package com.example.gloovito.modelo;

public class Producto {
    String idproducto, descipcion, nombre;
    Double precio;
    int stock;

    public Producto() {
    }

    public Producto(String idproducto, String descipcion, String nombre, Double precio, int stock) {
        this.idproducto = idproducto;
        this.descipcion = descipcion;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public String getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(String idproducto) {
        this.idproducto = idproducto;
    }

    public String getDescipcion() {
        return descipcion;
    }

    public void setDescipcion(String descipcion) {
        this.descipcion = descipcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
