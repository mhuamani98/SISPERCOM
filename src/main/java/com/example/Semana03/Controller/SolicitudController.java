package com.example.Semana03.Controller;

import com.example.Semana03.Model.Solicitud;
import com.example.Semana03.Repository.SolicitudRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

@Controller
@RequestMapping("/solicitud")
public class SolicitudController {

    @Autowired
    private SolicitudRepository solicitudRepo;

    private final String UPLOAD_DIR = "uploads/";

    // --- SECCIÓN USUARIO ---

    @GetMapping("/form") 
    public String nuevaSolicitud(Model model) {
        model.addAttribute("solicitud", new Solicitud());
        return "solicitud_form";
    }

    @PostMapping("/guardar")
    public String guardarSolicitud(@Valid @ModelAttribute("solicitud") Solicitud solicitud,
                                   BindingResult result,
                                   @RequestParam(value = "fileItse", required = false) MultipartFile fileItse,
                                   @RequestParam(value = "fileContrato", required = false) MultipartFile fileContrato,
                                   @RequestParam(value = "fileVoucher", required = false) MultipartFile fileVoucher,
                                   RedirectAttributes flash) throws IOException {

        if (result.hasErrors()) {
            return "solicitud_form";
        }

        // --- PUNTO CRÍTICO: Rescate de datos para no perderlos en la edición ---
        if (solicitud.getId() != null) {
            Solicitud solicitudExistente = solicitudRepo.findById(solicitud.getId()).orElse(null);
            if (solicitudExistente != null) {
                // Si el form manda nulos (por campos ocultos o disabled), recuperamos de la DB
                if (solicitud.getDireccion() == null) solicitud.setDireccion(solicitudExistente.getDireccion());
                if (solicitud.getNombreNegocio() == null) solicitud.setNombreNegocio(solicitudExistente.getNombreNegocio());
                if (solicitud.getRuc() == null) solicitud.setRuc(solicitudExistente.getRuc());
                if (solicitud.getGiro() == null) solicitud.setGiro(solicitudExistente.getGiro());
                if (solicitud.getAreaM2() == null) solicitud.setAreaM2(solicitudExistente.getAreaM2());
                if (solicitud.getAforo() == null) solicitud.setAforo(solicitudExistente.getAforo());
                if (solicitud.getRiesgo() == null) solicitud.setRiesgo(solicitudExistente.getRiesgo());
                
                // Mantener archivos existentes si no se suben nuevos
                if (fileItse.isEmpty()) solicitud.setArchivoItse(solicitudExistente.getArchivoItse());
                if (fileContrato.isEmpty()) solicitud.setArchivoContrato(solicitudExistente.getArchivoContrato());
                if (fileVoucher.isEmpty()) solicitud.setArchivoVoucher(solicitudExistente.getArchivoVoucher());
            }
        }

        // Procesar archivos nuevos
        if (!fileItse.isEmpty()) solicitud.setArchivoItse(saveFile(fileItse));
        if (!fileContrato.isEmpty()) solicitud.setArchivoContrato(saveFile(fileContrato));
        if (!fileVoucher.isEmpty()) solicitud.setArchivoVoucher(saveFile(fileVoucher));

        if (solicitud.getEstado() == null) solicitud.setEstado("PENDIENTE");
        solicitudRepo.save(solicitud);

        flash.addFlashAttribute("showModal", true);
        return "redirect:/solicitud/usuario_home"; 
    }

    @GetMapping("/usuario_home")
    public String homeUsuario(Model model, Authentication auth) {
        model.addAttribute("solicitudes", solicitudRepo.findAll()); 
        return "usuario_home";
    }

    // --- SECCIÓN EVALUADOR ---

    @GetMapping("/home-evaluador")
    public String homeEvaluador(Model model) {
        model.addAttribute("solicitudes", solicitudRepo.findAll());
        return "evaluador_home";
    }

