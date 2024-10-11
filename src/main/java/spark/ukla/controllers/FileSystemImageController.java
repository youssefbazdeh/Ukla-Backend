package spark.ukla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Image;
import spark.ukla.services.implementations.FileLocationService;

@RestController
@RequestMapping("file-system")
public class FileSystemImageController {

    @Autowired
    FileLocationService fileLocationService;

    @PostMapping("/image")
    Image uploadImage(@RequestParam MultipartFile image) throws Exception {
        return fileLocationService.save(image);
    }

    @GetMapping(value = "/image/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> downloadImage(@PathVariable Long imageId) {
        return fileLocationService.findImage(imageId);
    }

    @GetMapping(value = "/getIngredientImage/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> downloadIngredientImage(@PathVariable Long imageId) {
        return fileLocationService.findIngredientImage(imageId);
    }
}
