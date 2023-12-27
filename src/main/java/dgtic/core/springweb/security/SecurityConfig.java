package dgtic.core.springweb.security;

import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository){
        return username -> {
            Optional<UsuarioEntity> usuario = usuarioRepository.findUsuarioByEmail(username);
            if (usuario.isPresent()){
                return usuario.get();
            }
            throw new UsernameNotFoundException("Usuario: " + username + " no encontrado!");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .requestMatchers("/bootstrap/**").permitAll()
                .requestMatchers("/imagen/**").permitAll()
                .requestMatchers("/tema/**").permitAll()
                .requestMatchers("/usuario/alta-usuario").permitAll()
                .requestMatchers(HttpMethod.POST,"/usuario/formulario").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin(formLogin ->
                        formLogin.loginPage("/")
                                .loginProcessingUrl("/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/aplicacion", true)
                                .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());
        return httpSecurity.build();
    }

}
