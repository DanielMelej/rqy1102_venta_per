package com.ventas.vmventas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ventas.vmventas.DTO.ProductoDTO;

@Component
public class ProductoServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    private final String productoServiceUrl = "http://localhost:8000/api/v1/productos"; 

    // public ProductoDTO obtenerProductoPorId(Integer id) {
    //     return restTemplate.getForObject(productoServiceUrl + "/" + id, ProductoDTO.class);
    // }

    public ProductoDTO obtenerProductoPorId(Integer id) {
    try {
        return restTemplate.getForObject(productoServiceUrl + "/" + id, ProductoDTO.class);
    } catch (HttpClientErrorException.NotFound e) {
        // Si recibe un 404, devuelve null
        return null;
    } catch (RestClientException e) {
        // Para cualquier otro error de conexión, puedes loguear o lanzar algo más controlado
        throw new RuntimeException("Error al comunicarse con el servicio de productos");
    }
}

    public void actualizarProducto(Integer id, ProductoDTO producto) {
        restTemplate.put(productoServiceUrl + "/" + id, producto);
    }

    
}
