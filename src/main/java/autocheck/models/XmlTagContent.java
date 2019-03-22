package autocheck.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
//import java.util.logging.LogManager;

@Entity
public class XmlTagContent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private Long id;
    private Long xmlId;

    @Lob
    private String tag;
    @Lob
    private String tagContent;
    @Lob
    private String tagTranslation;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagContent() {
        return tagContent;
    }

    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
    }

    public String getTagTranslation() {
        return tagTranslation;
    }

    public void setTagTranslation(String tagTranslation) {
        this.tagTranslation = tagTranslation;
    }

    public Long getXmlId() {
        return xmlId;
    }

    public void setXmlId(Long xmlId) {
        this.xmlId = xmlId;
    }
}
