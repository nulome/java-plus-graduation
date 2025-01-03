package ru.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dto.user.NewUserRequest;
import ru.dto.user.UserDto;
import ru.dto.user.UserFollowingDto;
import ru.exception.ConflictException;
import ru.exception.NotFoundException;
import ru.mapper.UserMapper;
import ru.model.User;
import ru.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users = (ids == null || ids.isEmpty()) ?
                repository.findAll(PageRequest.of(from, size)).getContent() :
                repository.findByIdIn(ids, PageRequest.of(from, size));

        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUser) {
        User user = userMapper.toUser(newUser);
        return userMapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkUserInDataBase(id);
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void subscribe(Long followerId, Long followedId) {
        User follower = checkUserInDataBase(followerId);
        User followed = checkUserInDataBase(followedId);
        checkTwoUser(follower, followed);

        if (followed.getFollowers().contains(follower)) {
            throw new ConflictException("Нельзя повторно подписаться на пользователя");
        }

        follower.follow(followed);
        repository.saveAll(List.of(follower, followed));

    }

    @Override
    @Transactional
    public void unsubscribe(Long followerId, Long followedId) {
        User follower = checkUserInDataBase(followerId);
        User followed = checkUserInDataBase(followedId);
        checkTwoUser(follower, followed);

        if (!followed.getFollowers().contains(follower)) {
            throw new ConflictException("Нельзя отписаться если не подписан на пользователя");
        }

        follower.unfollow(followed);
        repository.saveAll(List.of(follower, followed));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(Long userId) {
        User user = checkUserInDataBase(userId);
        return toListUserDto(user.getFollowers());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(Long userId) {
        User user = checkUserInDataBase(userId);
        return toListUserDto(user.getFollowing());
    }

    @Override
    public Optional<UserDto> getUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        return user.map(userMapper::toUserDto);
    }

    @Override
    public Optional<UserFollowingDto> getUserFollowing(Long userId) {
        Optional<User> user = repository.findById(userId);
        return user.map(userMapper::toUserFollowingDto);
    }

    private List<UserDto> toListUserDto(Set<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users) {
            userDtoList.add(userMapper.toUserDto(user));
        }

        return userDtoList;
    }

    private void checkTwoUser(User user, User otherUser) {
        if (user.equals(otherUser)) {
            throw new ConflictException("Пользователи совпадают");
        }
    }

    private User checkUserInDataBase(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Значение в базе users не найдено: " + id));
    }
}