    @PostMapping("/actualizar-estado")
    public String actualizarEstado(@RequestParam Long id, 
                                   @RequestParam String nuevoEstado, 
                                   @RequestParam(required = false) String observaciones,
                                   RedirectAttributes flash) {
        
        // RECUPERAR EL OBJETO COMPLETO DE LA DB PARA NO PERDER DATOS
        Solicitud solicitud = solicitudRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado(nuevoEstado);
        
        if ("APROBADO".equals(nuevoEstado)) {
            solicitud.setObservaciones(null);
        } else {
            solicitud.setObservaciones(observaciones);
        }
        
        solicitudRepo.save(solicitud); // Esto hace un UPDATE seguro
        flash.addFlashAttribute("success", "Trámite actualizado correctamente");
        return "redirect:/solicitud/home-evaluador"; 
    }

    // --- SECCIÓN ABOGADO ---

    @GetMapping("/home-abogado")
    public String bandejaAbogado(Model model) {
        model.addAttribute("solicitudes", solicitudRepo.findAll()); 
        return "abogado_home";
    }

    @PostMapping("/emitir-certificado")
    public String emitirCertificado(@RequestParam Long id, 
                                    @RequestParam("fileCertificado") MultipartFile firmaFile,
                                    RedirectAttributes flash) throws Exception {
        
        Solicitud solicitud = solicitudRepo.findById(id).orElseThrow();
        
        if (!firmaFile.isEmpty()) {
            String nombrePdf = "Certificado_" + solicitud.getId() + "_" + System.currentTimeMillis() + ".pdf";
            Path pathPdf = Paths.get(UPLOAD_DIR).resolve(nombrePdf);

            PdfWriter writer = new PdfWriter(pathPdf.toFile());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(25, 25, 25, 25);

            Table tableMarco = new Table(1).useAllAvailableWidth();
            Cell celdaPrincipal = new Cell().setPadding(35);
            celdaPrincipal.setBorder(new com.itextpdf.layout.borders.SolidBorder(new com.itextpdf.kernel.colors.DeviceRgb(0, 51, 102), 3));

            celdaPrincipal.add(new Paragraph("REPÚBLICA DEL PERÚ").setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBold().setFontColor(new DeviceRgb(255,0,0)));
            celdaPrincipal.add(new Paragraph("LICENCIA MUNICIPAL DE FUNCIONAMIENTO").setTextAlignment(TextAlignment.CENTER).setFontSize(20).setBold().setMarginTop(2f));
            celdaPrincipal.add(new Paragraph("Habiendo cumplido con los requisitos establecidos y en aplicación de lo dispuesto en "
                    + "la ley N°28976 Ley Marco de Licencia de Funcionamiento y en el artículo 79° numeral 3.6.4 de la ley 27972, "
                    + "Ley Orgánica de Municipalidades:")
    .setTextAlignment(TextAlignment.CENTER).setFontSize(12).setFontColor(new DeviceRgb(0, 51, 102)));
                    
                 

            // --- PROTECCIÓN CONTRA NULOS (ANTI-WHITE LABEL ERROR) ---
            String nombreNegocio = (solicitud.getNombreNegocio() != null) ? solicitud.getNombreNegocio().toUpperCase() : "SIN NOMBRE";
            String ruc = (solicitud.getRuc() != null) ? solicitud.getRuc() : "---";
            String direccion = (solicitud.getDireccion() != null) ? solicitud.getDireccion() : "No especificada";
            String giro = (solicitud.getGiro() != null) ? solicitud.getGiro() : "Giro no especificado";
            String area = (solicitud.getAreaM2() != null) ? solicitud.getAreaM2().toString() : "0.00";
            String aforo = (solicitud.getAforo() != null) ? solicitud.getAforo().toString() : "0";
            String riesgo = (solicitud.getRiesgo() != null) ? solicitud.getRiesgo() : "BAJO";

            Paragraph cuerpo = new Paragraph().setTextAlignment(TextAlignment.CENTER).setMultipliedLeading(1.5f);
            cuerpo.add(new Text("\nSe expide el presente a favor de:\n").setItalic());
            cuerpo.add(new Text(nombreNegocio).setBold().setFontSize(14));
            cuerpo.add(new Text("\nRUC: ").setBold()); cuerpo.add(new Text(ruc));
            cuerpo.add(new Text("\nUBICACIÓN: ").setBold()); cuerpo.add(new Text(direccion));
            cuerpo.add(new Text("\nGIRO: ").setBold()); cuerpo.add(new Text(giro));
            cuerpo.add(new Text("\nÁREA: ").setBold()); cuerpo.add(new Text(area + " m²"));
            cuerpo.add(new Text(" | AFORO: ").setBold()); cuerpo.add(new Text(aforo));
            cuerpo.add(new Text("\nRIESGO: ").setBold()); cuerpo.add(new Text(riesgo));

            celdaPrincipal.add(cuerpo);

            // Footer con Firma y QR
            Table tablaFooter = new Table(2).useAllAvailableWidth();
            Cell celdaFirma = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            Image imgFirmaParaCert = new Image(ImageDataFactory.create(firmaFile.getBytes()));
            imgFirmaParaCert.setWidth(120).setHorizontalAlignment(HorizontalAlignment.CENTER);

            celdaFirma.add(imgFirmaParaCert);
            celdaFirma.add(new Paragraph("___________________________\nAbog. Responsable de ITSE").setTextAlignment(TextAlignment.CENTER).setFontSize(9));
            tablaFooter.addCell(celdaFirma);

            Cell celdaQR = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            String urlValidacion = "http://192.168.18.211:7777/solicitud/ver-certificado/" + solicitud.getId();
            Image imgQR = new Image(ImageDataFactory.create(generarQR(urlValidacion)));
            imgQR.setWidth(80).setHorizontalAlignment(HorizontalAlignment.RIGHT);

            celdaQR.add(new Paragraph("Validar documento:").setFontSize(8).setItalic());
            celdaQR.add(imgQR);
            tablaFooter.addCell(celdaQR);

            celdaPrincipal.add(new Paragraph("\n"));
            celdaPrincipal.add(tablaFooter);
            tableMarco.addCell(celdaPrincipal);
            document.add(tableMarco);
            document.close();

            solicitud.setArchivoCertificado(nombrePdf);
            solicitud.setEstado("EMITIDO"); 
            solicitudRepo.save(solicitud);
            flash.addFlashAttribute("success", "Certificado oficial generado correctamente.");
        }
        return "redirect:/solicitud/home-abogado";
    }

