package com.csi_rscoe.csi_backend.Controllers.Admin;

import com.csi_rscoe.csi_backend.Models.Blog;
import com.csi_rscoe.csi_backend.Repositories.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/blogs")
@CrossOrigin(origins = {"http://localhost:5173"})
public class AdminBlogController {

    @Autowired
    private BlogRepository blogRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Blog> getAll() { return blogRepository.findAll(); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Blog> create(@RequestBody Blog blog) {
        blog.setCreatedAt(new Date());
        Blog saved = blogRepository.save(blog);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Blog update(@PathVariable Long id, @RequestBody Blog updated) {
        Optional<Blog> existing = blogRepository.findById(id);
        if (existing.isPresent()) {
            Blog b = existing.get();
            b.setTitle(updated.getTitle());
            b.setContent(updated.getContent());
            b.setExcerpt(updated.getExcerpt());
            b.setAuthor(updated.getAuthor());
            b.setCategory(updated.getCategory());
            b.setReadTime(updated.getReadTime());
            b.setImage(updated.getImage());
            return blogRepository.save(b);
        }
        updated.setCreatedAt(new Date());
        return blogRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}


