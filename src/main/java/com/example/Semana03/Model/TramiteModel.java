package com.example.Semana03.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tramites")
public class TramiteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ===== DATOS DEL SOLICITANTE (Administrado) =====
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String celular;

    // ===== DATOS DEL ESTABLECIMIENTO (Local) =====
    private String nombreComercial;
    private String giro; // Ej: Bodega, Restaurante
    private String direccion;
    private Double area; // Metros cuadrados
    private String tipoLicencia; // Definitiva, Temporal, etc.

    // ===== DOCUMENTOS ADJUNTOS (Nombres de archivos) =====
    private String itse;     // Certificado de seguridad
    private String contrato; // Contrato de alquiler/propiedad
    private String voucher;  // Comprobante de pago
    private String archivoLicencia; // PDF generado al final

    // ===== INFORMACIÓN DEL SISTEMA =====
    private String numeroExpediente;
    private String observacion;
    
    @Column(length = 20)
    private String estado; // PENDIENTE, EN_REVISION, APROBADO, RECHAZADO

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario; // Usuario que creó el trámite

    /**
     * Se ejecuta automáticamente antes de guardar en la BD.
     */
    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
        this.fechaRegistro = LocalDateTime.now().withNano(0);
        
        // Generar un número de expediente básico si está vacío
        if (this.numeroExpediente == null) {
            this.numeroExpediente = "EXP-" + System.currentTimeMillis() % 100000;
        }
    }
}