package br.mpmt.sanzio.learningSpringboot.learningspringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private static final String UPLOAD_ROOT = "/home/rporto/upload-dir";

    private final ImageRepository repository;
    private final ResourceLoader loader;

    @Autowired
    public ImageService(ImageRepository repository, ResourceLoader loader) {
        this.repository = repository;
        this.loader = loader;
        try {
            setUp(repository).run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Page<Image> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Resource findOneImage(String fileName) {
        return loader.getResource("file:" + UPLOAD_ROOT + "/" + fileName);
    }

    public void createImage(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new Image(file.getOriginalFilename()));
        }
    }

    public void deleteImage(String fileName) throws IOException {
        final Image byName = repository.findByName(fileName);
        repository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
    }

    @Bean
    public CommandLineRunner setUp(ImageRepository repository) throws IOException {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            FileCopyUtils.copy("Test file1", new FileWriter(UPLOAD_ROOT + "/test1"));
            repository.save(new Image("test1"));

            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new Image("test2"));

            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
            repository.save(new Image("test3"));
        };
    }

}
