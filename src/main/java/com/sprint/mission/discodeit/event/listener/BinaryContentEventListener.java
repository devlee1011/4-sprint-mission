package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        UUID binaryContentId = event.binaryContentId();
        byte[] bytes = event.bytes();
        int size = bytes.length;

        log.debug("트랜잭션 커밋 후 바이너리 데이터 저장 이벤트 처리 시작: id={}, size={}",
                binaryContentId,
                size
        );
        binaryContentStorage.put(binaryContentId, bytes);
        log.debug("바이너리 데이터 저장 완료: id={}, size={}",
                binaryContentId,
                size
        );
    }
}
