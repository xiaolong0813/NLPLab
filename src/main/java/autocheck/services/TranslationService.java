package autocheck.services;

import autocheck.services.translation.TransApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    @Value("${translation.appid}")
    private String APP_ID;

    @Value("${translation.key}")
    private String SECURITY_KEY;

//    API doucment
//    http://fanyi-api.baidu.com/api/trans/product/apidoc

    public String translate(String source_query) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        return api.getTransResult(source_query, "en", "zh");
    }
}
