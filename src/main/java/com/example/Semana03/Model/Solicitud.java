package com.example.Semana03.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data // Esto ya genera automáticamente todos los Getters y Setters
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombreNegocio;
    private String ruc;
    private String estado; 
    private String observaciones;
    
    // Datos del Local
    private String direccion;
    private Integer piso;
    private Double areaM2;  // Asegúrate de usar .getAreaM2() en el controlador
    private Integer aforo;
    private String riesgo;  // Bajo, Medio, Alto
    private String giro;    // Rubro general
    private String giroDetallado;

    // Campos para archivos
    private String archivoItse;
    private String archivoContrato;
    private String archivoVoucher;
    private String archivoCertificado; // El PDF final
    
    // Datos de contacto y gestión
    private String telefonoContacto;
    private String firmaDigital; 
    private String nroCertificado;
    private LocalDate fechaEmision;
}