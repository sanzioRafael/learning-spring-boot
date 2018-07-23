package br.mpmt.sanzio.learningSpringboot.learningspringboot;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;


    private String name;

    private Image() { }

    public Image(String name) {
        this.name = name;
    }
}
