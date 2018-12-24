package autocheck.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Deviation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private Long doc_id;
    private String chapter_var_a;
    private String chapter_var_b;
    private String chapter_var_c;
    private String chapter_var_d;

    @Lob
    private String rfq_content_cn;
    @Lob
    private String rfq_keysent_cn;
    @Lob
    private String rfq_content_en;
    @Lob
    private String dev_content_cn;
    @Lob
    private String contract_wording_cn;

    private String category;
    private Double cost_1;
    private Double cost_2;
    private Double cost_3;
    private Double cost_4;
    private Double cost_5;
    private String team;
    private String link_support_doc;

    private String spare_1;
    private String spare_2;
    private String spare_3;
    private String spare_4;
    private String spare_5;

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

    public String getChapter_var_a() {
        return chapter_var_a;
    }

    public void setChapter_var_a(String chapter_var_a) {
        this.chapter_var_a = chapter_var_a;
    }

    public String getChapter_var_b() {
        return chapter_var_b;
    }

    public void setChapter_var_b(String chapter_var_b) {
        this.chapter_var_b = chapter_var_b;
    }

    public String getChapter_var_c() {
        return chapter_var_c;
    }

    public void setChapter_var_c(String chapter_var_c) {
        this.chapter_var_c = chapter_var_c;
    }

    public String getChapter_var_d() {
        return chapter_var_d;
    }

    public void setChapter_var_d(String chapter_var_d) {
        this.chapter_var_d = chapter_var_d;
    }

    public String getRfq_content_cn() {
        return rfq_content_cn;
    }

    public void setRfq_content_cn(String rfq_content_cn) {
        this.rfq_content_cn = rfq_content_cn;
    }

    public String getRfq_keysent_cn() {
        return rfq_keysent_cn;
    }

    public void setRfq_keysent_cn(String rfq_keysent_cn) {
        this.rfq_keysent_cn = rfq_keysent_cn;
    }

    public String getRfq_content_en() {
        return rfq_content_en;
    }

    public void setRfq_content_en(String rfq_content_en) {
        this.rfq_content_en = rfq_content_en;
    }

    public String getDev_content_cn() {
        return dev_content_cn;
    }

    public void setDev_content_cn(String dev_content_cn) {
        this.dev_content_cn = dev_content_cn;
    }

    public String getContract_wording_cn() {
        return contract_wording_cn;
    }

    public void setContract_wording_cn(String contract_wording_cn) {
        this.contract_wording_cn = contract_wording_cn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getCost_1() {
        return cost_1;
    }

    public void setCost_1(Double cost_1) {
        this.cost_1 = cost_1;
    }

    public Double getCost_2() {
        return cost_2;
    }

    public void setCost_2(Double cost_2) {
        this.cost_2 = cost_2;
    }

    public Double getCost_3() {
        return cost_3;
    }

    public void setCost_3(Double cost_3) {
        this.cost_3 = cost_3;
    }

    public Double getCost_4() {
        return cost_4;
    }

    public void setCost_4(Double cost_4) {
        this.cost_4 = cost_4;
    }

    public Double getCost_5() {
        return cost_5;
    }

    public void setCost_5(Double cost_5) {
        this.cost_5 = cost_5;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getLink_support_doc() {
        return link_support_doc;
    }

    public void setLink_support_doc(String link_support_doc) {
        this.link_support_doc = link_support_doc;
    }

    public String getSpare_1() {
        return spare_1;
    }

    public void setSpare_1(String spare_1) {
        this.spare_1 = spare_1;
    }

    public String getSpare_2() {
        return spare_2;
    }

    public void setSpare_2(String spare_2) {
        this.spare_2 = spare_2;
    }

    public String getSpare_3() {
        return spare_3;
    }

    public void setSpare_3(String spare_3) {
        this.spare_3 = spare_3;
    }

    public String getSpare_4() {
        return spare_4;
    }

    public void setSpare_4(String spare_4) {
        this.spare_4 = spare_4;
    }

    public String getSpare_5() {
        return spare_5;
    }

    public void setSpare_5(String spare_5) {
        this.spare_5 = spare_5;
    }
}
