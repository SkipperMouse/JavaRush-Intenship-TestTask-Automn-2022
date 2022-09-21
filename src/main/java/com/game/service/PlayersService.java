package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exceptions.BadRequestException;
import com.game.exceptions.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PlayersService {
    private static final Logger log = Logger.getGlobal();
    private final PlayerRepository playerRepository;
    private final PlayersServiceHelper helper;

    @Autowired
    public PlayersService(PlayerRepository playerRepository, PlayersServiceHelper helper) {
        this.playerRepository = playerRepository;
        this.helper = helper;
    }

    public List<Player> findPlayers(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned,
                                    Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel, PlayerOrder order,
                                    Integer pageNumber, Integer pageSize, boolean isForCount) {

        List<Player> allPlayers = playerRepository.findAll();
        {
        if (name != null) allPlayers = allPlayers.stream().filter(p -> p.getName().contains(name)).collect(Collectors.toList());
        if (title != null) allPlayers = allPlayers.stream().filter(p -> p.getTitle().contains(title)).collect(Collectors.toList());
        if (race != null) allPlayers = allPlayers.stream().filter(p-> p.getRace() == race).collect(Collectors.toList());
        if (profession != null) allPlayers = allPlayers.stream().filter(p -> p.getProfession() == profession).collect(Collectors.toList());
        if (after != null) allPlayers = allPlayers.stream().filter(p -> p.getBirthday().after(new Date(after))).collect(Collectors.toList());
        if (before != null) allPlayers = allPlayers.stream().filter(p -> p.getBirthday().before(new Date(before))).collect(Collectors.toList());
        if (banned != null) allPlayers = allPlayers.stream().filter(p -> p.getBanned() == banned).collect(Collectors.toList());
        if (minExperience != null) allPlayers = allPlayers.stream().filter(p -> p.getExperience().compareTo(minExperience) >= 0).collect(Collectors.toList());
        if (maxExperience != null) allPlayers = allPlayers.stream().filter(p -> p.getExperience().compareTo(maxExperience) <= 0).collect(Collectors.toList());
        if (minLevel != null) allPlayers = allPlayers.stream().filter(p -> p.getLevel().compareTo(minLevel) >= 0).collect(Collectors.toList());
        if (maxLevel != null) allPlayers = allPlayers.stream().filter(p -> p.getLevel().compareTo(maxLevel) <=0).collect(Collectors.toList());
        } // check parameters
        if (isForCount){ // form method preparePage
            return allPlayers;
        }
        allPlayers = helper.preparePage(allPlayers, order, pageNumber, pageSize);
        return allPlayers;
    }


    @Transactional(readOnly = false)
    public Optional<Player> createPlayer(Player player) throws BadRequestException {
        if (player.getName() == null || player.getTitle() == null || player.getRace() == null || player.getProfession() == null || player.getBirthday() == null || player.getExperience() ==null){
            throw new BadRequestException("error nulls");
        }
        helper.checkPlayer(player);

        if (player.getBanned() == null) player.setBanned(false);
        player.setLevel(helper.countLevel(player));
        player.setUntilNextLevel(helper.expToNextLvl(player));
        return Optional.of(playerRepository.save(player));
    }



    public Player findPlayerById(Long id) throws BadRequestException, PlayerNotFoundException {
        if(id == null || id <= 0) throw new BadRequestException();
        Optional<Player> player = playerRepository.findById(id);
        if (!player.isPresent()) throw new PlayerNotFoundException();
        return player.get();
    }

    public Integer playersCount(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        return findPlayers(name, title, race, profession, after, before, banned, minExperience,
                maxExperience, minLevel, maxLevel, null, null, null, true).size();
    }
    @Transactional(readOnly = false)
    public Player updatePlayer(Player player, Long id) throws BadRequestException, PlayerNotFoundException {
        helper.checkPlayer(player);
        Player playerToUpdate = findPlayerById(id);

            if(player.getName() != null) playerToUpdate.setName(player.getName());
            if(player.getTitle() != null) playerToUpdate.setTitle(player.getTitle());
            if(player.getRace() != null) playerToUpdate.setRace(player.getRace());
            if(player.getProfession() != null) playerToUpdate.setProfession(player.getProfession());
            if(player.getBirthday() != null) playerToUpdate.setBirthday(player.getBirthday());
            if(player.getBanned() != null) playerToUpdate.setBanned(player.getBanned());
            if(player.getExperience() != null) playerToUpdate.setExperience(player.getExperience());
            playerToUpdate.setLevel(helper.countLevel(playerToUpdate));
            playerToUpdate.setUntilNextLevel(helper.expToNextLvl(playerToUpdate));

            return playerRepository.save(playerToUpdate);


    }
    @Transactional(readOnly = false)
    public HttpStatus deletePlayer(Long id) throws BadRequestException, PlayerNotFoundException {
    Player player = findPlayerById(id);
    playerRepository.delete(player);
    return HttpStatus.OK;
    }


}
