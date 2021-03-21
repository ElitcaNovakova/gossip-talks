package bg.codeacademy.spring.gossiptalks.conttroler;


import static bg.codeacademy.spring.gossiptalks.conttroler.UserController.getCurrentUserName;

import bg.codeacademy.spring.gossiptalks.dto.Gossip;
import bg.codeacademy.spring.gossiptalks.dto.GossipList;
import bg.codeacademy.spring.gossiptalks.model.User;
import bg.codeacademy.spring.gossiptalks.service.GossipService;
import bg.codeacademy.spring.gossiptalks.service.UserService;
import bg.codeacademy.spring.gossiptalks.validation.ValidText;
import io.swagger.annotations.ApiParam;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/gossips")
@Validated
public class GossipController {

  private UserService userService;
  private GossipService gossipService;

  public GossipController(UserService userService, GossipService gossipService) {
    this.userService = userService;
    this.gossipService = gossipService;
  }

  @GetMapping
  public GossipList getGossips(
      @Min(0) @RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer pageNo,
      @Min(0) @Max(50) @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
    String name = getCurrentUserName();
    User user = userService.requireUser(name);
    Page<bg.codeacademy.spring.gossiptalks.model.Gossip> gossips = gossipService
        .getGossips(pageNo, pageSize, user);
    return toDTO(gossips);
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public Gossip postGossip(
      @Valid @ValidText @Size(max = 255) @ApiParam(value = "", required = true) @RequestParam(value = "text", required = true) String text) {
    String name = getCurrentUserName();
    User user = userService.requireUser(name);
    bg.codeacademy.spring.gossiptalks.model.Gossip gossip = gossipService.createGossip(text, user);
    return toDTO(gossip);
  }

  static Gossip toDTO(bg.codeacademy.spring.gossiptalks.model.Gossip gossip) {
    String id = Long.toString(gossip.getId(), 32).toUpperCase();
    return (Gossip) new Gossip()
        .setId(id)
        .setUsername(gossip.getAuthor().getUsername())
        .setDateTime(gossip.getDateTime())
        .setText(gossip.getText());

  }

  static GossipList toDTO(Page<bg.codeacademy.spring.gossiptalks.model.Gossip> page) {
    return new GossipList()
        .setPageNumber(page.getNumber())
        .setPageSize(page.getSize())
        .setCount(page.getNumberOfElements())
        .setTotal((int) page.getTotalElements())
        .setContent(
            page.getContent().stream().map(GossipController::toDTO).collect(Collectors.toList()));
  }

}
