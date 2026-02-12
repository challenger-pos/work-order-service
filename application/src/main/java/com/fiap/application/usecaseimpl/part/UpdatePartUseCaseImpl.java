package com.fiap.application.usecaseimpl.part;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.core.domain.part.Part;
import com.fiap.core.exception.NotFoundException;
import com.fiap.usecase.part.FindPartByIdUseCase;
import com.fiap.usecase.part.UpdatePartUseCase;

public class UpdatePartUseCaseImpl implements UpdatePartUseCase {

    private final PartGateway partGateway;
    private final FindPartByIdUseCase findPartByIdUseCase;

    public UpdatePartUseCaseImpl(PartGateway partGateway, FindPartByIdUseCase findPartByIdUseCase) {
        this.partGateway = partGateway;
        this.findPartByIdUseCase = findPartByIdUseCase;
    }

    @Override
    public Part execute(Part partWithUpdates) throws NotFoundException {
        Part existingPart = findPartByIdUseCase.execute(partWithUpdates.getId());

        existingPart.setName(partWithUpdates.getName());
        existingPart.setDescription(partWithUpdates.getDescription());
        existingPart.setPrice(partWithUpdates.getPrice());

        return partGateway.update(existingPart);
    }
}