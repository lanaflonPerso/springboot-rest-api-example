package com.hendisantika.hendisantikaauth.service;

import com.hendisantika.hendisantikaauth.config.AuthProperties;
import com.hendisantika.hendisantikaauth.model.User;
import com.hendisantika.hendisantikaauth.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-rest-api-example
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-03-26
 * Time: 07:08
 */
public class RegistrationServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private AuthProperties properties;
    private RegistrationService registrationService;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        properties = new AuthProperties();
        properties.setRedirectionUrl("http://www.example.com");

        registrationService = new RegistrationServiceImpl(userRepository, emailService, bCryptPasswordEncoder,
                properties);

    }

    @Test
    public void givenExistingUserWhenCheckingUserRegisteredThenReturnTrue() {

        User user = new User();
        user.setEmail("user@example.com");

        // given existing user
        given(userRepository.findOneByEmail("user@example.com")).willReturn(user);

        // when checking user registered
        boolean isRegistered = registrationService.isUserRegistered(user);

        // then expect true
        assertThat(isRegistered, is(true));

    }

    @Test
    public void givenNonExistingUserWhenCheckingUserRegisteredThenReturnFalse() {

        // given new user
        User user = new User();
        user.setEmail("user@example.com");

        // when checking user registered
        boolean isRegistered = registrationService.isUserRegistered(user);

        // then expect false
        assertThat(isRegistered, is(false));

    }

    @Test
    public void givenTokenWhenCheckingUserForTokenThenReturnCorrectUser() {

        User user = new User();
        user.setEmail("user@example.com");
        user.setConfirmationToken("1234");

        // given existing user
        given(userRepository.findByConfirmationToken("1234")).willReturn(user);

        // when getting user for token
        User loadedUser = registrationService.getUserForToken("1234");

        // then expect correct user to be loaded
        assertThat(loadedUser.getEmail(), is("user@example.com"));

    }

    @Test
    public void givenNewUserWhenRegisteringUserThenVerifyEmailWasSentWithCorrectFrom() {

        User user = new User();
        user.setEmail("user@example.com");

        // given user
        given(userRepository.findOneByEmail("user@example.com")).willReturn(user);

        // when registering new user
        registrationService.registerUser(user);

        // then email was sent
        verify(emailService).sendEmail(mailMessageCaptor.capture());

        SimpleMailMessage mail = mailMessageCaptor.getValue();
        assertThat(mail.getFrom(), is("noreply@gigsterous.com"));

    }

}