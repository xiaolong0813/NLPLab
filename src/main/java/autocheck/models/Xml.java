package autocheck.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Xml {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String filename;
    private String filepath;
    private Integer status;

    @Lob
    private String xmlString;

    private String xmlTagIdArray;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString(String xmlString) {
        this.xmlString = xmlString;
    }

    public String getXmlTagIdArray() {
        return xmlTagIdArray;
    }

    public void setXmlTagIdArray(String xmlTagIdArray) {
        this.xmlTagIdArray = xmlTagIdArray;
    }

//    public List<Long> getXmlTagIdArray() {
//        return xmlTagIdArray;
//    }
//
//    public void setXmlTagIdArray(List<Long> xmlTagIdArray) {
//        this.xmlTagIdArray = xmlTagIdArray;
//    }
}
