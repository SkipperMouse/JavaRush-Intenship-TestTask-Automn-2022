package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class PlayersServiceHelper {
    public Comparator<Player> getComparator(PlayerOrder order) {
        return new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if (order == PlayerOrder.NAME) return o1.getName().compareTo(o2.getName());
                if (order == PlayerOrder.LEVEL) return  o1.getLevel().compareTo(o2.getLevel());
                if (order == PlayerOrder.EXPERIENCE) return o1.getExperience().compareTo(o2.getExperience());
                if (order == PlayerOrder.BIRTHDAY) return o1.getBirthday().compareTo(o2.getBirthday());
                    return o1.getId().compareTo(o2.getId());
            }
        };
    }

    public List<Player> preparePage(List<Player> allPlayers, PlayerOrder order, Integer pageNumber, Integer pageSize) {
        order = (order == null) ? PlayerOrder.ID : order;
        pageNumber = (pageNumber == null) ? 0 : pageNumber;
        pageSize = (pageSize == null) ? 3 : pageSize;

        allPlayers.sort(getComparator(order));
        int startIndex = (pageNumber == 0) ? pageNumber : pageNumber * pageSize;
        int endIndex = (pageNumber == 0)  ? pageSize : pageNumber * pageSize +pageSize;
        endIndex = Math.min(endIndex, allPlayers.size());

        return allPlayers.subList(startIndex, endIndex);

    }

    public Integer countLevel(Player player){
        double exp = player.getExperience();
        double level = ((Math.sqrt(2500 + 200 * exp)) - 50) /100;
        return (int) Math.floor(level);
    }
    public Integer expToNextLvl(Player player){
        int exp = player.getExperience();
        int lvl = player.getLevel();
        int answer = (50 * (lvl +1) * (lvl +2)) - exp;
        return answer;
    }
    public void checkPlayer(Player player) throws BadRequestException {

        if (player.getName() != null && (player.getName().length() > 12 || player.getName().equals(""))) {
            throw new BadRequestException("error name");
        }
        if (player.getTitle() != null && player.getTitle().length() > 30 ){
            throw new BadRequestException("error Title");
        }
        if (player.getExperience() != null && (player.getExperience() > 10_000_000 || player.getExperience() < 0)) {
            throw new BadRequestException("error exp");
        }
        if (player.getBirthday() != null && (player.getBirthday().getTime() < 0 || player.getBirthday().getTime() < 946684800000L || player.getBirthday().getTime() > 32535216000000L)){
            throw new BadRequestException("error date");
        }

    }
}
