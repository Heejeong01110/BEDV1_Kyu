package org.prgrms.kyu.controller.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.prgrms.kyu.dto.JoinRequest;
import org.prgrms.kyu.dto.StoreCreateRequest;
import org.prgrms.kyu.dto.StoreFindResponse;
import org.prgrms.kyu.repository.UserRepository;
import org.prgrms.kyu.service.StoreService;
import org.prgrms.kyu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(StoreRestController.class)
class StoreRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private StoreService storeService;

  @MockBean
  private UserService userService;

  @MockBean
  UserRepository userRepository;


  @AfterEach
  public void cleanUp(){
    storeService.deleteAll();
    userRepository.deleteAll();
  }


  Long userId;
  JoinRequest form;
  StoreCreateRequest storeCreateRequest;
  StoreCreateRequest storeCreateRequest2;

  @BeforeEach
  public void userSetUp(){
    //given
    userId = 1L;
    form = new JoinRequest(
        "test1@test.com",
        "1234",
        "user",
        "nick",
        "Seoul",
        "STORE_OWNER");

    storeCreateRequest =
        new StoreCreateRequest(
            "momstouch",
            "01011112222",
            "i am momstouch.",
            "Seoul",
            userId);

    storeCreateRequest2 =
        new StoreCreateRequest(
            "momstouch2",
            "01011113333",
            "i am momstouch2.",
            "Seoul",
            userId);

    given(userService.join(ArgumentMatchers.any(JoinRequest.class))).willReturn(1L);
    userService.join(form);
  }



  @Test
  @DisplayName("음식점을 생성할 수 있다.")
  public void saveStoreTest() throws Exception {
    //given //when
    given(storeService.save(ArgumentMatchers.any(StoreCreateRequest.class))).willReturn(1L);

    //then
    mockMvc.perform(post("/api/v1/stores")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(storeCreateRequest)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("store-save",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
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
  @DisplayName("모든 음식점을 찾을 수 있다.")
  public void getAllStoreTest() throws Exception {
    //given
    given(storeService.save(ArgumentMatchers.any(StoreCreateRequest.class))).willReturn(1L);
    Long storeId1 = storeService.save(storeCreateRequest);
    given(storeService.save(ArgumentMatchers.any(StoreCreateRequest.class))).willReturn(2L);
    Long storeId2 = storeService.save(storeCreateRequest2);

    List<StoreFindResponse> list = List.of(
        new StoreFindResponse(
            storeId1,
            storeCreateRequest.getName(),
            storeCreateRequest.getTelephone(),
            storeCreateRequest.getDescription(),
            storeCreateRequest.getLocation()
        ),
        new StoreFindResponse(
            storeId2,
            storeCreateRequest2.getName(),
            storeCreateRequest2.getTelephone(),
            storeCreateRequest2.getDescription(),
            storeCreateRequest2.getLocation()
        ));


    //when
    given(storeService.findAll()).willReturn(list);

    //then
    mockMvc.perform(get("/api/v1/stores")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("store-find-all",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
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
    //given
    given(storeService.save(ArgumentMatchers.any(StoreCreateRequest.class))).willReturn(1L);
    Long storeId = storeService.save(storeCreateRequest);

    StoreFindResponse storeFindResponse = new StoreFindResponse(
        storeId,
        storeCreateRequest.getName(),
        storeCreateRequest.getTelephone(),
        storeCreateRequest.getDescription(),
        storeCreateRequest.getLocation()
    );

    //when
    given(storeService.findById(ArgumentMatchers.any(Long.class))).willReturn(storeFindResponse);

    //then
    mockMvc.perform(get("/api/v1/stores/{id}",storeId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("store-find-by-id",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
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
