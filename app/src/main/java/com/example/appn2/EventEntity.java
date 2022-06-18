package com.example.appn2;

import android.graphics.Bitmap;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventEntity {

    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private Integer review;
    private String title;
    private Bitmap image;

    public EventEntity(Integer id, LocalDate date, LocalTime time, Integer review, String title, Bitmap image) {
        this.setId(id);
        this.setDate(date);
        this.setTime(time);
        this.setReview(review);
        this.setTitle(title);
        this.setImage(image);
    }

    public EventEntity() {
    }

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public LocalTime getTime(){
        return this.time;
    }

    public void setTime(LocalTime time){
        this.time = time;
    }

    public Integer getReview(){
        return this.review;
    }

    public void setReview(Integer review){
        this.review = review;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Bitmap getImage(){
        return this.image;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public static EventEntityBuilder builder() {
        return new EventEntityBuilder();
    }

    public static final class EventEntityBuilder {
        private Integer id;
        private LocalDate date;
        private LocalTime time;
        private Integer review;
        private String title;
        private Bitmap image;

        private EventEntityBuilder() {
        }

        public static EventEntityBuilder anEventEntity() {
            return new EventEntityBuilder();
        }

        public EventEntityBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public EventEntityBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public EventEntityBuilder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public EventEntityBuilder review(Integer review) {
            this.review = review;
            return this;
        }

        public EventEntityBuilder title(String title) {
            this.title = title;
            return this;
        }

        public EventEntityBuilder image(Bitmap image) {
            this.image = image;
            return this;
        }

        public EventEntity build() {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setId(id);
            eventEntity.setDate(date);
            eventEntity.setTime(time);
            eventEntity.setReview(review);
            eventEntity.setTitle(title);
            eventEntity.setImage(image);
            return eventEntity;
        }
    }
}
