package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.model.Post;
import ru.netology.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class PostController {
  private static final Logger logger = LoggerFactory.getLogger(PostController.class);
  public static final String APPLICATION_JSON = "application/json";
  private final PostService service;
  private final Gson gson = new Gson();

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var data = service.all();
    response.getWriter().print(gson.toJson(data));
  }

  public void getById(HttpServletRequest request, HttpServletResponse response) {
    String idStr = request.getParameter("id");
    try {
      long id = Long.parseLong(idStr);
      Optional<Post> post = Optional.ofNullable(service.getById(id));
      if (post.isPresent()) {
        String json = gson.toJson(post.get());
        response.setContentType(APPLICATION_JSON);
        response.getWriter().write(json);
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
    } catch (NumberFormatException e) {
      logger.error("Error parsing ID", e);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (IOException e) {
      logger.error("IO Exception", e);
    }
  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var post = gson.fromJson(body, Post.class);
    final var data = service.save(post);
    response.getWriter().print(gson.toJson(data));
  }

  public void removeById(HttpServletRequest request, HttpServletResponse response) {
    String idStr = request.getParameter("id");
    try {
      long id = Long.parseLong(idStr);
      service.removeById(id);
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (NumberFormatException e) {
      logger.error("Error parsing ID", e);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error removing post", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
