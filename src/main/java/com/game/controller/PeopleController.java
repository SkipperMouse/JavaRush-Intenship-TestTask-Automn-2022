package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rest/players")
public class PeopleController {
    private final PlayersService playersService;
    private static final Logger log = Logger.getGlobal();
    @Autowired
    public PeopleController(PlayersService playersService) {
        this.playersService = playersService;
    }

    @GetMapping()
    public List<Player> getPlayerList(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) Race race,
                                      @RequestParam(required = false) Profession profession,
                                      @RequestParam(required = false) Long after,
                                      @RequestParam(required = false) Long before,
                                      @RequestParam(required = false) Boolean banned,
                                      @RequestParam(required = false) Integer minExperience,
                                      @RequestParam(required = false) Integer maxExperience,
                                      @RequestParam(required = false) Integer minLevel,
                                      @RequestParam(required = false) Integer maxLevel,
                                      @RequestParam(required = false) PlayerOrder order,
                                      @RequestParam(required = false) Integer pageNumber,
                                      @RequestParam(required = false) Integer pageSize
                                      ){
        List<Player> players = playersService.findPlayers(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel, order, pageNumber, pageSize, false);
        return players;
    }
    @GetMapping("/{id}")
    public @ResponseBody Player getPlayer(@PathVariable("id") long id) throws BadRequestException, PlayerNotFoundException {
        return playersService.findPlayerById(id);
    }


    @GetMapping("/count")
    public Integer getPlayersCount(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) Race race,
                                   @RequestParam(required = false) Profession profession,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean banned,
                                   @RequestParam(required = false) Integer minExperience,
                                   @RequestParam(required = false) Integer maxExperience,
                                   @RequestParam(required = false) Integer minLevel,
                                   @RequestParam(required = false) Integer maxLevel){
    return playersService.playersCount(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
    }

    @PostMapping("")
    public @ResponseBody Player createPlayer(@RequestBody Player player) throws BadRequestException, PlayerNotFoundException {
        Optional<Player> optionalPlayer = playersService.createPlayer(player);
        if (optionalPlayer.isPresent()) return optionalPlayer.get();
        throw new BadRequestException("can't create player");
    }



    @PostMapping("/{id}")
    public Player updatePlayer(@RequestBody Player player, @PathVariable("id") Long id) throws BadRequestException, PlayerNotFoundException {
        return playersService.updatePlayer(player, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deletePLayer(@PathVariable("id") Long id) throws BadRequestException, PlayerNotFoundException {
        return ResponseEntity.status(playersService.deletePlayer(id)).build();

    }



}
