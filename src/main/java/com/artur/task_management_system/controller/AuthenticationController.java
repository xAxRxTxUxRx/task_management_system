package com.artur.task_management_system.controller;

import com.artur.task_management_system.dto.UserCreationDTO;
import com.artur.task_management_system.dto.mappers.UserMapper;
import com.artur.task_management_system.model.AuthenticationRequest;
import com.artur.task_management_system.model.AuthenticationResponse;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.service.UserConfirmationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Контроллер для управления аутентификацией.
 */
@Tag(name = "Authentication")
@RestController
@RequestMapping(path = "api/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final String PASSWORD_NOT_MATCHING_MSG = "Passwords %s and %s are not matching";
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final UserConfirmationService userConfirmationService;

    /**
     * Регистрирует нового пользователя.
     *
     * Принимает данные пользователя из {@link UserCreationDTO}, проверяет, что пароли совпадают, и затем регистрирует пользователя.
     * Возвращает объект {@link AuthenticationResponse} с информацией о результате регистрации.
     *
     * @param userCreationDTO данные пользователя для регистрации
     * @return объект {@link AuthenticationResponse} с информацией о результате регистрации
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Register a new user from UserCreationDTO",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful register of user",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request"),
                    @ApiResponse(responseCode = "409", description = "Email already taken")
            }
    )
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserCreationDTO userCreationDTO){
        if (!Objects.equals(userCreationDTO.getPassword(), userCreationDTO.getMatchingPassword())){
            throw new IllegalStateException(String.format(PASSWORD_NOT_MATCHING_MSG,
                    userCreationDTO.getPassword(), userCreationDTO.getMatchingPassword()));
        }
        User user = userMapper.userCreationDTOtoUser(userCreationDTO);

        AuthenticationResponse resp = userConfirmationService.registerUser(user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * Аутентифицирует пользователя.
     *
     * Принимает данные для аутентификации из {@link AuthenticationRequest} и возвращает объект {@link AuthenticationResponse} с информацией о результате аутентификации.
     *
     * @param request данные для аутентификации
     * @return объект {@link AuthenticationResponse} с информацией о результате аутентификации
     */
    @PostMapping("/authenticate")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate user by AuthenticationRequest",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful authentication of user",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request){
        AuthenticationResponse resp = userConfirmationService.authenticate(request);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * Подтверждает токен подтверждения.
     *
     * Принимает токен подтверждения в качестве параметра запроса и вызывает метод {@code confirmToken} у {@link UserConfirmationService}.
     * Этот метод должен быть использован для подтверждения регистрации пользователя по электронной почте.
     *
     * @param token токен подтверждения
     */
    @GetMapping("/confirm")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate user by AuthenticationRequest",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful authentication of user"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Confirmation token not found"),
                    @ApiResponse(responseCode = "409", description = "Conflict confirming token")
            }
    )
    public void confirm(
            @Parameter(description = "Email confirmation token",
                    example = "deb77d29-7962-4f2c-b164-efb94a297239", required = true)
            @RequestParam("token") String token) {
        userConfirmationService.confirmToken(token);
    }
}
