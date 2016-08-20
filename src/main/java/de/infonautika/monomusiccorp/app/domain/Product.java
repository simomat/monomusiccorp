package de.infonautika.monomusiccorp.app.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.infonautika.monomusiccorp.app.serialize.ItemIdSerializer;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private String id;
    @Basic
    private String artist;
    @Basic
    private String title;

    public static Product create(String artist, String title) {
        Product product = new Product();
        product.setArtist(artist);
        product.setTitle(title);
        return product;
    }

    @JsonSerialize(using = ItemIdSerializer.class)
    public ItemId getItemId() {
        return new ItemId(id);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
