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

    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    public Venta findById(Integer id) {
        return ventaRepository.findById(id).get();
    }

    public Venta save(Venta venta) {
        if (venta == null) {
            throw new RuntimeException("❌ La venta no puede ser nula");
        }

        if (venta.getProductoId() == null) {
            throw new RuntimeException("❌ El ID del producto no puede ser nulo");
        }

        if (venta.getCantidad() == null || venta.getCantidad() <= 0) {
            throw new RuntimeException("❌ La cantidad debe ser mayor a 0");
        }

        // Paso 1: Obtener producto desde el otro microservicio
        ProductoDTO producto = productoServiceClient.obtenerProductoPorId(venta.getProductoId());

        if (producto == null) {
            throw new RuntimeException("❌ El producto no existe");
        }

        // Paso 2: Validar stock
        if (producto.getStock() < venta.getCantidad()) {
            throw new RuntimeException("❌ Stock insuficiente para el producto solicitado");
        }

        // Paso 3: Descontar stock
        producto.setStock(producto.getStock() - venta.getCantidad());

        // Paso 4: Actualizar producto
        productoServiceClient.actualizarProducto(producto.getIdProducto(), producto);

        // Paso 5: Calcular total y guardar la venta
        venta.setTotal(producto.getPrecio() * venta.getCantidad());

        return ventaRepository.save(venta);
    }

    // public Venta updateVenta(Integer id, Venta ventaActualizada) {
    // Venta ventaExistente = findById(id);

    // if (!ventaExistente.getProductoId().equals(ventaActualizada.getProductoId()))
    // {
    // throw new RuntimeException("No se puede cambiar el producto de una venta
    // existente");
    // }

    // int cantidadOriginal = ventaExistente.getCantidad();
    // int cantidadNueva = ventaActualizada.getCantidad();
    // int diferencia = cantidadOriginal - cantidadNueva;

    // ProductoDTO producto =
    // productoServiceClient.obtenerProductoPorId(ventaExistente.getProductoId());
    // if (producto == null) {
    // throw new RuntimeException("Producto no encontrado");
    // }

    // int stockActual = producto.getStock();
    // int stockNuevo = stockActual + diferencia;

    // if (stockNuevo < 0) {
    // throw new RuntimeException("Stock insuficiente para esta modificación");
    // }

    // producto.setStock(stockNuevo);
    // productoServiceClient.actualizarProducto(producto.getIdProducto(), producto);

    // ventaExistente.setCantidad(cantidadNueva);
    // ventaExistente.setTotal(producto.getPrecio() * cantidadNueva);

    // return ventaRepository.save(ventaExistente);
    // }

    public Venta updateVenta(Integer id, Venta ventaActualizada) {
        if (ventaActualizada == null) {
            throw new RuntimeException("❌ El cuerpo de la solicitud no puede estar vacío");
        }

        if (ventaActualizada.getCantidad() == null || ventaActualizada.getCantidad() <= 0) {
            throw new RuntimeException("❌ La cantidad debe ser mayor a 0");
        }

        Venta ventaExistente = findById(id);

        // Validación: no se permite modificar el ID (aunque normalmente no cambiaría
        // por ser del path)
        if (ventaActualizada.getId() != null && !ventaActualizada.getId().equals(id)) {
            throw new RuntimeException("❌ No está permitido modificar el ID de la venta");
        }

        // Validación: no se permite modificar el producto asociado
        if (ventaActualizada.getProductoId() != null &&
                !ventaActualizada.getProductoId().equals(ventaExistente.getProductoId())) {
            throw new RuntimeException("❌ No está permitido modificar el producto de una venta existente");
        }

        // Validación: no se permite modificar el total manualmente
        if (ventaActualizada.getTotal() != null &&
                !ventaActualizada.getTotal().equals(ventaExistente.getTotal())) {
            throw new RuntimeException("❌ No está permitido modificar el total manualmente");
        }

        // Recalcular la diferencia de cantidad
        int cantidadOriginal = ventaExistente.getCantidad();
        int cantidadNueva = ventaActualizada.getCantidad();
        int diferencia = cantidadOriginal - cantidadNueva;

        ProductoDTO producto = productoServiceClient.obtenerProductoPorId(ventaExistente.getProductoId());
        if (producto == null) {
            throw new RuntimeException("❌ Producto no encontrado");
        }

        int stockActual = producto.getStock();
        int stockNuevo = stockActual + diferencia;

        if (stockNuevo < 0) {
            throw new RuntimeException("❌ Stock insuficiente para esta modificación");
        }

        // Actualizar el stock en el microservicio de productos
        producto.setStock(stockNuevo);
        productoServiceClient.actualizarProducto(producto.getIdProducto(), producto);

        // Actualizar solo la cantidad y el total
        ventaExistente.setCantidad(cantidadNueva);
        ventaExistente.setTotal(producto.getPrecio() * cantidadNueva);

        return ventaRepository.save(ventaExistente);
    }

    public void delete(Integer id) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));

        ProductoDTO producto = productoServiceClient.obtenerProductoPorId(ventaExistente.getProductoId());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }

        producto.setStock(producto.getStock() + ventaExistente.getCantidad());
        productoServiceClient.actualizarProducto(producto.getIdProducto(), producto);

        ventaRepository.deleteById(id);
    }
}
