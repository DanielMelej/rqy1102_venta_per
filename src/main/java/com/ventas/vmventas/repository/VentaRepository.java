package com.ventas.vmventas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ventas.vmventas.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>{
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

}
