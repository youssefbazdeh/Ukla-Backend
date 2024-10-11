package spark.ukla.services.implementations;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.creator_feature.Creator;
import spark.ukla.creator_feature.CreatorRepository;
import spark.ukla.entities.ConfirmationToken;
import spark.ukla.entities.Image;
import spark.ukla.entities.Profile;
import spark.ukla.entities.User;
import spark.ukla.entities.enums.AuthentificationProvider;
import spark.ukla.entities.enums.Role;
import spark.ukla.repositories.ProfileRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IRegistrationService;
import spark.ukla.utils.EmailService;
import spark.ukla.utils.RandomUtil;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationService implements IRegistrationService {
    private final UserRepository userRepository;
    private final CreatorRepository creatorRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;

    public RegistrationService(UserRepository userRepository, CreatorRepository creatorRepository, ConfirmationTokenService confirmationTokenService, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.creatorRepository = creatorRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.profileRepository = profileRepository;
    }


    @Override
    public ResponseEntity<String> register(User newUser) {
        boolean ExistsByUsername = userRepository.existsByUsername(newUser.getUsername());

        if (ExistsByUsername) {
            return new ResponseEntity<>("username exists", HttpStatus.FOUND);
        }
        boolean ExistsByEmail = userRepository.existsByEmail(newUser.getEmail());

        if (ExistsByEmail) {
            return new ResponseEntity<>("email exists", HttpStatus.NOT_ACCEPTABLE);
        }


        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        newUser.setProvider(AuthentificationProvider.USERNAME_PASSWORD);
        newUser.setRole(Role.USER);
        Profile profile = new Profile();
        profile.setOnBoardingScreen(true);
        newUser.setProfile(profile);
        spark.ukla.entities.User userSaved = userRepository.save(newUser);
        profileRepository.save(profile);
        String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5), userSaved);
        confirmationTokenService.add(confirmationToken);

        emailService.send(newUser.getEmail(), "Confirm your email.", emailService.buildEmail(newUser.getUsername(),
                "Thank you for registering. Please activate your account:", token));
        log.info("User saved, confirm your email.");


        return new ResponseEntity<>("user saved", HttpStatus.CREATED);
    }

    @Override
    public String registerCreator(Creator newCreator, Image image) {
        boolean ExistsByUsername = userRepository.existsByUsername(newCreator.getUsername());

        if (ExistsByUsername) {
            return "username exists";
        }
        boolean ExistsByEmail = userRepository.existsByEmail(newCreator.getEmail());

        if (ExistsByEmail) {
            return "email exists";
        }

        newCreator.setImage(image);
        newCreator.setPassword(bCryptPasswordEncoder.encode(newCreator.getPassword()));
        newCreator.setRole(Role.CREATOR);
        spark.ukla.entities.User userSaved = userRepository.save(newCreator);

        String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5), userSaved);
        confirmationTokenService.add(confirmationToken);

        emailService.send(newCreator.getEmail(), "Confirm your email.", emailService.buildEmail(newCreator.getUsername(),
                "Thank you for registering. Please activate your account:", token));
        log.info("Creator saved, confirm your email.");


        return "creator saved";
    }



    public Optional<Creator> retrieveByUsername(String username) {
        return Optional.ofNullable(creatorRepository.findByUsername(username));
    }
    public User registerWithOutEmailVerification(User user1) {

        boolean ExistsByUsername = userRepository.existsByUsername(user1.getUsername()); // todo  delete this after adding this check in the previous called method which is the create account method
        boolean ExistsByEmail = userRepository.existsByEmail(user1.getEmail()); // todo delete this after checking that this case will never happen because the frontend sends a request to the signin method first which redirects to link account if the user email exists
        if (ExistsByUsername) {
            log.error("Username exists.");
            return null;
        } else if (ExistsByEmail) {
            log.error("Email exists.");
            return null;
        } else {
            user1.setPassword(bCryptPasswordEncoder.encode(user1.getPassword()));

            return userRepository.save(user1);


        }
    }


    @Override
    public int enableUser(long id) {
        return userRepository.enableUser(id);
    }

    @Override
    @Transactional
    public String confirmToken(String token) {
        if (confirmationTokenService.getByToken(token).isPresent()) {
            ConfirmationToken confirmationToken = confirmationTokenService.getByToken(token).get();
            if (confirmationToken.getConfirmedAt() != null) {
                return "email already confirmed";
            }
            LocalDateTime expiredAt = confirmationToken.getExpiredAt();
            if (expiredAt.isBefore(LocalDateTime.now())) {
                return "token expired";
            }
            confirmationTokenService.updateConfirmedAt(token);
            if (confirmationToken.getUser()!=null)
                enableUser(confirmationToken.getUser().getId());
            return "confirmed";
        } else
            return "token not found";


    }

    @Override
    public String resendActivationCode(String email) {
        try {
            User user = userRepository.findByEmail(email);
            confirmationTokenService.deleteToken(user);
            String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
            ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(5), user);
            confirmationTokenService.add(confirmationToken);
            emailService.send(user.getEmail(), "Confirm your email.", emailService.buildEmail(user.getUsername(),
                    "Thank you for registering. Please activate your account:", token));
            log.info(" resend Activation code in your email.");
            return "code resent";
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return exception.getMessage();
        }
    }
}
