package spark.ukla.azurebob;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.sas.SasProtocol;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;


@Log4j2
@Service
public class AzureBlobFileService {


    private final BlobServiceClient blobServiceClient;
    public AzureBlobFileService( BlobServiceClient blobServiceClient) {

        this.blobServiceClient = blobServiceClient;
    }


    public String uploadVideo(@NonNull MultipartFile file,String location, String containerName) {
        String sasUrl = "";

        BlobContainerClient blobContainerClient = getBlobContainerClient(containerName);
        String filename = location;
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        try {
            if (blockBlobClient.exists()) {
                log.error("Error video name exits {}" );
            }
            blockBlobClient.upload(new BufferedInputStream(file.getInputStream()), file.getSize(), true);
            sasUrl = createSharedAccessSignaturesUrl(filename, containerName);


        } catch (IOException e) {
            log.error("Error while processing file {}", e.getLocalizedMessage());
            return sasUrl;
        }
        return sasUrl;
    }

    public boolean delete(String filename, String containerName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient(containerName);
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        try {
            if (!blockBlobClient.exists()) {
                log.info("Blob does not exist");
                return false; // Early exit if blob doesn't exist
            }
            blockBlobClient.delete();
            log.info("Deleted blob successfully");
        } catch (Exception e) {
            log.error("Failed to delete blob", e);
        }
        return true;
    }


    // upload Image
    public Boolean uploadFile(@NonNull MultipartFile file,String location, String containerName) {
        boolean isSuccess = true;
        BlobContainerClient blobContainerClient = getBlobContainerClient(containerName);
        String filename = location;
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
        if (blockBlobClient.exists()) {
            isSuccess = false;
            log.error("Error file name exits {}" );
            return isSuccess;
        }
        try {
            blockBlobClient.upload(new BufferedInputStream(file.getInputStream()), file.getSize(), true);
        } catch (IOException e) {
            isSuccess = false;
            log.error("Error while processing file {}", e.getLocalizedMessage());
        }
        return isSuccess;
    }


    public ResponseEntity<byte[]> getBlobContent(String blobName, String containerName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient(containerName);
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(blobName).getBlockBlobClient();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            blockBlobClient.download(outputStream);
            byte[] bytes = outputStream.toByteArray();
            return new ResponseEntity<>(bytes, HttpStatus.OK);
        } catch (BlobStorageException e) {
            log.error("Error while getting blob content: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private @NonNull BlobContainerClient getBlobContainerClient(@NonNull String containerName) {
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
        return blobContainerClient;
    }


    public String createSharedAccessSignaturesUrl(String blobName, String containerName){
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        BlobContainerSasPermission blobContainerSasPermission = new BlobContainerSasPermission()
                .setReadPermission(true);

        BlobServiceSasSignatureValues builder = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusMonths(1), // Set the expiration time
                blobContainerSasPermission
        )
                .setProtocol(SasProtocol.HTTPS_ONLY); // Set the allowed protocol

        String sasToken = blobClient.generateSas(builder);
        return String.format("%s?%s", blobClient.getBlobUrl(),sasToken);
    }

    public ResponseEntity<InputStreamResource> getBlobContentVideo(String blobName, String containerName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient(containerName);
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(blobName).getBlockBlobClient();
        try {
            InputStream inputStream = blockBlobClient.openInputStream();
            MediaType mediaType = MediaTypeFactory.getMediaType(blockBlobClient.getProperties().getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM);
            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + blobName + "\"")
                    .body(resource);
        } catch (BlobStorageException e) {
            log.error("Error while streaming video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}