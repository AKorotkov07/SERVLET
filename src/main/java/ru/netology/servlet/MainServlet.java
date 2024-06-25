package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.service.PostService;
import ru.netology.repository.PostRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
  private static final String GET = "GET";
  private static final String POST = "POST";
  private static final String DELETE = "DELETE";
  private static final String API_POSTS = "/api/posts";
  private static final String API_POSTS_ID_PATTERN = "/api/posts/\\d+";
  private static final String ID_PATH = "/";
  private static final int STATUS_NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;
  private static final int STATUS_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

  private PostController controller;

  @Override
  public void init() throws ServletException {
    final var service = new PostService(new PostRepository());
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      if (method.equals(GET) && path.equals(API_POSTS)) {
        controller.all(resp);
        return;
      }
      if (method.equals(GET) && path.matches(API_POSTS_ID_PATTERN)) {
        final var id = Long.parseLong(path.substring(path.lastIndexOf(ID_PATH) + 1));
        controller.getById(req, resp);
        return;
      }
      if (method.equals(POST) && path.equals(API_POSTS)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(DELETE) && path.matches(API_POSTS_ID_PATTERN)) {
        controller.removeById(req, resp);
        return;
      }
      resp.setStatus(STATUS_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(STATUS_ERROR);
    }
  }
}

