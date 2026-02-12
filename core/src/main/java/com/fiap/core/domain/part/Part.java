package com.fiap.core.domain.part;

import com.fiap.core.exception.BusinessRuleException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

	private UUID id;
	private String name;
	private String description;
	private Money price;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
}