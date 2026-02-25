package com.fiap.application.usecaseimpl.service;

import com.fiap.application.gateway.service.ServiceGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceUseCaseImplTest {

    @Mock
    ServiceGateway serviceGateway;

    @Test
    void shouldSendWorkOrderStatusTransitionDurationSuccessfully() {
        long durationInSeconds = 3600L;
        String statusTag = "COMPLETED";

        doNothing().when(serviceGateway).sendWorkOrderStatusTransitionDuration(durationInSeconds, statusTag);

        MetricsServiceUseCaseImpl useCase = new MetricsServiceUseCaseImpl(serviceGateway);
        useCase.execute(durationInSeconds, statusTag);

        InOrder inOrder = inOrder(serviceGateway);
        inOrder.verify(serviceGateway).sendWorkOrderStatusTransitionDuration(durationInSeconds, statusTag);
        verifyNoMoreInteractions(serviceGateway);
    }

    @Test
    void shouldSendMetricsWithDifferentStatusTags() {
        MetricsServiceUseCaseImpl useCase = new MetricsServiceUseCaseImpl(serviceGateway);

        // Test with IN_PROGRESS status
        useCase.execute(1800L, "IN_PROGRESS");
        verify(serviceGateway).sendWorkOrderStatusTransitionDuration(1800L, "IN_PROGRESS");

        // Test with PENDING status
        useCase.execute(900L, "PENDING");
        verify(serviceGateway).sendWorkOrderStatusTransitionDuration(900L, "PENDING");

        // Test with REFUSED status
        useCase.execute(7200L, "REFUSED");
        verify(serviceGateway).sendWorkOrderStatusTransitionDuration(7200L, "REFUSED");
    }

    @Test
    void shouldSendMetricsWithZeroDuration() {
        long zeroDuration = 0L;
        String statusTag = "CREATED";

        doNothing().when(serviceGateway).sendWorkOrderStatusTransitionDuration(zeroDuration, statusTag);

        MetricsServiceUseCaseImpl useCase = new MetricsServiceUseCaseImpl(serviceGateway);
        useCase.execute(zeroDuration, statusTag);

        verify(serviceGateway).sendWorkOrderStatusTransitionDuration(zeroDuration, statusTag);
    }

}
