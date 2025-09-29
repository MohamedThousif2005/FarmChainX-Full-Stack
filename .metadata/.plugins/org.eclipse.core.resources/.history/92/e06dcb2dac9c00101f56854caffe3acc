package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.OrderDTO;
import com.farmchainx.backend.service.OrderService;
import com.farmchainx.backend.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(origins = "*")
public class InvoiceController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PdfService pdfService;
    
    @GetMapping("/{orderId}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            byte[] pdfBytes = pdfService.generateInvoicePdf(order);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "invoice_" + order.getOrderNumber() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
                    
        } catch (DocumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}