package com.example.gloovito.modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Local implements Serializable {
    String nombre,direccion,idlocal,imagenURL;
    ArrayList<Producto> productos;

    public Local() {
    }

    public Local(String nombre, String direccion, String idlocal, String imagenURL, ArrayList<Producto> productos) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.idlocal = idlocal;
        this.productos = productos;
        this.imagenURL = imagenURL;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdlocal() {
        return idlocal;
    }

    public void setIdlocal(String idlocal) {
        this.idlocal = idlocal;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
}
