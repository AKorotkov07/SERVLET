package ru.netology.repository;

import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
  private static final String APPLICATION_JSON = "application/json";
  private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
  private final AtomicLong counter = new AtomicLong(0);

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      long id = counter.incrementAndGet();
      Post newPost = new Post(id, post.getContent());
      posts.put(id, newPost);
      return newPost;
    } else {
      return posts.compute(post.getId(), (id, existingPost) -> {
        if (existingPost == null) {
          Post newPost = new Post(id, post.getContent());
          return newPost;
        } else {
          existingPost.setContent(post.getContent());
          return existingPost;
        }
      });
    }
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}