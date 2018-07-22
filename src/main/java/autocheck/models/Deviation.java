package autocheck.models;

import javax.persistence.*;

@Entity
public class Deviation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long doc_id;
    private String chapter;
    private String type;
    private String dev_type;

    @Lob
    private String content;

    @Lob
    private String d_content;

    private Long group_id;
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Long doc_id) {
        this.doc_id = doc_id;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDev_type() {
        return dev_type;
    }

    public void setDev_type(String dev_type) {
        this.dev_type = dev_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getD_content() {
        return d_content;
    }

    public void setD_content(String d_content) {
        this.d_content = d_content;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
