package br.mpmt.sanzio.learningSpringboot.learningspringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private ImageService imageService;

    public HomeController() {

    }

    @Autowired
    public HomeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value = "/")
    public String index(Model model, Pageable pageable) {
        final Page<Image> page = imageService.findPage(pageable);
        model.addAttribute("page", page);
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String filename) {
        try {
            Resource file = imageService.findOneImage(filename);
            return ResponseEntity.ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Couldn't find " + filename + " => " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    @ResponseBody
    public ResponseEntity<?> createFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws URISyntaxException {
        try {
            imageService.createImage(file);
            return ResponseEntity.created(new URI(request.getRequestURI()).resolve(file.getOriginalFilename() + "/raw"))
                    .body("Successfully upload " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            imageService.deleteImage(filename);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Successfully delete " + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed tp delete " + filename + " => " + e.getMessage());
        }
    }

}
