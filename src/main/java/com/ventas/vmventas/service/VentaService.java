package com.ventas.vmventas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ventas.vmventas.DTO.ProductoDTO;
import com.ventas.vmventas.model.Venta;
import com.ventas.vmventas.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class VentaService {
    @Autowired
    public VentaRepository ventaRepository;

    @Autowired
    private ProductoServiceClient productoServiceClient;

    public List<Venta> getAllVentas(){
        return ventaRepository.findAll();
    }

    public Venta findById(Integer id){
        return ventaRepository.findById(id).get();
    }

    // public Venta save(Venta venta){
    //     return ventaRepository.save(venta);
    // }

    public Venta save(Venta venta) {
        // Paso 1: Obtener producto desde el otro microservicio
        ProductoDTO producto = productoServiceClient.obtenerProductoPorId(venta.getProductoId());
        if (producto == null) {
            throw new RuntimeException("El producto no existe");
        }
        // Paso 2: Validar stock
        if (producto.getStock() < venta.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }
        // Paso 3: Descontar stock
        producto.setStock(producto.getStock() - venta.getCantidad());
        // Paso 4: Actualizar producto
        productoServiceClient.actualizarProducto(producto.getIdProducto(), producto);
        // Paso 5: Calcular total y guardar la venta
        venta.setTotal(producto.getPrecio() * venta.getCantidad());

        return ventaRepository.save(venta);
    }

    public void delete(Integer id) {
        ventaRepository.deleteById(id);
    }

}
