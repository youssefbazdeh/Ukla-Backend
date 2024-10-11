package spark.ukla.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Video;
import spark.ukla.services.implementations.FileLocationService;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("file-system-video")
public class FileSystemVideoController {

    @Autowired
    FileLocationService fileLocationService;




    @PostMapping(value = "saveVideo")
    Video uploadVideo(@RequestParam MultipartFile video) {
        return fileLocationService.saveVideo(video);
    }

    @GetMapping(value = "/video/{videoId}")

    ResponseEntity<InputStreamResource> downloadVideo(@PathVariable Long videoId) {
        return fileLocationService.findVideo(videoId);
    }

    @PutMapping("update-video-url")
    public ResponseEntity<String> updateVideoUrl(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        try {
            ResponseEntity<String> response = fileLocationService.updateVideoUrl(url);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Error updating video URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update video URL");
        }
    }
}
