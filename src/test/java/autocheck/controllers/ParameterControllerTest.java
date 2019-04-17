package autocheck.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import autocheck.models.Message;
import autocheck.models.Parameter;
import autocheck.models.ParameterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ParameterController.class)
public class ParameterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParameterRepository parameterRepository;

    private Parameter parameter;
    private List<Parameter> parameters;
    private List<Parameter> new_parameters;

    private String api_url = "/api/parameters";
    private String content_type = "application/json;charset=UTF-8";

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }


    @Before
    public void setUp() {
        // 模拟数据
        Parameter parameter1 = new Parameter();
        parameter1.setId(0L);
        parameter1.setName("Check New Sentences");
        parameter1.setValue(0D);

        Parameter parameter2 = new Parameter();
        parameter2.setId(1L);
        parameter2.setName("Similarity Algorithm");
        parameter2.setValue(0D);

        this.parameter = new Parameter();
        this.parameter.setId(2L);
        this.parameter.setName("Test Parameter");
        this.parameter.setValue(0D);

        this.parameters = Arrays.asList(parameter1, parameter2);
        this.new_parameters = Arrays.asList(parameter1, parameter2, this.parameter);
    }

    @Test
    public void getParams() throws Exception{
        // 模拟Repository行为
        when(parameterRepository.findAll()).thenReturn(this.parameters);

        // 模拟API请求
        MvcResult mvcResult = this.mockMvc.perform(get(this.api_url))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(this.content_type))
                                        .andReturn();
        Parameter[] parameter_list = this.mapFromJson(mvcResult.getResponse().getContentAsString(), Parameter[].class);

        // 验证List长度是否为2
        assertThat(parameter_list).hasSize(2);
    }

    @Test
    public void addParam() throws Exception {
        // 模拟Repository行为
        when(parameterRepository.findByName(this.parameter.getName())).thenReturn(new ArrayList<>());
        when(parameterRepository.save(this.parameter)).thenReturn(this.parameter);
        when(parameterRepository.findAll()).thenReturn(this.new_parameters);

        // 模拟API请求
        String inputJson = this.mapToJson(this.parameter);
        MvcResult mvcResult = this.mockMvc.perform(post(this.api_url).contentType(this.content_type).content(inputJson)).andReturn();

        Message message = this.mapFromJson(mvcResult.getResponse().getContentAsString(), Message.class);
        // 验证状态码
        assertThat(message.getStatus_code()).isEqualTo(200);
        Parameter[] parameterList = this.mapFromJson(this.mapToJson(message.getData()), Parameter[].class);
        // 验证是否List长度是否为3，即添加成功
        assertThat(parameterList).hasSize(3);
    }

    @Test
    public void updatePara() throws Exception {
        // 模拟Repository行为
        when(parameterRepository.findById(this.parameter.getId())).thenReturn(java.util.Optional.ofNullable(this.parameter));
        this.parameter.setValue(1D);
        when(parameterRepository.save(this.parameter)).thenReturn(this.parameter);
        when(parameterRepository.findAll()).thenReturn(this.new_parameters);

        // 模拟API请求
        String inputJson = this.mapToJson(this.parameter);
        MvcResult mvcResult = this.mockMvc.perform(post(this.api_url).contentType(this.content_type).content(inputJson)).andReturn();

        Message message = this.mapFromJson(mvcResult.getResponse().getContentAsString(), Message.class);
        // 验证状态码
        assertThat(message.getStatus_code()).isEqualTo(200);
        Parameter[] parameterList = this.mapFromJson(this.mapToJson(message.getData()), Parameter[].class);
        // 验证是否List长度是否为3
        assertThat(parameterList).hasSize(3);
        Parameter update_parameter = parameterList[2];
        // 验证是否更新成功
        assertThat(update_parameter.getValue()).isEqualTo(1D);
    }
}