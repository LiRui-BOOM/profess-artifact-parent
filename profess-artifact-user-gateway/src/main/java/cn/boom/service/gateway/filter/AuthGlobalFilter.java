package cn.boom.service.gateway.filter;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        //获取请求的url
        String path = exchange.getRequest().getURI().getPath();
        //静态资源访问,接口通过判断url中是否含有指定的字符来判断是否放行,在这里设置url的白名单
        System.out.println(path);
        if (path.contains("/user/add")
                || path.contains("/user/updatePasswordByEmailCheckCode")
                || path.contains("/user/sendUpdatePasswordCheckCodeEmail")) {
            return chain.filter(exchange);
        }

        // 令牌检验
        if (StringUtils.isEmpty(header)) { //拦截
            //如果携带错误的头信息，则给用户提示请登录！
            JsonObject message = new JsonObject();
            message.addProperty("msg", "未登录，请登录！");
            message.addProperty("code", 403);
            return out(exchange.getResponse(), message);

        } else {

            //得到token uid
            String token = header;
            //查询登录状态
            String sessionKey = (String)redisTemplate.opsForValue().get("PROFESS_SESSION_" + token);

            if (sessionKey == null) {
                // token 检验失败
                JsonObject message = new JsonObject();
                message.addProperty("msg", "登录信息过期或未登录！");
                message.addProperty("code", 403);
                return out(exchange.getResponse(), message);

            }
            return chain.filter(exchange);
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> out(ServerHttpResponse response, JsonObject message) {

        byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "*");
                headers.add("Access-Control-Allow-Headers", "*");
                headers.add("Access-Control-Allow-Credentials", "true");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

    private static String convertWsToHttp(String scheme) {
        scheme = scheme.toLowerCase();
        return "ws".equals(scheme) ? "http" : "wss".equals(scheme) ? "https" : scheme;
    }
}
