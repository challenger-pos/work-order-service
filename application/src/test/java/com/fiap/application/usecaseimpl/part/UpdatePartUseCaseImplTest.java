package com.fiap.application.usecaseimpl.part;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.core.domain.part.Part;
import com.fiap.core.exception.NotFoundException;
import com.fiap.usecase.part.FindPartByIdUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePartUseCaseImplTest {

    @Mock
    PartGateway partGateway;

    @Mock
    FindPartByIdUseCase findPartByIdUseCase;

    @Mock
    Part partWithUpdates;

    @Mock
    Part existingPart;

    @Mock
    Part updatedPart;

    //TODO removido por conta do approveStock
//    @Test
//    void shouldUpdateExistingPartWithNewValues() throws NotFoundException {
//        UUID id = UUID.randomUUID();
//        when(partWithUpdates.getId()).thenReturn(id);
//        when(findPartByIdUseCase.execute(id)).thenReturn(existingPart);
//        when(partGateway.update(existingPart)).thenReturn(updatedPart);
//
//        UpdatePartUseCaseImpl useCase = new UpdatePartUseCaseImpl(partGateway, findPartByIdUseCase);
//        Part result = useCase.execute(partWithUpdates);
//
//        assertSame(updatedPart, result);
//
//        InOrder inOrder = inOrder(findPartByIdUseCase, partWithUpdates, existingPart, partGateway);
//        inOrder.verify(findPartByIdUseCase).execute(id);
//        inOrder.verify(partWithUpdates).getName();
//        inOrder.verify(existingPart).setName(any());
//        inOrder.verify(partWithUpdates).getDescription();
//        inOrder.verify(existingPart).setDescription(any());
//        inOrder.verify(partWithUpdates).getPrice();
//        inOrder.verify(existingPart).setPrice(any());
//        inOrder.verify(partWithUpdates).getStock();
//        inOrder.verify(existingPart).setStock(any());
//        inOrder.verify(partGateway).update(existingPart);
//        verifyNoMoreInteractions(findPartByIdUseCase, partWithUpdates, existingPart, partGateway);
//    }

    @Test
    void shouldPropagateNotFoundWhenPartDoesNotExist() throws NotFoundException {
        UUID id = UUID.randomUUID();
        when(partWithUpdates.getId()).thenReturn(id);
        when(findPartByIdUseCase.execute(id)).thenThrow(new NotFoundException("x", "y"));

        UpdatePartUseCaseImpl useCase = new UpdatePartUseCaseImpl(partGateway, findPartByIdUseCase);

        assertThrows(NotFoundException.class, () -> useCase.execute(partWithUpdates));

        verify(findPartByIdUseCase).execute(id);
        verifyNoInteractions(partGateway);
    }
}
