package spark.ukla.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import spark.ukla.azurebob.AzureBlobFileService;
import spark.ukla.entities.Image;
import spark.ukla.entities.Video;
import spark.ukla.repositories.ImageDbRepository;
import spark.ukla.repositories.VideoRepository;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service
public class FileLocationService {





        @Autowired
        private CacheManager cacheManager;
        private final ImageDbRepository imageDbRepository;

        private final VideoRepository videoRepository;
        private final AzureBlobFileService azureBlobFileService ;

    public FileLocationService( ImageDbRepository imageDbRepository, VideoRepository videoRepository, AzureBlobFileService azureBlobFileService) {

        this.imageDbRepository = imageDbRepository;
        this.videoRepository = videoRepository;
        this.azureBlobFileService = azureBlobFileService;
    }

    public Image save(MultipartFile image) throws Exception {
        String imageName = UUID.randomUUID().toString();
        if(azureBlobFileService.uploadFile(image,imageName,"images")){
            return imageDbRepository.save(new Image(imageName));
        }

        return null ;

        }

    @PostConstruct
    public void checkCache() {
        Cache cache = cacheManager.getCache("ingredientImages");
        if (cache != null) {
            System.out.println("Cache 'ingredientImages' is configured and available.");
        } else {
            System.out.println("Cache 'ingredientImages' is not available.");
        }
    }
    public ResponseEntity<byte[]> findIngredientImage(Long imageId) {
        ResponseEntity<byte[]> imageBytes = getCachedImageBytes(imageId);
        if (imageBytes != null) {
            return imageBytes;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found in cache");
        }
    }

    private ResponseEntity<byte[]> getCachedImageBytes(Long imageId) {
        Cache cache = cacheManager.getCache("ingredientImages");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(imageId);
            if (valueWrapper != null) {
                return (ResponseEntity<byte[]>) valueWrapper.get();
            } else {
                ResponseEntity<byte[]> imageBytes = findImage(imageId);
                cache.put(imageId, imageBytes);
                return imageBytes;
            }
        } else {
            return findImage(imageId);
        }
    }
        public ResponseEntity<byte[]> findImage(Long imageId) {
            Image image = imageDbRepository.findById(imageId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            return azureBlobFileService.getBlobContent(image.getLocation(),"images");
        }

        public Video saveVideo(MultipartFile video){
            String videoName = UUID.randomUUID().toString();
                String sasUrl = azureBlobFileService.uploadVideo(video,videoName,"videos");
            if(!sasUrl.isEmpty()){
                return videoRepository.save(new Video(videoName,sasUrl));
            }
            return null ;

        }

    public boolean deleteVideo(Long idVideo) {
        Video video = videoRepository.findById(idVideo).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (video == null ) {
            return false;
        }

        boolean success = azureBlobFileService.delete(video.getLocation(), "videos");
        return success;
    }
    public boolean deleteImage(String imageLocation) {

        boolean success = azureBlobFileService.delete(imageLocation, "images");
        return success;
    }

    public ResponseEntity<InputStreamResource> findVideo(Long idVideo){
            Video video = videoRepository.findById(idVideo).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
            return azureBlobFileService.getBlobContentVideo(video.getLocation(),"videos");
    }

    public ResponseEntity<String> updateVideoUrl(String url) {
        Video video = videoRepository.findVideoBySasUrl(url);
        String newUrl = azureBlobFileService.createSharedAccessSignaturesUrl(video.getLocation(),"videos");
        video.setSasUrl(newUrl);
        videoRepository.save(video);
        return ResponseEntity.ok(newUrl);
    }

}
