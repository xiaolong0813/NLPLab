package autocheck.services;

import autocheck.models.*;
import edu.stanford.nlp.pipeline.CoreSentence;
import org.apache.commons.lang3.CharUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.common.Levenshtein;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.xm.Similarity;
import org.xm.similarity.text.*;

import java.util.*;
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
            case 12:
                return 1-Levenshtein.distance(sent1,sent2)*1.0/Math.max(sent1.length(), sent2.length());
            default:
                return 0.0;
        }
    }

    private String cleanSent(String sent) {
        int pos = 0;
        int len = sent.length();
        while (pos < len && (sent.charAt(pos) == '.' || sent.charAt(pos) == ' ' || sent.charAt(pos) == '(' || sent.charAt(pos) == ')' || CharUtils.isAsciiNumeric(sent.charAt(pos)) )) {
            ++pos;
        }
        return sent.substring(pos);
    }

    @Async
    public Future<List<String>> checkSimilarPara(String para_text, Iterable<Sentence> paras) {
        ArrayList<String> list = new ArrayList<>();
        Double maxValue = 0.0;
        Integer simAlgo = parameterRepository.findByName("Similarity Algorithm").get(0).getValue().intValue();
        for (Sentence para:paras) {
            maxValue = Math.max(maxValue, getSimValue(cleanSent(para.toString()), cleanSent(para_text), simAlgo));
        }
        list.add(para_text);
        if (maxValue > 0.9) {
            list.add("True");
            return new AsyncResult<>(list);
        }
        else {
            list.add("False");
            return new AsyncResult<>(list);
        }
    }

    @Async
    public Future<List<String>> findSimilarDev(Deviation dev, List<CoreSentence> sentences, List<String> headings, Integer model, Integer rfqVar, Integer simAlgo, Integer level) {
        Double simValue, maxValue;
        Integer maxStartRow, maxEndRow;
        String maxString, heading;
        ArrayList<String> list = new ArrayList<>();
        maxStartRow = -1;
        maxEndRow = -1;
        maxValue = -1.0;
        maxString = "";
        heading = "";

        String dev_text;

        if (level == 0) {
            // Match with Paragraph
            dev_text = dev.getRfq_content_cn();
        } else {
            // Match with Sentence
            dev_text = dev.getRfq_keysent_cn();
        }
        Integer startNo, endNo;
        StringBuilder sent_text = new StringBuilder();
        startNo = 0;
        endNo = 0;
        while (startNo < sentences.size()) {
            while (endNo < sentences.size() && sent_text.toString().length() < dev_text.length()) {
                sent_text.append(sentences.get(endNo).text());
                endNo += 1;
            }
            simValue = getSimValue(cleanSent(dev_text), cleanSent(sent_text.toString()), simAlgo);
            if (simValue > maxValue) {
                maxValue = simValue;
                maxStartRow = startNo;
                maxEndRow = endNo;
                maxString = sent_text.toString();
                heading = headings.get(startNo);
            }
            startNo += 1;
            endNo = startNo;
            sent_text = new StringBuilder();
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
