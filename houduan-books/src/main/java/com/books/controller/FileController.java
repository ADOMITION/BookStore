package com.books.controller;

import com.books.common.Result;
import com.books.service.BookService;
import com.books.service.OssService;
import com.books.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/file")
public class FileController {

    private final OssService ossService;
    private final UserService userService;
    private final BookService bookService;

    public FileController(OssService ossService, UserService userService, BookService bookService) {
        this.ossService = ossService;
        this.userService = userService;
        this.bookService = bookService;
    }

    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file,
                               @RequestParam("userId") Integer userId) {
        try {
            // 验证文件类型
            if (!file.getContentType().startsWith("image/")) {
                return Result.fail("仅支持图片格式");
            }

            // 上传到OSS
            String avatarUrl = ossService.uploadAvatar(file);

            // 更新用户信息
            userService.updateAvatar(userId, avatarUrl);

            return Result.suc(avatarUrl);
        } catch (IOException e) {
            return Result.fail("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/uploadBookCover")
    public Result uploadBookCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookId") Integer bookId
    ) {
        try {
            // 验证文件类型
            if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                return Result.fail("仅支持图片格式");
            }
            // 上传到OSS
            String coverUrl = ossService.uploadBookCover(file);

            // 更新书籍信息
            bookService.updateCover(bookId, coverUrl);

            return Result.suc(coverUrl);
        } catch (IOException e) {
            return Result.fail("上传失败：" + e.getMessage());
        }
    }

}