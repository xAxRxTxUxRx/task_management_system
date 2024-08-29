package com.artur.task_management_system.service.impl;

import com.artur.task_management_system.exception.EmailTakenException;
import com.artur.task_management_system.exception.EntityNotFoundByIdException;
import com.artur.task_management_system.exception.UserNotFoundByEmailException;
import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.model.Task;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.service.ConfirmationTokenService;
import com.artur.task_management_system.service.UserService;
import com.artur.task_management_system.model.attributes.UserRole;
import com.artur.task_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с User.
 * Реализует UserDetailsService и UserService.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    /**
     * Загружает детали пользователя по имени пользователя.
     *
     * @param username имя пользователя для загрузки
     * @return объект UserDetails, содержащий информацию о пользователе
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundByEmailException(username));
    }

    @Override
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));
    }

    @Override
    public Page<User> getAllUsers(int pageNumber, int pageSize,
                                  String field, String directionStr) {
        Pageable pageable = makePageable(pageNumber, pageSize, field, directionStr);
        return userRepository.findAll(pageable);
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() -> new EntityNotFoundByIdException("user", userId));
    }

    @Override
    @Transactional
    public void deleteLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User logedInUser = getUserByEmail(username);
        for (Task task : logedInUser.getAssignedTasks()){
            task.removePerformer(logedInUser);
        }
        for (Task task : logedInUser.getCreatedTasks()){
            for (User performer : task.getPerformers()){
                performer.removeAssignedTask(task);
            }
        }
        confirmationTokenService.deleteByUserId(logedInUser.getId());
        userRepository.delete(logedInUser);
    }

    @Override
    public ConfirmationToken addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new EmailTakenException(user.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setUserRole(UserRole.USER);

        userRepository.save(user);

        return generateToken(user);
    }

    @Override
    public Optional<ConfirmationToken> updateUserById(Long userId, User user) {
        User userToUpdate = getUserById(userId);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        userToUpdate.setPassword(encodedPassword);

        userToUpdate.setName(user.getName());

        boolean emailUpdated = false;
        if (!Objects.equals(user.getEmail(), userToUpdate.getEmail())){
            if (userRepository.existsByEmail(user.getEmail())){
                throw new EmailTakenException(user.getEmail());
            }

            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setEnabled(false);
            emailUpdated = true;
        }

        userRepository.save(userToUpdate);

        if (emailUpdated){
            ConfirmationToken confirmationToken = generateToken(userToUpdate);
            return Optional.of(confirmationToken);
        }else{
            return Optional.empty();
        }
    }

    @Override
    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));

        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Создает объект Pageable на основе предоставленных параметров пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Pageable, готовый к использованию в запросах к репозиторию
     * @throws IllegalStateException если параметры пагинации или сортировки некорректны
     */
    private Pageable makePageable(Integer pageNumber, Integer pageSize,
                                  String field, String directionStr) {
        if (pageNumber == null || pageSize == null){
            throw new IllegalStateException("Pagination cannot be null");
        }

        Pageable pageable;
        if (field != null && directionStr != null) {
            Sort.Direction direction;
            if (directionStr.equals("Asc")){
                direction = Sort.Direction.ASC;
            }else if(directionStr.equals("Desc")){
                direction = Sort.Direction.DESC;
            }else{
                throw new IllegalStateException("Wrong sorting direction value");
            }
            pageable = PageRequest.of(pageNumber, pageSize, direction, field);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        return pageable;
    }

    /**
     * Генерирует токен подтверждения для пользователя.
     *
     * @param user объект User, для которого генерируется токен
     * @return объект ConfirmationToken, связанный с пользователем
     */
    private ConfirmationToken generateToken(User user){
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setToken(token);
        confirmationToken.setUser(user);

        return confirmationToken;
    }
}
