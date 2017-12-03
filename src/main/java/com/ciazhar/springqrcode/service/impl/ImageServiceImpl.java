package com.ciazhar.springqrcode.service.impl;

import com.ciazhar.springqrcode.service.ImageService;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.zxing.BarcodeFormat.*;
import static com.google.zxing.client.j2se.MatrixToImageWriter.*;
import static org.slf4j.LoggerFactory.*;
import static org.springframework.http.MediaType.IMAGE_PNG;

/**
 * Created by ciazhar on 04/12/17.
 * [ Documentatiion Here ]
 */

@Service
@Cacheable(cacheNames = "qr-code-cache", sync = true)
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = getLogger(ImageServiceImpl.class);

    public byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException {

        Assert.hasText(text);
        Assert.isTrue(width > 0);
        Assert.isTrue(height > 0);

        LOGGER.info("Will generate image  text=[{}], width=[{}], height=[{}]", text, width, height);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitMatrix matrix = new MultiFormatWriter().encode(text, QR_CODE, width, height);
        writeToStream(matrix, IMAGE_PNG.getSubtype(), baos, new MatrixToImageConfig());
        return baos.toByteArray();
    }

    @Override
    public ListenableFuture<byte[]> generateQRCodeAsync(String text, int width, int height) throws Exception {
        return new AsyncResult<>(generateQRCode(text, width, height));
    }

    @CacheEvict(cacheNames = "qr-code-cache", allEntries = true)
    public void purgeCache() {
        LOGGER.info("Purging cache");
    }
}
