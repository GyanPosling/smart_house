//package com.avelina_anton.bzhch.smart_house.demo.servicesTest;
//
//import com.avelina_anton.bzhch.smart_house.demo.models.User;
//import com.avelina_anton.bzhch.smart_house.demo.repositories.UsersRepository;
//import com.avelina_anton.bzhch.smart_house.demo.services.UsersService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UsersServiceTest {
//
//    @Mock
//    private UsersRepository usersRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UsersService usersService;
//
//    @Test
//    void registerUser_ValidUser_ShouldRegister() {
//        User user = new User();
//        user.setName("test");
//        user.setPassword("password");
//        user.setEmail("test@example.com");
//
//        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
//        when(usersRepository.save(any(User.class))).thenReturn(user);
//
//        User registeredUser = usersService.registerUser(user);
//
//        assertNotNull(registeredUser);
//        assertEquals("encodedPassword", registeredUser.getPassword());
//        verify(usersRepository, times(1)).save(user);
//    }
//
//    @Test
//    void findUserByEmail_ShouldReturnUser() {
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//
//        Optional<User> foundUser = usersService.findUserByEmail("test@example.com");
//
//        assertTrue(foundUser.isPresent());
//        assertEquals("test@example.com", foundUser.get().getEmail());
//        verify(usersRepository, times(1)).findByEmail("test@example.com");
//    }
//
//    @Test
//    void getAllUsers_ShouldReturnAllUsers() {
//        User user1 = new User();
//        user1.setId(1L);
//        User user2 = new User();
//        user2.setId(2L);
//
//        when(usersRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
//
//        List<User> users = usersService.getAllUsers();
//
//        assertEquals(2, users.size());
//        verify(usersRepository, times(1)).findAll();
//    }
//
//    @Test
//    void validatePassword_ValidPassword_ShouldReturnTrue() {
//        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
//
//        boolean result = usersService.validatePassword("rawPassword", "encodedPassword");
//
//        assertTrue(result);
//        verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");
//    }
//}