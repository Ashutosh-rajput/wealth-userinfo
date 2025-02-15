package com.WealthManager.UserInfo.service.implimentation;//package com.softnerve.Patient_MicroService.service.implimentation;
//
//import com.google.api.gax.paging.Page;
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import com.softnerve.Patient_MicroService.constant.ApiConstant;
//import com.softnerve.Patient_MicroService.exception.document.InvalidFileTypeException;
//import com.softnerve.Patient_MicroService.exception.document.DocumentNotFoundException;
//import com.softnerve.Patient_MicroService.exception.document.FileAlreadyExistsException;
//import com.softnerve.Patient_MicroService.exception.document.FileUploadException;
//import com.softnerve.Patient_MicroService.exception.document.FolderNotFoundException;
//import com.softnerve.Patient_MicroService.model.dto.SuccessResponse;
//import com.softnerve.Patient_MicroService.model.upload_interface.DocumentEntity;
//import com.softnerve.Patient_MicroService.service.DocumentService;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.attachment.softnerve.service.KafkaService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Service
//@Setter
//@RequiredArgsConstructor
//public class DocumentIMPL implements DocumentService {
//
//    @Value("${gcp.bucket.name}")
//    private String bucketName;
//
//    private final Storage storage;
//
//    private final KafkaService kafkaService;
//
//    // Upload Multiple files
//    @Override
//    public SuccessResponse uploadFile(DocumentEntity documentEntity, List<MultipartFile> files) {
//        String rootFolderName = documentEntity.getId() + "/";
//        String documentsFolderName = rootFolderName + "Documents/";
//
//        // Create the root folder and Documents subfolder
//        createFolder(documentsFolderName);
//
//        for (MultipartFile file : files) {
//            checkFileExistence(documentEntity.getId(), file.getOriginalFilename());
//
//            Blob blob;
//            try {
//                // Upload the file to the Documents folder in Cloud Storage
//                blob = uploadBlob(bucketName, documentsFolderName, file);
//            } catch (IOException e) {
//                log.error("Failed to upload document: {}", file.getOriginalFilename(), e);
//                throw new FileUploadException("Failed to upload documents");
//            }
//
//            // Publish file information to Kafka
//            publishToKafka(documentEntity.getId(), blob);
//            log.info("Document {} uploaded successfully.", file.getOriginalFilename());
//        }
//
//        return new SuccessResponse(
//                HttpStatus.OK.value(),
//                "Documents uploaded successfully",
//                ApiConstant.ADD_DOCUMENT,
//                null
//        );
//    }
//
//
//    // Creates a folder for storing files associated with the User ID
//    protected void createFolder(String folderName) {
//        BlobId folderBlobId = BlobId.of(bucketName, folderName);
//        BlobInfo folderBlobInfo = BlobInfo.newBuilder(folderBlobId).build();
//        storage.create(folderBlobInfo);
//    }
//
//
//    // Uploads a file to Cloud Storage and returns the Blob object
//    protected Blob uploadBlob(String bucketName, String folderName, MultipartFile file) throws IOException {
//        String filePath = folderName + file.getOriginalFilename();
//        BlobId blobId = BlobId.of(bucketName, filePath);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
//                .setContentType(file.getContentType())
//                .build();
//        return storage.create(blobInfo, file.getBytes());
//    }
//
//    // Publishes the file URL to Kafka for further processing
//    protected void publishToKafka(String userId, Blob blob) {
//        String newFileUrl = generateSignedUrl(blob, 365, TimeUnit.DAYS).toString();
//        kafkaService.publishToKafkaAsync("files", userId, newFileUrl);
//        log.info("Published file URL {} to files for user {}", newFileUrl, userId);
//    }
//
//    //Helper Method to check if the file already exists then throw exception
//    protected void checkFileExistence(String documentId, String fileName) {
//        if (doesFileExist(documentId, fileName)) {
//            log.warn("File already exists: {}", fileName);
//            throw new FileAlreadyExistsException("File already exists: " + fileName);
//        }
//    }
//
//    // Add a method to check if the folder already exists
//    protected boolean doesFolderExist(String userId) {
//        // Check if the folder with userId exists in the bucket
//        Storage.BlobListOption blobListOption = Storage.BlobListOption.prefix(userId + "/");
//        Page<Blob> blobs = storage.list(bucketName, blobListOption);
//        return blobs.iterateAll().iterator().hasNext();
//    }
//
//    // Add a method to check if the file already exists
//    protected boolean doesFileExist(String userId, String fileName) {
//        String filePath = userId + "/" + fileName;
//        BlobId blobId = BlobId.of(bucketName, filePath);
//        Blob blob = storage.get(blobId);
//        return blob != null && !blob.getName().endsWith("/");
//    }
//
//    // Helper method to check if the folder exists
//    protected void checkFolderExistence(String userId) {
//        if (!doesFolderExist(userId)) {
//            log.warn("Folder {} does not exist.", userId);
//            throw new FolderNotFoundException("Folder " + userId + " does not exist.");
//        }
//    }
//
//    // Helper method to Generates a signed URL
//    protected URL generateSignedUrl(Blob blob, long duration, TimeUnit unit) {
//        return blob.signUrl(duration, unit);
//    }
//
//    @Override
//    public SuccessResponse listOfFiles(DocumentEntity documentEntity) {
//        List<String> list = new ArrayList<>();
//
//        if (doesFolderExist(documentEntity.getId())) {
//            // Create a prefix to list objects only in the user's folder
//            String prefix = documentEntity.getId() + "/Documents/";
//
//            // Use BlobListOption to filter blobs based on the prefix
//            Storage.BlobListOption blobListOption = Storage.BlobListOption.prefix(prefix);
//
//            Iterable<Blob> blobs = storage.list(bucketName, blobListOption).iterateAll();
//
//            for (Blob blob : blobs) {
//                if (!blob.getName().endsWith("/")) {
//                    // Get the file name from the blob's name
//                    String fileName = blob.getName().substring(blob.getName().lastIndexOf("/") + 1);
//
//                    // Generate authenticated URL with a NO expiration time for each blob
//                    URL authenticatedUrl = generateSignedUrl(blob, 365, TimeUnit.DAYS);
//
//                    list.add(fileName + " : " + authenticatedUrl.toString());
//                }
//            }
//            return new SuccessResponse(
//                    HttpStatus.OK.value(),
//                    "Documents fetched successfully",
//                    ApiConstant.GET_ALL_DOCUMENTS.replace("{userId}", documentEntity.getId()),
//                    new SuccessResponse.ResponseData<>(list, null)
//            );
//        } else {
//            log.warn("No documents found for user with ID: {}", documentEntity.getId());
//
//            return new SuccessResponse(
//                    HttpStatus.NOT_FOUND.value(),
//                    "Documents Not Found",
//                    ApiConstant.GET_ALL_DOCUMENTS.replace("{userId}", documentEntity.getId()),
//                    null
//            );
//        }
//    }
//
//    // Deletes a file from the specified document entity's folder in the storage.
//    @Override
//    public SuccessResponse deleteFile(DocumentEntity documentEntity, String fileName) {
//        checkFolderExistence(documentEntity.getId());
//        String filePath = documentEntity.getId() + "/Documents/" + fileName;
//        BlobId blobId = BlobId.of(bucketName, filePath);
//        Blob blob = storage.get(blobId);
//
//        if (blob != null && !blob.getName().endsWith("/")) {
//            blob.delete();
//            log.info("Document {} has been deleted.", fileName);
//        } else {
//            log.warn("Document {} not found in folder {}.", fileName, documentEntity.getId());
//            throw new DocumentNotFoundException("Document " + fileName + " not found in folder " + documentEntity.getId() + ".");
//        }
//
//        return SuccessResponse.builder()
//                .statusCode(200)
//                .message("Document " + fileName + " deleted successfully")
//                .path(ApiConstant.DELETE_DOCUMENT)
//                .responseData(null)
//                .build();
//    }
//
//    // Retrieves download URLs for all documents associated with the specified User ID.
//    @Override
//    public SuccessResponse getDownloadUrls(DocumentEntity documentEntity) {
//        // Step 1: Check if the folder for the user exists
//        checkFolderExistence(documentEntity.getId());
//
//        // Initialize a list to store download URLs
//        List<String> downloadUrls = new ArrayList<>();
//
//        // Create a prefix to list objects only in the user's folder
//        String prefix = documentEntity.getId() + "/Documents/";
//
//        // Use BlobListOption to filter blobs based on the prefix
//        Storage.BlobListOption blobListOption = Storage.BlobListOption.prefix(prefix);
//
//        // List all blobs (files) in the bucket with the specified prefix
//        Iterable<Blob> blobs = storage.list(bucketName, blobListOption).iterateAll();
//
//        // Step 2: Iterate through each blob in the user's folder
//        for (Blob blob : blobs) {
//            // Check if the blob is not a folder (it does not end with a slash)
//            if (!blob.getName().endsWith("/")) {
//                // Get the file name from the blob's name
//                String fileName = blob.getName().substring(blob.getName().lastIndexOf("/") + 1);
//
//                // Generate signed URL for downloading the blob's content
//                URL authenticatedUrl = generateSignedUrl(blob, 365 * 100, TimeUnit.DAYS);
//
//                // Append Content-Disposition header to force download
//                String downloadUrl = authenticatedUrl.toString() + "&response-content-disposition=attachment";
//
//                // Add the file name and its download URL to the list
//                downloadUrls.add(fileName + " : " + downloadUrl);
//            }
//        }
//
//        log.info("Download URLs fetched successfully for user with ID: {}", documentEntity.getId());
//
//        // Construct the SuccessResponse object
//        return new SuccessResponse(
//                HttpStatus.OK.value(),
//                "Download URLs fetched successfully",
//                ApiConstant.DOWNLOAD_DOCUMENTS.replace("{userId}", documentEntity.getId()),
//                new SuccessResponse.ResponseData<>(downloadUrls, null)
//        );
//    }
//
//    // Uploads a profile photo for the specified User.
//    @Override
//    public SuccessResponse uploadProfilePhoto(DocumentEntity documentEntity, MultipartFile file) {
//        try {
//            String contentType = file.getContentType();
//            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
//                throw new InvalidFileTypeException("Profile photo must be in JPEG or PNG format.");
//            }
//            // Step 1: Create the root folder and Profile subfolder
//            String rootFolderName = documentEntity.getId() + "/";
//            String profileFolderName = rootFolderName + "Profile/";
//            createFolder(profileFolderName);
//
//            // Step 2: Create a Blob inside the Profile folder for the uploaded profile photo
//            String filePath = profileFolderName + "profile_photo";
//            BlobId blobId = BlobId.of(bucketName, filePath);
//            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
//                    .setContentType(file.getContentType())
//                    .build();
//            storage.create(blobInfo, file.getBytes());
//
//            log.info("Profile photo uploaded successfully in folder {}.", profileFolderName);
//            return new SuccessResponse(
//                    HttpStatus.OK.value(),
//                    "Profile photo uploaded successfully.",
//                    ApiConstant.UPLOAD_PROFILE_PHOTO,
//                    null
//            );
//        } catch (IOException e) {
//            log.error("Failed to upload profile photo", e);
//            throw new FileUploadException("Failed to upload profile photo");
//        }
//    }
//
//
//    @Override
//    public SuccessResponse getProfilePhotoUrl(DocumentEntity documentEntity) {
//        // Check if the folder exists for the given user ID
//        if (doesFolderExist(documentEntity.getId())) {
//            // Create a prefix to list objects only in the user's folder
//            String prefix = documentEntity.getId() + "/Profile/";
//
//            // Use BlobListOption to filter blobs based on the prefix
//            Storage.BlobListOption blobListOption = Storage.BlobListOption.prefix(prefix);
//
//            // List all blobs (files) in the bucket with the specified prefix
//            Iterable<Blob> blobs = storage.list(bucketName, blobListOption).iterateAll();
//
//            // Iterate through each blob in the user's folder
//            for (Blob blob : blobs) {
//                if (!blob.getName().endsWith("/")) {
//                    // Get the file name from the blob's name
//                    String fileName = blob.getName().substring(blob.getName().lastIndexOf("/") + 1);
//
//                    // If the file is the profile photo
//                    if (fileName.equals("profile_photo")) {
//                        // Generate authenticated URL with a NO expiration time for the blob
//                        String signedUrl = generateSignedUrl(blob, 365, TimeUnit.DAYS).toString();
//
//                        // Return the success response with the URL
//                        return new SuccessResponse(
//                                HttpStatus.OK.value(),
//                                "Profile photo URL retrieved successfully",
//                                ApiConstant.GET_PROFILE_PHOTO_URL.replace("{userId}", documentEntity.getId()),
//                                new SuccessResponse.ResponseData<>(Collections.singletonList(signedUrl), null) // Pagination is null
//                        );
//                    }
//                }
//            }
//        } else {
//            log.warn("No profile found for user with ID: {}", documentEntity.getId());
//        }
//        // If no profile photo URL is found, return a not found response
//        return new SuccessResponse(
//                HttpStatus.NOT_FOUND.value(),
//                "Profile photo not found",
//                ApiConstant.GET_PROFILE_PHOTO_URL.replace("{userId}", documentEntity.getId()),
//                null // Pagination is null
//        );
//    }
//}
