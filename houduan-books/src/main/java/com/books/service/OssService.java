package com.books.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface OssService {
    //上传头像
    String uploadAvatar(MultipartFile file) throws IOException;
    //上传书籍封面
    String uploadBookCover(MultipartFile file) throws IOException;
}