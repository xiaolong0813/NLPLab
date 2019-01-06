package autocheck.services;

import autocheck.models.*;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.xm.Similarity;
import org.xm.similarity.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

@Service
public class ItemProcessService {
    private static final Logger logger= LogManager.getLogger(ItemProcessService.class);
    @Autowired
    private ParameterRepository parameterRepository;

    private Double getSimValue(String sent1, String sent2, Integer similarity_flag) {
//        int similarity_flag = parameterRepository.findByName("Similarity Algorithm").get(0).getValue().intValue();
        TextSimilarity similarity;
        switch (similarity_flag) {
            case 0:
                return Similarity.standardEditDistanceSimilarity(sent1, sent2);
            case 1:
                return Similarity.gregorEditDistanceSimilarity(sent1, sent2);
            case 2:
                return Similarity.editDistanceSimilarity(sent1, sent2);
            case 3:
                return Similarity.morphoSimilarity(sent1, sent2);
            case 4:
                similarity = new CosineSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 5:
                similarity = new EuclideanDistanceTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 6:
                similarity = new JaccardTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 7:
                similarity = new JaroDistanceTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 8:
                similarity = new JaroWinklerDistanceTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 9:
                similarity = new ManhattanDistanceTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 10:
                similarity = new SimHashPlusHammingDistanceTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            case 11:
                similarity = new DiceTextSimilarity();
                return similarity.getSimilarity(sent1, sent2);
            default:
                return 0.0;
        }
    }

    @Async
    public Future<List<String>> findSimilarDev(Deviation dev, List<XWPFParagraph> paragraphs, Integer model, Integer rfqVar, Integer simAlgo, Integer level) {
        Double simValue, maxValue;
        Integer startRow, endRow, maxStartRow, maxEndRow;
        String maxString, heading;
        ArrayList<String> list = new ArrayList<>();

        StringBuilder doc_text = new StringBuilder();
        startRow = 0;
        endRow = 0;

        maxStartRow = -1;
        maxEndRow = -1;
        maxValue = 0.0;
        maxString = "";
        heading = "";

        String dev_text;

        if (level == 0) {
            // Match with Paragraph
            dev_text = dev.getRfq_content_cn();
            while (startRow < paragraphs.size()) {
                while (endRow < paragraphs.size() && doc_text.toString().length() < dev_text.length()) {
                    doc_text.append(paragraphs.get(endRow).getText());
                    endRow += 1;
                }
                simValue = getSimValue(doc_text.toString(), dev_text, simAlgo);
                if (simValue > maxValue) {
                    maxValue = simValue;
                    maxStartRow = startRow;
                    maxEndRow = endRow;
                    maxString = doc_text.toString();
                }
                startRow += 1;
                endRow = startRow;
                doc_text = new StringBuilder();
            }
        } else {
            // Match with Sentence
            dev_text = dev.getRfq_keysent_cn();
            String paragraph_text;
            String sentence_text;
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

            Integer rowNo = 0;

            for (XWPFParagraph paragraph:paragraphs) {
                // Split paragraph into sentences
                paragraph_text = paragraph.getText();
                CoreDocument para_doc = new CoreDocument(paragraph_text);
                pipeline.annotate(para_doc);
                List<CoreSentence> sentences = para_doc.sentences();
                Integer startNo, endNo;
                StringBuilder sent_text = new StringBuilder();
                startNo = 0;
                endNo = 0;

                while (startNo < sentences.size()) {
                    while (endNo < sentences.size() && sent_text.toString().length() < dev_text.length()) {
                        sent_text.append(sentences.get(endNo).text());
                        endNo += 1;
                    }
                    simValue = getSimValue(sent_text.toString(), dev_text, simAlgo);
                    if (simValue > maxValue) {
                        maxValue = simValue;
                        maxStartRow = rowNo;
                        maxEndRow = rowNo;
                        maxString = sent_text.toString();
                    }
                    startNo += 1;
                    endNo = startNo;
                    sent_text = new StringBuilder();
                }
                rowNo += 1;
            }
        }

        // Get Heading
        for (int i = maxStartRow; i > 0; --i) {
            if (paragraphs.get(i).getStyle() != null && paragraphs.get(i).getStyle().contains("Heading")) {
                heading = paragraphs.get(i).getText();
                break;
            }
        }

        list.add(heading);
        list.add(dev_text);
        list.add(maxString);
        list.add(dev.getDev_content_cn());
        list.add(dev.getCategory());
        switch (model) {
            case 0:
                list.add(dev.getCost_1().toString());
                break;
            case 1:
                list.add(dev.getCost_2().toString());
                break;
            case 2:
                list.add(dev.getCost_3().toString());
                break;
            case 3:
                list.add(dev.getCost_4().toString());
                break;
            case 4:
                list.add(dev.getCost_5().toString());
                break;
            default:
                list.add(dev.getCost_1().toString());
        }
        list.add(dev.getTeam());
        list.add(dev.getLink_support_doc());
        list.add(maxValue.toString());

        list.add(maxStartRow.toString());
        list.add(maxEndRow.toString());

        return new AsyncResult<>(list);
    }


    @Async
    public Future<String> checkSentence(String new_sentence, List<Sentence> old_sentences) {
        Double simValue;
        for (Sentence sentence: old_sentences) {
            simValue = getSimValue(new_sentence, sentence.getText(), 4);
            if (simValue > 0.9) {
                return new AsyncResult<>("");
            }
        }
        return new AsyncResult<>(new_sentence);
    }
}
