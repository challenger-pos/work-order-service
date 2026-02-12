package com.fiap.config;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.part.WorkOrderPartGateway;
import com.fiap.application.usecaseimpl.part.*;
import com.fiap.usecase.part.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PartConfig {

    @Bean
    public CreatePartUseCase createPartUseCase(PartGateway partGateway) {
        return new CreatePartUseCaseImpl(partGateway);
    }

    @Bean
    public DeletePartUseCase deletePartUseCase(PartGateway partGateway, WorkOrderPartGateway workOrderPartGateway) {
        return new DeletePartUseCaseImpl(partGateway, workOrderPartGateway);
    }

    @Bean
    public FindPartByIdUseCase findPartByIdUseCase(PartGateway partGateway) {
        return new FindPartByIdUseCaseImpl(partGateway);
    }

    @Bean
    public FindPartsByIdsUseCase findPartsByIdsUseCase(PartGateway partGateway) {
        return new FindPartsByIdsUseCaseImpl(partGateway);
    }

    @Bean
    public UpdatePartUseCase updatePartUseCase(PartGateway partGateway, FindPartByIdUseCase findPartByIdUseCase) {
        return new UpdatePartUseCaseImpl(partGateway, findPartByIdUseCase);
    }
}