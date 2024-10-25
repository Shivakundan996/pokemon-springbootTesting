package com.pokemonreview.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemonreview.api.controllers.ReviewController;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.service.ReviewService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ReviewControllerTests {

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewDto reviewDto;

    @BeforeEach
    void init(){
        reviewDto = ReviewDto.builder().title("test").stars(5).content("test").build();

    }

    @Test
    void createReviewTest() throws Exception {
        when(reviewService.createReview(1,reviewDto)).thenReturn(reviewDto);

        ResultActions perform = mockMvc.perform(post("/api/pokemon/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto)));

        perform.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title",CoreMatchers.is(reviewDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content",CoreMatchers.is(reviewDto.getContent())));
    }

    @Test
    void getReviewByPokemonId_Test() throws Exception {
       ReviewDto reviewDto = ReviewDto.builder().title("test").stars(5).content("test").build();
//use anyInt() for any() because any() by default takes null
        when(reviewService.getReviewsByPokemonId(anyInt())).thenReturn(Arrays.asList(reviewDto));


        //mocking the HTTP get request by mockMVC
        ResultActions perform = mockMvc.perform(get("/api/pokemon/1/reviews").
                contentType(MediaType.APPLICATION_JSON));

        //assertions to validate response
        perform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()",CoreMatchers.is(Arrays.asList(reviewDto).size())));

    }

}
