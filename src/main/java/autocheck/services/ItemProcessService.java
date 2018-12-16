package autocheck.services;

import autocheck.models.*;
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
import java.util.concurrent.Future;

@Service
public class ItemProcessService {
    private static final Logger logger= LogManager.getLogger(ItemProcessService.class);
    @Autowired
    private ParameterRepository parameterRepository;

    private Double getSimValue(String sent1, String sent2) {
        int similarity_flag = parameterRepository.findByName("Similarity Algorithm").get(0).getValue().intValue();
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
    public Future<List<String>> findSimilarDev(String dev_text, String dev_content, String dev_type, Double threshold, List<XWPFParagraph> paragraphs) {

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

        while (startRow < paragraphs.size()) {
            while (endRow < paragraphs.size() && doc_text.toString().length() < dev_text.length()) {
                doc_text.append(paragraphs.get(endRow).getText());
                endRow += 1;
            }
            simValue = getSimValue(doc_text.toString(), dev_text);
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

        if (maxValue > threshold) {
            // Get Heading
            for (int i = maxStartRow; i > 0; --i) {
                if (paragraphs.get(i).getStyle() != null && paragraphs.get(i).getStyle().contains("Heading")) {
                    heading = paragraphs.get(i).getText();
                    break;
                }
            }
            list.add(heading);
            list.add(maxString);
            list.add(dev_text);
            list.add(dev_content);
            list.add(dev_type);
            list.add(maxStartRow.toString());
            list.add(maxEndRow.toString());
            return new AsyncResult<>(list);
        }
        return new AsyncResult<>(list);
    }


    @Async
    public Future<String> checkSentence(String new_sentence, List<Sentence> old_sentences) {
        Double simValue;
        for (Sentence sentence: old_sentences) {
            simValue = getSimValue(new_sentence, sentence.getText());
            if (simValue > 0.9) {
                return new AsyncResult<>("");
            }
        }
        return new AsyncResult<>(new_sentence);
    }
}
