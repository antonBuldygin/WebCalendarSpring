package webCalendarSpring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webCalendarSpring.repositories.BookRepository;

import webCalendarSpring.entities.BookEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookRepository bookRepository;
@Autowired
    public BookController(BookRepository bookEntityRepository) {
        this.bookRepository = bookEntityRepository;
    }

    @GetMapping("/all")
    public List<BookEntity> findAll(){

     return     bookRepository.findAll();


    }

    @PostMapping("/new_book")
    public BookEntity saveBook(@RequestBody BookEntity book){
        System.out.println(book);
        return bookRepository.save(book);
    }

    @PostMapping("/new_file")
    public ResponseEntity<byte[]> saveFile(@RequestParam("file") MultipartFile file){

        List<BookEntity> all = bookRepository.findAll();
        try {
            all.get(0).setPic(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + StringUtils.cleanPath(file.getOriginalFilename() + "\""))
                .contentType(MediaType.valueOf(file.getContentType())).body(all.get(0).getPic());
    }

}
