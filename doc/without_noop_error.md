If we don't provide the {noop} in the below code, we will get error:

 @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(SecurityProperties properties,ObjectProvider<PasswordEncoder> passwordEncoder){
          InMemoryUserDetailsManager detailsManager=new InMemoryUserDetailsManager();
          SecurityProperties.User user=properties.getUser();
          List<String> roles=user.getRoles();

          UserDetails user1= User.withUsername("rishi").password("{noop}1234").roles(StringUtils.toStringArray(roles)).build();
          
          
           detailsManager.createUser(user1);
           return detailsManager;

    }

Error:


o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception

java.lang.IllegalArgumentException: Given that there is no default password encoder configured, each password must have a password encoding prefix. Please either prefix this password with '{noop}' or set a default password encoder in `DelegatingPasswordEncoder`.
        at org.springframework.security.crypto.password.DelegatingPasswordEncoder$UnmappedIdPasswordEncoder.matchesNonNull(DelegatingPasswordEncoder.java:304) ~[spring-security-crypto-7.1.0.jar:7.1.0]
        at org.springframework.security.crypto.password.AbstractValidatingPasswordEncoder.matches(AbstractValidatingPasswordEncoder.java:52) ~[spring-security-crypto-7.1.0.jar:7.1.0]
        at org.springframework.security.crypto.password.DelegatingPasswordEncoder.matchesNonNull(DelegatingPasswordEncoder.java:245) ~[spring-security-crypto-7.1.0.jar:7.1.0]
        at org.springframework.security.crypto.password.AbstractValidatingPasswordEncoder.matches(AbstractValidatingPasswordEncoder.java:52) ~[spring-security-crypto-7.1.0.jar:7.1.0]
        at org.springframework.security.authentication.dao.DaoAuthenticationProvider.additionalAuthenticationChecks(DaoAuthenticationProvider.java:89) ~[spring-security-core-7.1.0.jar:7.1.0]
        at org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.performPreCheck(AbstractUserDetailsAuthenticationProvider.java:198) ~[spring-security-core-7.1.0.jar:7.1.0]
        at org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.authenticate(AbstractUserDetailsAuthenticationProvider.java:159) ~[spring-security-core-7.1.0.jar:7.1.0]
        at org.springframework.security.authentication.ProviderManager.authenticate(ProviderManager.java:183) ~[spring-security-core-7.1.0.jar:7.1.0]
        at org.springframework.security.authentication.ProviderManager.authenticate(ProviderManager.java:215) ~[spring-security-core-7.1.0.jar:7.1.0]
        at org.springframework.security.web.authentication.www.BasicAuthenticationFilter.doFilterInternal(BasicAuthenticationFilter.java:204) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.8.jar:7.0.8]
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:385) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:110) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:96) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:385) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:90) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:75) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.8.jar:7.0.8]
        at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:385) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:82) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.context.SecurityContextHolderFilter.doFilter(SecurityContextHolderFilter.java:69) ~[spring-security-web-7.1.0.jar:7.1.0]
        at org.springframework.security.web.FilterC