package bg.codeacademy.spring.gossiptalks.conttroler;

import bg.codeacademy.spring.gossiptalks.dto.GossipDto;
import bg.codeacademy.spring.gossiptalks.dto.GossipList;
import bg.codeacademy.spring.gossiptalks.model.Gossip;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.service.GossipService;
import bg.codeacademy.spring.gossiptalks.service.UserService;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/gossips")
@Validated
public class GossipController {

  private UserService userService;

  public GossipController(UserService userService,
      GossipService gossipService) {
    this.userService = userService;
    this.gossipService = gossipService;
  }

  private GossipService gossipService;


  @GetMapping
  public GossipList getGossips(
      @Min(0) @RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer pageNo,
      @Min(0) @Max(50) @RequestParam(value = "pageSize", required = false, defaultValue = "0") Integer pageSize) {
    User user = userService.getCurrentUser();
    Page<Gossip> gossips = gossipService.getGossips(pageNo, pageSize, user);
    return toDTO(gossips);
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public GossipDto postGossip(
      @RequestPart(value = "text", required = true) String text) {
    User user = userService.getCurrentUser();
    Gossip gossip = gossipService.createGossip(text, user);
    return toDTO(gossip);
  }

  static GossipDto toDTO(Gossip gossip) {
    String id = Long.toString(gossip.getId(), 32);
    DateTimeFormatter fm = DateTimeFormatter.ISO_DATE_TIME;
    return new GossipDto()
        .setId(id)
        .setText(gossip.getText())
        .setUsername(gossip.getAuthor().getUsername())
        .setDateTime(fm.format(gossip.getDateTime()));
  }

  static GossipList toDTO(Page<Gossip> page) {
    return new GossipList()
        .setPageNumber(page.getNumber())
        .setPageSize(page.getSize())
        .setCount(page.getNumberOfElements())
        .setTotal((int) page.getTotalElements())
        .setContent(page.getContent().stream()
            .sorted((Gossip g1, Gossip g2) ->
                g2.getDateTime().compareTo(g1.getDateTime()))
            .map(model -> toDTO(model))
            .collect(Collectors.toList())

        );

  }

}
