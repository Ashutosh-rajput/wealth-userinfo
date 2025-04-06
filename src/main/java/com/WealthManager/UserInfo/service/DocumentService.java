package com.WealthManager.UserInfo.service;


import com.WealthManager.UserInfo.data.dao.DocumentEntity;
import com.WealthManager.UserInfo.data.model.SuccessResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    SuccessResponse uploadFile(DocumentEntity documentEntity, List<MultipartFile> files) throws IOException;

    SuccessResponse listOfFiles(DocumentEntity documentEntity);

    SuccessResponse deleteFile(DocumentEntity documentEntity, String fileName);

    SuccessResponse getDownloadUrls(DocumentEntity documentEntity);

    SuccessResponse uploadProfilePhoto(DocumentEntity documentEntity, MultipartFile file);

    SuccessResponse getProfilePhotoUrl(DocumentEntity documentEntity);

    SuccessResponse uploadLabReport(DocumentEntity documentEntity, List<MultipartFile> files) throws IOException;

    SuccessResponse getLabReportsDownloadUrls(DocumentEntity documentEntity);
}
