package com.leyou.client;

import com.leyou.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(value = "user-service",contextId = "userClient")
@Component
public interface UserClient extends UserApi {
}
