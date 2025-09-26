package com.csi_rscoe.csi_backend.Controllers.Public;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = { "http://localhost:5173" })
public class UploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        // Validate size (<= 2MB)
        long maxBytes = 2L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            return ResponseEntity.badRequest().body(Map.of("error", "File too large. Max 2MB"));
        }

        // Validate type (PNG or JPG/JPEG)
        String contentType = file.getContentType() != null ? file.getContentType() : "";
        boolean png = contentType.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE);
        boolean jpg = contentType.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE);
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!(png || jpg)) {
            // Try fallback by extension
            String lower = ext != null ? ext.toLowerCase() : "";
            if (!("png".equals(lower) || "jpg".equals(lower) || "jpeg".equals(lower))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only PNG or JPG allowed"));
            }
        }
        String filename = UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
        Path destDir = Paths.get(uploadDir);
        Files.createDirectories(destDir);
        Path dest = destDir.resolve(filename);
        file.transferTo(dest.toFile());
        Map<String, String> res = new HashMap<>();
        // return absolute URL to be accessible from frontend in dev
        String absoluteUrl = "http://localhost:8080/uploads/" + filename;
        res.put("url", absoluteUrl);
        return ResponseEntity.ok(res);
    }
}
