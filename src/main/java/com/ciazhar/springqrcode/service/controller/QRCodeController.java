package com.ciazhar.springqrcode.service.controller;

import com.ciazhar.springqrcode.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import static java.util.concurrent.TimeUnit.*;
import static org.springframework.http.CacheControl.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by ciazhar on 04/12/17.
 * [ Documentatiion Here ]
 */

@RestController
@RequestMapping("/qrcode")
@CrossOrigin(methods = {GET, DELETE})
@EnableScheduling
public class QRCodeController {

    private final int QRCODE_WIDTH = 256;
    private final int QRCODE_HEIGHT = 256;
    public static final long THIRTY_MINUTES = 1800000;
    private final ImageService service;

    @Autowired
    public QRCodeController(ImageService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@RequestParam String text){
        try {
            return ok().cacheControl(maxAge(30, MINUTES))
                    .body(service.generateQRCodeAsync(text,QRCODE_WIDTH,QRCODE_HEIGHT).get());
        } catch (Exception e) {
            throw new RuntimeException("Error while generating QR code images.",e);
        }
    }

    @Scheduled(fixedRate = THIRTY_MINUTES)
    @DeleteMapping()
    @ResponseStatus(NO_CONTENT)
    public void deleteAllCachedImage(){
        service.purgeCache();
    }
}
