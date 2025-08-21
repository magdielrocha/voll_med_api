package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Recupera o token
        var tokenJWT = recuperarToken(request);

        // Se existe o cabeçalho, aí ele pega o token no cabeçalho (subject) e faz a validação
        if (tokenJWT != null) {
            //recupera o token, pega o login e a senha
            var subject = tokenService.getSubject(tokenJWT);
            //Autencicação forçada
            // Carrega o usuário do banco de dados
            var usuario = repository.findByLogin(subject);
            //chamar uma classe específica do Spring SecurityContextHolder
            // instancia o objeto UsernamePasswordAuthenticationToken
            // e passa os parâmetros 1 - usuario, 2 - null e 3 - o perfil do usuário (getAuthorities())
            // que foi criado na classe usuário.
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
           // Agora nessa requisição o Spring considera que o usuário está logado
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);
    }

    // RECUPERAÇÃO DO TOKEN  ENVIADO NO CABEÇALHO
    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) { //se authorization for diferente de null, ou seja, se o cabeçalho for enviado.
           return  authorizationHeader.replace("Bearer ", "").trim();
        }
        return null;
    }

}
