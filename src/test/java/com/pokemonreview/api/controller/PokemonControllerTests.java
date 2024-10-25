package com.pokemonreview.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemonreview.api.controllers.PokemonController;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.PokemonResponse;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.service.PokemonService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PokemonController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PokemonControllerTests {
    @Autowired
    private MockMvc mockMvc;


    @MockBean   // mockbean is for only springboot, for quarksu it wont work
    private PokemonService pokemonService;

    @Autowired
    private ObjectMapper objectMapper;
    private Pokemon pokemon;
    private Review review;
    private ReviewDto reviewDto;
    private PokemonDto pokemonDto;

    @BeforeEach
    public void init() {
        pokemon = Pokemon.builder().name("pikachu").type("electric").build();
        pokemonDto = PokemonDto.builder().name("pickachu").type("electric").build();
        review = Review.builder().title("title").content("content").stars(5).build();
        reviewDto = ReviewDto.builder().title("review title").content("test content").stars(5).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void PokemonController_CreatePokemon_ReturnCreated() throws Exception {
        given(pokemonService.createPokemon(any())).willAnswer((invocation -> invocation.getArgument(0)));

        /**
         * getting error for using Mokcito.when method instead of Mockito.given
         */
        //Mocking an HTTP GET Request with MockMvc
        //when(pokemonService.getPokemonById(any())).thenReturn(any());
        ResultActions response = mockMvc.perform(post("/api/pokemon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto))); // while posting any data need to use content() method
// Assertions to Validate the Response
        response.andExpect(status().isCreated())
                //.andDo(MockMvcResultHandlers.print());   used to print the data
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",CoreMatchers.is(pokemonDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type",CoreMatchers.is(pokemonDto.getType())));

    }

    @Test
    void getAllPokemon_returResposne() throws Exception {
        PokemonResponse pokemonResponse = PokemonResponse.builder().pageSize(10).pageNo(0).content(Arrays.asList(pokemonDto)).build();
        when(pokemonService.getAllPokemon(0,10)).
                thenReturn(pokemonResponse);   //default value will be null for any() ,so we need to initliaze mockito in init() method

        ResultActions perform = mockMvc.perform(get("/api/pokemon")
//                .content(objectMapper.writeValueAsString(pokemonDto))
                .param("pageNo","0")
                .param("pagesize","10")
                .contentType(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isOk()).
                //andDo(print());
        andExpect(MockMvcResultMatchers.jsonPath("$.content.size()",CoreMatchers.is(pokemonResponse.getContent().size())));

    }

    @Test
    void getPokemonById_returnReponse() throws Exception {
        when(pokemonService.getPokemonById(1)).thenReturn(pokemonDto);

        ResultActions perform = mockMvc.perform(get("/api/pokemon/1")  // if we give url wrong ,will get 404 not found********
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto)));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.name",CoreMatchers.is(pokemonDto.getName())));


    }

    @Test
    void deletePokemon() throws Exception {
        doNothing().when(pokemonService).deletePokemonId(1);

        mockMvc.perform(delete("/api/pokemon/1/delete").
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
