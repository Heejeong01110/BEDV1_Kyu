package org.prgrms.kyu.controller.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.AuthenticationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgrms.kyu.dto.JoinRequest;
import org.prgrms.kyu.dto.StoreCreateRequest;
import org.prgrms.kyu.entity.User;
import org.prgrms.kyu.repository.UserRepository;
import org.prgrms.kyu.service.StoreService;
import org.prgrms.kyu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class StoreRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StoreService storeService;

  @Autowired
  private UserService userService;

  @Autowired
  UserRepository userRepository;


  @AfterEach
  public void cleanUp(){
    storeService.deleteAll();
    userRepository.deleteAll();
  }



  @Test
  @DisplayName("음식점을 생성할 수 있다.")
  public void saveStoreTest() throws Exception {
    Long userId = userService.join(
        new JoinRequest(
            "test1@test.com",
            "1234",
            "user",
            "nick",
            "Seoul",
            "STORE_OWNER"));

    StoreCreateRequest storeCreateRequest =
        new StoreCreateRequest(
          "momstouch",
          "01011112222",
          "i am momstouch.",
          "Seoul",
          userId);

    mockMvc.perform(post("/api/v1/stores")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(storeCreateRequest)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("store-save",
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                fieldWithPath("telephone").type(JsonFieldType.STRING).description("telephone"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description"),
                fieldWithPath("location").type(JsonFieldType.STRING).description("location"),
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("userId")
            ),
            responseFields(
                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                fieldWithPath("data").type(JsonFieldType.NUMBER).description("게시글 id"),
                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("응답시간")
            )
        ));
  }

  @Test
  @DisplayName("음식점을 생성할 수 있다.")
  public void getAllStoreTest() throws Exception {
    Long userId = userService.join(
        new JoinRequest(
            "test1@test.com",
            "1234",
            "user",
            "nick",
            "Seoul",
            "STORE_OWNER"));

    StoreCreateRequest storeCreateRequest =
        new StoreCreateRequest(
            "momstouch",
            "01011112222",
            "i am momstouch.",
            "Seoul",
            userId);
    StoreCreateRequest storeCreateRequest2 =
        new StoreCreateRequest(
            "momstouch2",
            "01011113333",
            "i am momstouch2.",
            "Seoul",
            userId);

    storeService.save(storeCreateRequest);
    storeService.save(storeCreateRequest2);

    mockMvc.perform(get("/api/v1/stores")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("store-find-all",
            responseFields(
                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("응답시간"),

                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("데이터"),
                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("id"),
                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("name"),
                fieldWithPath("data[].telephone").type(JsonFieldType.STRING).description("telephone"),
                fieldWithPath("data[].description").type(JsonFieldType.STRING).description("description"),
                fieldWithPath("data[].location").type(JsonFieldType.STRING).description("location")
            )
        ));
  }



  @Test
  @DisplayName("id로 가게 정보를 검색할 수 있다.")
  public void getOneStoreTest() throws Exception {
    Long userId = userService.join(
        new JoinRequest(
            "test1@test.com",
            "1234",
            "user",
            "nick",
            "Seoul",
            "STORE_OWNER"));

    StoreCreateRequest storeCreateRequest =
        new StoreCreateRequest(
            "momstouch",
            "01011112222",
            "i am momstouch.",
            "Seoul",
            userId);
    Long storeId = storeService.save(storeCreateRequest);

    mockMvc.perform(get("/api/v1/stores/{id}",storeId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("store-find-by-id",
            responseFields(
                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
                fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("응답시간"),

                fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("id"),
                fieldWithPath("data.name").type(JsonFieldType.STRING).description("name"),
                fieldWithPath("data.telephone").type(JsonFieldType.STRING).description("telephone"),
                fieldWithPath("data.description").type(JsonFieldType.STRING).description("description"),
                fieldWithPath("data.location").type(JsonFieldType.STRING).description("location")
            )
        ));
  }

}
