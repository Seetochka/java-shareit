package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final ItemDto mockItemDto = new ItemDto(1L, "Item", "ItemDesc", true, 1L, null, null,
            new ItemDto.Booking(2L, 4L, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(8)),
            null);
    private final CommentDto mockCommentDto = new CommentDto(1L, "Comment", "User",
            LocalDateTime.now());

    @Test
    void testCreateItemWithoutUser() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(mockItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItemWithoutName() throws Exception {
        ItemDto mockItemDtoWithoutName = new ItemDto(1L, "", "ItemDesc", true, 1L, null, null,
                new ItemDto.Booking(2L, 4L, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(8)),
                null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(mockItemDtoWithoutName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchItemByTextIncorrectSize() throws Exception {
        mvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("text", "searchText")
                        .queryParam("from", "0")
                        .queryParam("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchItemByTextIncorrectFrom() throws Exception {
        mvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("text", "searchText")
                        .queryParam("from", "-1")
                        .queryParam("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByUserIdIncorrectSize() throws Exception {
        mvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "0")
                        .queryParam("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllByUserIdIncorrectFrom() throws Exception {
        mvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCommentWithoutUser() throws Exception {
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(mockCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCommentWithoutText() throws Exception {
        CommentDto mockCommentDtoWithoutText = new CommentDto(1L, "", "User",
                LocalDateTime.now());

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(mockCommentDtoWithoutText))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }
}
