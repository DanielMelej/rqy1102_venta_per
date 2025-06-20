package com.ventas.vmventas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ventas.vmventas.model.Venta;
import com.ventas.vmventas.service.VentaService;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {
    @Autowired
    public VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> listar(){
        List<Venta> ventas = ventaService.getAllVentas();
        if (ventas.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscar(@PathVariable Integer id){
        try{
            Venta venta = ventaService.findById(id);
            return ResponseEntity.ok(venta);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    // @PostMapping
    // public ResponseEntity<Venta> createVenta(@RequestBody Venta venta) {
    //     Venta savedVenta = ventaService.save(venta);
    //     return ResponseEntity.ok(savedVenta);
    // }

    @PostMapping
    public ResponseEntity<?> createVenta(@RequestBody Venta venta) {
    try {
        Venta savedVenta = ventaService.save(venta);
        return ResponseEntity.ok(savedVenta);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error interno al guardar la venta");
    }
}

    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizar(@PathVariable Integer id, @RequestBody Venta ventaActualizada) {
        try {
            Venta ventaExistente = ventaService.findById(id);
            
            // Actualizar campos
            ventaExistente.setProductoId(ventaActualizada.getProductoId());
            ventaExistente.setCantidad(ventaActualizada.getCantidad());
            ventaExistente.setTotal(ventaActualizada.getTotal());

            Venta ventaGuardada = ventaService.save(ventaExistente);
            return ResponseEntity.ok(ventaGuardada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            ventaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



}
