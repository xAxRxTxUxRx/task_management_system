package com.artur.task_management_system.controller;

import com.artur.task_management_system.dto.UserCreationDTO;
import com.artur.task_management_system.dto.UserViewDTO;
import com.artur.task_management_system.dto.mappers.UserMapper;
import com.artur.task_management_system.model.PageResponse;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.service.UserService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Контроллер для управления пользователями.
 */
@Tag(name = "Users")
@RestController
@RequestMapping(path = "api/users")
@AllArgsConstructor
public class UserController {
    private final String PASSWORD_NOT_MATCHING_MSG = "Passwords %s and %s are not matching";
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final UserService userService;
    private final UserConfirmationService userConfirmationService;

    /**
     * Получает список всех пользователей с возможностью пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество записей на странице для пагинации
     * @param field поле для сортировки результатов
     * @param directionStr направление сортировки (Asc/Desc)
     * @return список пользователей типа {@link UserViewDTO} и метоинфорацию по офсетам с HTTP статусом 200 OK
     */
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieve a paginated/sorted list of all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of user list" +
                            " with metadata about offsets",
                            content = @Content(schema = @Schema(implementation = UserViewDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request. (Pagination can't be null/Wrong sorting direction value)"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<PageResponse<UserViewDTO>> getAllUsers(
            @Parameter(description = "Page number for pagination", required = true, example = "0")
            @RequestParam("pageNumber")
            Integer pageNumber,

            @Parameter(description = "Number of records per page for pagination", required = true, example = "10")
            @RequestParam("pageSize")
            Integer pageSize,

            @Parameter(description = "Field to sort results by", required = false, example = "id")
            @RequestParam(value = "field", required = false)
            String field,

            @Parameter(description = "Sorting direction (Asc/Desc)", required = false, example = "Asc")
            @RequestParam(value = "direction", required = false)
            String directionStr) {
        Page<User> users = userService.getAllUsers(pageNumber, pageSize, field, directionStr);
        List<UserViewDTO> userViewDTOs = users.stream().map(userMapper::userToUserViewDTO).toList();
        PageResponse<UserViewDTO> pageResponse = new PageResponse<>(
                userViewDTOs,
                users.getTotalElements(),
                users.getSize(),
                users.getNumber()+1,
                users.getTotalPages()
        );
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

    /**
     * Получает информацию о пользователе по его ID.
     *
     * @param userId ID пользователя для получения информации
     * @return информация о пользователе в формате {@link UserViewDTO} с HTTP статусом 200 OK
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "Get User By ID",
            description = "Fetches a user based on the provided userId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of user details",
                            content = @Content(schema = @Schema(implementation = UserViewDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<UserViewDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "1")
            @PathVariable("userId")
            Long userId){
        User user = userService.getUserById(userId);
        UserViewDTO userViewDTO = userMapper.userToUserViewDTO(user);
        return new ResponseEntity<>(userViewDTO, HttpStatus.OK);
    }

    /**
     * Удаляет текущего залогиненного пользователя.
     */
    @DeleteMapping
    @Operation(
            summary = "Delete Logged-In User",
            description = "Deletes the currently logged-in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized")
            }
    )
    public void deleteLoggedInUser(){
        userService.deleteLoggedInUser();
    }

    /**
     * Обновляет информацию о текущем залогиненном пользователе.
     *
     * @param userDTO данные пользователя для обновления
     * @throws IllegalStateException если пароли не совпадают
     */
    @PutMapping
    @Operation(
            summary = "Update Logged-In User",
            description = "Updates the currently logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized")
            }
    )
    public void updateLoggedInUser(@Valid @RequestBody UserCreationDTO userDTO){
        if (!Objects.equals(userDTO.getPassword(), userDTO.getMatchingPassword())){
            throw new IllegalStateException(String.format(PASSWORD_NOT_MATCHING_MSG,
                    userDTO.getPassword(), userDTO.getMatchingPassword()));
        }
        User user = userMapper.userCreationDTOtoUser(userDTO);
        userConfirmationService.updateLoggedInUser(user);
    }
}
