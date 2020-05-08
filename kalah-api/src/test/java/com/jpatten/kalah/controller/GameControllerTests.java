/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jpatten.kalah.controller;

import com.jpatten.kalah.exception.GameNotFoundException;
import com.jpatten.kalah.exception.IllegalMoveException;
import com.jpatten.kalah.model.Game;
import com.jpatten.kalah.model.GameStatus;
import com.jpatten.kalah.model.dto.GameDto;
import com.jpatten.kalah.model.dto.PitStatus;
import com.jpatten.kalah.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = GameController.class, secure = false)
public class GameControllerTests {

    @MockBean
    private GameService gameService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void create_returns_new_game() throws Exception {

        GameDto gameDto = getGameDto();

        when(gameService.createGame(any(), any(), any())).thenReturn(gameDto);

        MockHttpServletRequestBuilder post = post("/games")
                .principal(mockPrincipal())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        this.mockMvc.perform(post)
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.uri").value("http://localhost/games/1"))
                .andExpect(jsonPath("$.gameStatus").value("PLAYER_ONE_TURN"));
    }

    private Principal mockPrincipal() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("user");
        return mockPrincipal;
    }

    private GameDto getGameDto() {
        return new GameDto(1L, GameStatus.PLAYER_ONE_TURN, "http://localhost/games/1", getPitStatus());
    }

    @Test
    public void play_returns_ok_given_valid_game_and_pit() throws Exception {
        GameDto gameDto = getGameDto();

        when(gameService.play(any(), any(), any())).thenReturn(gameDto);

        this.mockMvc.perform(getPlayRequest())
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.uri").value("http://localhost/games/1"))
                .andExpect(jsonPath("$.gameStatus").value("PLAYER_ONE_TURN"));
    }

    @Test
    public void play_returns_404_when_game_not_found() throws Exception {
        when(gameService.play(any(), any(), any())).thenThrow(new GameNotFoundException("Game not found"));

        this.mockMvc.perform(getPlayRequest())
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void play_returns_403_when_illegal_move() throws Exception {
        when(gameService.play(any(), any(), any())).thenThrow(new IllegalMoveException("Illegal Move"));

        this.mockMvc.perform(getPlayRequest())
                .andDo(print())
                .andExpect(status().is(403));
    }

    private MockHttpServletRequestBuilder getPlayRequest() {
        return MockMvcRequestBuilders.post("/games/1/pits/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    public void get_returns_200_when_game_found() throws Exception {
        when(gameService.findById(anyLong(), any())).thenReturn(getGameDto());

        this.mockMvc.perform(getRequest())
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    public void getGame_returns_404_when_game_not_found() throws Exception {
        when(gameService.findById(anyLong(), any())).thenThrow(GameNotFoundException.class);

        this.mockMvc.perform(getRequest())
                .andDo(print())
                .andExpect(status().is(404));
    }

    private MockHttpServletRequestBuilder getRequest() {
        return MockMvcRequestBuilders.get("/games/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    public void getAll_returns_200() throws Exception {
        when(gameService.findAll(any())).thenReturn(Collections.singletonList(getGameDto()));

        this.mockMvc.perform(getAllRequest())
                .andDo(print())
                .andExpect(status().is(200));
    }

    private MockHttpServletRequestBuilder getAllRequest() {
        return MockMvcRequestBuilders.get("/games/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private PitStatus getPitStatus() {
        return PitStatus.fromGame(Game.createGame("player1", "player2"));
    }

}
