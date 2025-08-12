package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.info("공개 채널 생성 시작 - 채널명: {}", request.name());
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        channelRepository.save(channel);
        log.info("공개 채널 저장 - 채널 ID: {}", channel.getId());

        ChannelDto result = channelMapper.toDto(channel);
        log.info("공개 채널 생성 완료 - 채널 ID: {}, 채널명: {}, 채널 설명: {}, 채널 타입: {}",
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType());
        return result;
    }

    @Transactional
    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.info("비공개 채널 생성 시작 - 참여자 ID: {}", request.participantIds().stream()
                .map(id -> id + "")
                .collect(Collectors.joining(", ")));

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);

        channelRepository.save(channel);
        log.info("비공개 채널 저장 - 채널 ID: {}", channel.getId());

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
                .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
                .toList();
        readStatusRepository.saveAll(readStatuses);
        log.info("읽기 정보 저장 - 읽기 정보 ID: {}", readStatuses.stream()
                .map(readStatus -> readStatus.getId() + "")
                .collect(Collectors.joining(", ")));

        ChannelDto result = channelMapper.toDto(channel);
        log.info("비공개 채널 생성 완료 - 채널 ID: {}, 채널 타입: {}, 참여자 ID: {}",
                channel.getId(),
                channel.getType(),
                readStatuses.stream()
                        .map(readStatus -> readStatus.getUser().getId() + "")
                        .collect(Collectors.joining(", ")));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        log.info("채널 상세 조회 시작 - 채널 ID: {}", channelId);

        ChannelDto result = channelRepository.findById(channelId)
                .map(channelMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("채널 상세 조회 실패 - 존재하지 않는 채널 ID: {}", channelId);
                    return new NoSuchElementException("Channel with id " + channelId + " not found");
                });
        log.info("채널 상세 조회 완료 - 채널 ID: {}, 채널 타입: {}", channelId, result);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.info("해당 사용자가 참여중인 채널 목록 조회 시작 - 사용자 ID: {}", userId);

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .map(Channel::getId)
                .toList();

        List<ChannelDto> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
                .stream()
                .map(channelMapper::toDto)
                .toList();
        log.info("해당 사용자가 참여중인 채널 목록 조회 완료 - 사용자 ID: {}, 채널 ID: {}",
                userId,
                result.stream()
                        .map(channelDto -> channelDto.id() + "")
                        .collect(Collectors.joining(", ")));
        return result;
    }

    @Transactional
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.info("공개 채널 수정 시작 - 채널 ID: {}, 요청 채널명: {}, 요청 채널 설명: {}",
                channelId,
                request.newName(),
                request.newDescription());

        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.warn("공개 채널 수정 실패 - 존재하지 않는 채널 ID: {}", channelId);
                    return new NoSuchElementException("Channel with id " + channelId + " not found");
                });
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("공개 채널 수정 실패 - 채널 ID: {}, 잘못된 채널 타입: {}", channelId, channel.getType());
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(newName, newDescription);
        ChannelDto result = channelMapper.toDto(channel);
        log.info("공개 채널 수정 완료 - 채널 ID: {}, 변경된 채널명: {}, 변경된 채널 설명: {}",
                channel.getId(),
                channel.getName(),
                channel.getDescription());
        return result;
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        log.info("채널 삭제 시작 - 채널 ID: {}", channelId);
        if (!channelRepository.existsById(channelId)) {
          log.warn("채널 삭제 실패 - 존재하지 않는 채널 ID: {}", channelId);
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        messageRepository.deleteAllByChannelId(channelId);
        log.info("채널 관련 메시지 삭제 - 채널 ID: {}", channelId);

        readStatusRepository.deleteAllByChannelId(channelId);
        log.info("채널 관련 읽기 정보 삭제 - 채널 ID: {}", channelId);

        channelRepository.deleteById(channelId);
        log.info("채널 삭제 완료 - 채널 ID: {}", channelId);
    }
}
