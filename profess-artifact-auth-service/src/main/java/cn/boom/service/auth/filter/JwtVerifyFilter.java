package cn.boom.service.auth.filter;

import cn.boom.framework.common.response.R;
import cn.boom.framework.common.utils.JwtUtils;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.model.Payload;
import cn.boom.service.auth.config.RsaConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtVerifyFilter extends BasicAuthenticationFilter {

    private RsaConfiguration prop;

    public JwtVerifyFilter(AuthenticationManager authenticationManager, RsaConfiguration prop) {
        super(authenticationManager);
        this.prop = prop;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            //如果携带错误的头信息，则给用户提示请登录！
            chain.doFilter(request, response);
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            R r = R.error().put("msg","请登录！").put("code",HttpServletResponse.SC_FORBIDDEN);
            out.write(new ObjectMapper().writeValueAsString(r));
            out.flush();
            out.close();
        } else {
            //如果携带了正确格式的token要先得到token
            String token = header.replace("Bearer ", "");
            //验证token是否正确
            try {
                Payload<TbUser> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), TbUser.class);
                TbUser tbUser = payload.getUserInfo();
                if (tbUser != null) {
                    UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(tbUser.getUsername(), null, tbUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    chain.doFilter(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = response.getWriter();
                R r = R.error().put("msg", "token错误！认证不通过！").put("code", HttpServletResponse.SC_FORBIDDEN);
                out.write(new ObjectMapper().writeValueAsString(r));
                out.flush();
                out.close();
            }
        }
    }
}
