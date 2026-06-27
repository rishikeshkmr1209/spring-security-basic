package com.spring.basic.config;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.security.autoconfigure.SecurityProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.StringUtils;

@Configuration
public class UserDetailsServiceAutoConfiguration {
//Commenting it once we tried to create user from UserAuthEntityController as UserAuthEntityService also extends UserDetailsService and hence its bean will also get created.
/* 
    @Bean
    @Primary
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(SecurityProperties properties,ObjectProvider<PasswordEncoder> passwordEncoder){
          
          SecurityProperties.User user=properties.getUser();
          List<String> roles=user.getRoles();
         //If we are not creating bean of PasswordEncoder, We need to keep the {noop} so that based on the value, the default Encoder will match the plain one.
         // UserDetails user1= User.withUsername("rishi").password("{noop}1234").roles(StringUtils.toStringArray(roles)).build();
          //UserDetails user2= User.withUsername("sunny").password("{noop}1234").roles(StringUtils.toStringArray(roles)).build();

          //Once we enable the Bcrypt,then use till code
          UserDetails user1= User.withUsername("rishi").password(new BCryptPasswordEncoder().encode("1234")).roles(StringUtils.toStringArray(roles)).build();
          UserDetails user2= User.withUsername("sunny").password(new BCryptPasswordEncoder().encode("1234")).roles(StringUtils.toStringArray(roles)).build();
          
          
           InMemoryUserDetailsManager detailsManager=new InMemoryUserDetailsManager(user1,user2);
           return detailsManager;

    }
 */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    


}
