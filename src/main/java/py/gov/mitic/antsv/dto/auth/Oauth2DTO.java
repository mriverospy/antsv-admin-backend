package py.gov.mitic.htv.dto.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Oauth2DTO {

    private String headTyp;

    private String headAlg;

    private String sub;

    private String iss;

    private Integer exp;

    public Oauth2DTO(){}

    public Oauth2DTO(String header, String payload) throws JsonProcessingException {
        Map<String, Object> dataHeader = new ObjectMapper().readValue(header, new TypeReference<Map<String, Object>>() {});
        if(dataHeader != null) {
            this.headAlg = (String) dataHeader.get("alg");
            this.headTyp = (String) dataHeader.get("typ");
        }
        Map<String, Object> dataBody = new ObjectMapper().readValue(payload, new TypeReference<Map<String, Object>>() {});
        if(dataBody != null) {
            this.sub = (String) dataBody.get("sub");
            this.iss = (String) dataBody.get("iss");
            this.exp = (Integer) dataBody.get("exp");
        }
    }

}
