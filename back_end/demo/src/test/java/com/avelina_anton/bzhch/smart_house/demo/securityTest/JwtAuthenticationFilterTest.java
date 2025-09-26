//package com.avelina_anton.bzhch.smart_house.demo.securityTest;
//
//import com.avelina_anton.bzhch.smart_house.demo.security.JwtAuthenticationFilter;
//import com.avelina_anton.bzhch.smart_house.demo.security.JwtUtils;
//import com.avelina_anton.bzhch.smart_house.demo.services.CustomUserDetailsService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class JwtAuthenticationFilterTest {
//
//    @Mock
//    private JwtUtils jwtUtils;
//
//    @Mock
//    private CustomUserDetailsService customUserDetailsService;
//
//    @InjectMocks
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private FilterChain filterChain;
//
//    @Test
//    void doFilterInternal_ValidToken_ShouldSetAuthentication() throws ServletException, IOException {
//        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
//        when(jwtUtils.getUsernameFromJwt("validtoken")).thenReturn("testuser");
//        when(jwtUtils.validateJwtToken("validtoken")).thenReturn(true);
//
//        UserDetails userDetails = mock(UserDetails.class);
//        when(customUserDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain, times(1)).doFilter(request, response);
//        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void doFilterInternal_NoToken_ShouldContinueChain() throws ServletException, IOException {
//        when(request.getHeader("Authorization")).thenReturn(null);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain, times(1)).doFilter(request, response);
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void doFilterInternal_InvalidToken_ShouldContinueChain() throws ServletException, IOException {
//        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
//        when(jwtUtils.getUsernameFromJwt("invalidtoken")).thenReturn("testuser");
//        when(jwtUtils.validateJwtToken("invalidtoken")).thenReturn(false);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain, times(1)).doFilter(request, response);
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//}