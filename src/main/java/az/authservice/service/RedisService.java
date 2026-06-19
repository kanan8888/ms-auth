package az.authservice.service;

import java.util.Optional;

public interface RedisService {
    void setValue(String key, String value, long ttlSeconds);
    Optional<String> getValue(String key);
    void delete(String key);
    void increment(String key, long ttlSeconds);
    int getCount(String key);
}