    @GetMapping("/ver-certificado/{id}")
    public ResponseEntity<Resource> verCertificado(@PathVariable Long id) throws MalformedURLException {
        Solicitud solicitud = solicitudRepo.findById(id).orElseThrow();
        Path path = Paths.get(UPLOAD_DIR).resolve(solicitud.getArchivoCertificado());
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // --- UTILITARIOS ---

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private byte[] generarQR(String texto) throws Exception {
        com.google.zxing.qrcode.QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();
        com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(texto, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);
        java.io.ByteArrayOutputStream pngOutputStream = new java.io.ByteArrayOutputStream();
        com.google.zxing.client.j2se.MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
    
    @GetMapping("/detalle/{id}")
public String verDetalle(@PathVariable Long id, Model model) {
    // Buscamos la solicitud completa con todos sus datos técnicos
    Solicitud solicitud = solicitudRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    
    model.addAttribute("solicitud", solicitud);
    return "evaluador_detalle"; // Nombre del nuevo HTML que crearemos
}
@GetMapping("/editar/{id}")
public String editarSolicitud(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
    Solicitud solicitud = solicitudRepo.findById(id).orElse(null);
    
    if (solicitud == null) {
        flash.addFlashAttribute("error", "La solicitud no existe.");
        return "redirect:/solicitud/usuario_home";
    }

    model.addAttribute("solicitud", solicitud);
    model.addAttribute("titulo", "Corregir Solicitud");
    return "solicitud_form"; 
}
}