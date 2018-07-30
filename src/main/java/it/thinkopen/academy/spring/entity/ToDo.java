package it.thinkopen.academy.spring.entity;

import it.thinkopen.academy.spring.Utils;
import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDateTime;

import static it.thinkopen.academy.spring.Utils.DEFAULT_DATETIME_FORMAT;

@Entity
@Table(name = "todo")
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Long date;

    @Column(name = "expiration")
    private Long expiration;

    @Column(name = "done")
    private Boolean done;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String toString() {
        final JSONObject jobj = new JSONObject();
        final LocalDateTime ldtDate = date != null ? Utils.toLocalDateTime(date) : null;
        final LocalDateTime ldtExp = expiration != null ? Utils.toLocalDateTime(expiration) : null;

        jobj.put("id", id);
        jobj.put("title", title);
        jobj.put("content", content);
        jobj.put("date", ldtDate != null ? DEFAULT_DATETIME_FORMAT.format(ldtDate) : null);
        jobj.put("exp",  ldtExp != null ? DEFAULT_DATETIME_FORMAT.format(ldtExp) : null);
        jobj.put("done", done);

        return jobj.toString();
    }
}
