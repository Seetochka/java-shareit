package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto mockItemRequestDto = new ItemRequestDto(1L, "ItemRequestDesk", 1L,
            LocalDateTime.now());

    @Test
    void testCreateItemRequestWithoutUser() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(mockItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItemRequestWithoutDesc() throws Exception {
        ItemRequestDto mockItemRequestDtoWithoutDesc = new ItemRequestDto(1L, "", 1L,
                LocalDateTime.now());

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(mockItemRequestDtoWithoutDesc))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllItemRequestWithoutUser() throws Exception {
        mvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllItemRequestIncorrectSize() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "0")
                        .queryParam("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllItemRequestIncorrectFrom() throws Exception {
        mvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "10"))
                .andExpect(status().isBadRequest());
    }
}
