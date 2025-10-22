package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        BinaryContent binaryContent = event.getBinaryContent();
        byte[] bytes = event.getBytes();

        log.debug("트랜잭션 커밋 후 바이너리 데이터 저장 시작: id={}, filename={}, size={}",
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize()
        );
        binaryContentStorage.put(binaryContent.getId(), bytes);
        log.debug("바이너리 데이터 저장 완료: id={}, filename={}, size={}",
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getSize()
        );
    }
}
