var keycloak = Keycloak();

function initKeycloak() {
  keycloak.init({ onLoad: 'login-required' })
  .success(function(authenticated) {
      console.log('Authenticated');
  }).error(function() {
      console.log('Failed to authenticate');
  });  
}

function createGame() {
  $.ajax({
      type: "POST",
      contentType: "application/json",
      url: '/api/games/',
      dataType: "json",
      headers: {
        'Authorization': 'Bearer ' + keycloak.token
      },
      success: function(game) {
          initBoard(game);
      }
  });
}

function initBoard(game) {
  for (var i = 1; i < 15; i++) {
      // listen for click events on all pits except player houses
      if (i % 7 !== 0) {
          attachClickListener(i, game);
      }
  }
  renderBoard(game);
}

function attachClickListener(i, game) {
  $('#pit' + i).click(function () {
      move(game, i);
  });
}

function move(game, pitIndex) {
  console.log('Moving ' + pitIndex);

  $.ajax({
      type: "POST",
      contentType: "application/json",
      url: '/api/games/' + game.id + '/pits/' + pitIndex,
      dataType: "json",
      headers: {
        'Authorization': 'Bearer ' + keycloak.token,
      },
      success: function(game) {
          console.log(game);
          renderBoard(game);
      }
  });
}

function renderBoard(game) {
  var gameStatus = game.gameStatus;

  for (var i = 1; i < 15; i++) {
      if (i % 7 !== 0) {
          $('#pit' + i).text(game.status[i]);

          setPitColour(game, i, gameStatus);
      }

  }
  $('#player1_house').text(game.status[7]);
  $('#player2_house').text(game.status[14]);
  $('#game_status').text(getFormattedGameStatus(game));
}

function setPitColour(game, pitIndex, gameStatus) {
  var pit = $('#pit' + pitIndex);
  if (game.status[pitIndex] !== '0' &&
      (gameStatus === 'PLAYER_ONE_TURN' && pitIndex < 7) ||
      (gameStatus === 'PLAYER_TWO_TURN' && pitIndex > 7)) {

      pit.css({'background-color': '#000000', 'cursor': 'pointer'});
  } else {
      pit.css({'background-color': '#CCCCCC', 'cursor': 'default'});
  }
}

function getFormattedGameStatus(game) {
  if (game.gameStatus === 'PLAYER_ONE_TURN') {
      return 'Player One\'s Turn';
  }
  else if (game.gameStatus === 'PLAYER_TWO_TURN') {
      return 'Player Two\'s Turn';
  }
  else if (game.gameStatus === 'FINISHED') {
      return 'Finished';
  }
  return game.status;
}

function logout() {
  keycloak.logout();
}

initKeycloak();
