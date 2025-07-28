package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity(name = "binary_contents")
public class BinaryContent extends BaseEntity {

  @Column(nullable = false, length = 255)
  private String fileName;
  @Column(nullable = false)
  private Long size;
  @Column(nullable = false, length = 100)
  private String contentType;
}
