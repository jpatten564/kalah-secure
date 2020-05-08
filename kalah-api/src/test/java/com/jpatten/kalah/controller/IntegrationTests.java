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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpatten.kalah.model.GameStatus;
import com.jpatten.kalah.model.dto.GameDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Stack;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
@WithMockUser(username = "user", roles = { "user" })
public class IntegrationTests {

    private static final int RANDOMIZED_GAME_COUNT = 20;
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void game_runs_from_start_to_finish() throws Exception {

        GameDto game = createGame();

        while (game.getGameStatus() != GameStatus.FINISHED) {
            game = tryPlaysUntilSuccess(game, false);
        }
    }

    @Test
    public void game_runs_from_start_to_finish_randomized() throws Exception {

        for (int i = 0; i < RANDOMIZED_GAME_COUNT; i++) {
            GameDto game = createGame();

            while (game.getGameStatus() != GameStatus.FINISHED) {
                game = tryPlaysUntilSuccess(game, true);
            }
        }
    }

    private GameDto tryPlaysUntilSuccess(GameDto game, boolean randomize) {

        Stack<Integer> testSequence = getTestSequence(game);
        if (randomize) {
            Collections.shuffle(testSequence);
        }

        Optional<GameDto> result = Optional.empty();

        while (!result.isPresent()) {
            result = tryPlay(game.getId(), testSequence.pop());
        }

        return result.orElseThrow(() -> new RuntimeException("No valid moves found"));
    }

    private Stack<Integer> getTestSequence(GameDto game) {
        Stack<Integer> testSequence = new Stack<>();

        testSequence.addAll(rangeClosed(
                getStartIndex(game), getEndIndex(game))
                .boxed()
                .sorted(Comparator.reverseOrder())
                .collect(toList()));
        return testSequence;
    }

    private int getStartIndex(GameDto game) {
        return game.getGameStatus() == GameStatus.PLAYER_ONE_TURN ? 1 : 8;
    }

    private int getEndIndex(GameDto game) {
        return game.getGameStatus() == GameStatus.PLAYER_ONE_TURN ? 6 : 13;
    }

    private Optional<GameDto> tryPlay(String gameId, int pitIndex) {
        try {
            MvcResult mvcResult = playPit(gameId, pitIndex).andReturn();

            if (mvcResult.getResponse().getStatus() == 200) {
                return Optional.of(parseResponse(mvcResult, GameDto.class));
            }
        }
        catch (Exception e) {
            // Ignore, assume we made an invalid move
        }
        return Optional.empty();
    }

    private GameDto createGame() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/games")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(201))
                .andReturn();

        return parseResponse(mvcResult, GameDto.class);
    }

    private ResultActions playPit(String gameId, int pitId) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.post(String.format("/games/%s/pits/%s", gameId, pitId))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    private static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
