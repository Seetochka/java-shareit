package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemRequestMapper itemRequestMapper;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final User mockUser = new User(1L, "User1", "1@user.com");
    private final ItemRequestDto.User mockUserDto = new ItemRequestDto.User(1L, "User1", "1@user.com");
    private final ItemRequestDto mockItemRequestDto = new ItemRequestDto(1L, "ItemRequestDesk", mockUserDto,
            LocalDateTime.now(), null);
    private final ItemRequest mockItemRequest = new ItemRequest(1L, "ItemRequestDesk", mockUser,
            LocalDateTime.now(), null);

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(Long.class), any()))
                .thenReturn(mockItemRequest);
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(mockItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(mockItemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testCreateItemRequestFailValidation() throws Exception {
        when(itemRequestService.createItemRequest(any(Long.class), any()))
                .thenThrow(new ValidationException("TestCreateItemRequestFailValidation",
                        "TestCreateItemRequestFailValidation"));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(mockItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Ошибка валидации: TestCreateItemRequestFailValidation")));
    }

    @Test
    void testGetItemRequestsByUserId() throws Exception {
        when(itemRequestService.getItemRequestsByUser(any(Long.class)))
                .thenReturn(List.of(mockItemRequest));
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(mockItemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testGetAllItemRequest() throws Exception {
        when(itemRequestService.getAllItemRequest(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockItemRequest));
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(mockItemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(any(Long.class), any(Long.class)))
                .thenReturn(mockItemRequest);
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mvc.perform(get("/requests/1")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(mockItemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(mockItemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(mockItemRequestDto.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
