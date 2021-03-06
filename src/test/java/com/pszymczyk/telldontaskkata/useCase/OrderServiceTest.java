package com.pszymczyk.telldontaskkata.useCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import com.pszymczyk.telldontaskkata.doubles.InMemoryProductCatalog;
import com.pszymczyk.telldontaskkata.doubles.TestOrderRepository;
import com.pszymczyk.telldontaskkata.doubles.TestShipmentService;
import com.pszymczyk.telldontaskkata.entity.Category;
import com.pszymczyk.telldontaskkata.entity.Order;
import com.pszymczyk.telldontaskkata.entity.OrderStatus;
import com.pszymczyk.telldontaskkata.entity.Product;
import com.pszymczyk.telldontaskkata.repository.ProductCatalog;
import com.pszymczyk.telldontaskkata.service.ApprovedOrderCannotBeRejectedException;
import com.pszymczyk.telldontaskkata.service.OrderApprovalRequest;
import com.pszymczyk.telldontaskkata.service.OrderCannotBeShippedException;
import com.pszymczyk.telldontaskkata.service.OrderCannotBeShippedTwiceException;
import com.pszymczyk.telldontaskkata.service.OrderService;
import com.pszymczyk.telldontaskkata.service.OrderServiceImpl;
import com.pszymczyk.telldontaskkata.service.OrderShipmentRequest;
import com.pszymczyk.telldontaskkata.service.RejectedOrderCannotBeApprovedException;
import com.pszymczyk.telldontaskkata.service.SellItemRequest;
import com.pszymczyk.telldontaskkata.service.SellItemsRequest;
import com.pszymczyk.telldontaskkata.service.ShippedOrdersCannotBeChangedException;
import com.pszymczyk.telldontaskkata.service.UnknownProductException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class OrderServiceTest {

    private final TestOrderRepository orderRepository = new TestOrderRepository();
    private Category food = new Category() {{
        setName("food");
        setTaxPercentage(new BigDecimal("10"));
    }};
    private final ProductCatalog productCatalog = new InMemoryProductCatalog(
            Arrays.asList(
                    new Product() {{
                        setName("salad");
                        setPrice(new BigDecimal("3.56"));
                        setCategory(food);
                    }},
                    new Product() {{
                        setName("tomato");
                        setPrice(new BigDecimal("4.65"));
                        setCategory(food);
                    }}
            )
    );
    private final TestShipmentService shipmentService = new TestShipmentService();
    private final OrderService orderService = new OrderServiceImpl(orderRepository, productCatalog, shipmentService);

    @Test
    public void sellMultipleItems() {
        //given
        SellItemRequest saladRequest = new SellItemRequest();
        saladRequest.setProductName("salad");
        saladRequest.setQuantity(2);

        SellItemRequest tomatoRequest = new SellItemRequest();
        tomatoRequest.setProductName("tomato");
        tomatoRequest.setQuantity(3);

        SellItemsRequest request = new SellItemsRequest();
        request.setRequests(new ArrayList<>());
        request.getRequests().add(saladRequest);
        request.getRequests().add(tomatoRequest);

        //when
        orderService.createOrder(request);

        //then
        Order insertedOrder = orderRepository.getSavedOrder();
        assertThat(insertedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(insertedOrder.getTotal()).isEqualTo(new BigDecimal("23.20"));
        assertThat(insertedOrder.getTax()).isEqualTo(new BigDecimal("2.13"));
        assertThat(insertedOrder.getCurrency()).isEqualTo("EUR");
        assertThat(insertedOrder.getItems()).hasSize(2);
        assertThat(insertedOrder.getItems().get(0).getProduct().getName()).isEqualTo("salad");
        assertThat(insertedOrder.getItems().get(0).getProduct().getPrice()).isEqualTo(new BigDecimal("3.56"));
        assertThat(insertedOrder.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(insertedOrder.getItems().get(0).getTaxedAmount()).isEqualTo(new BigDecimal("7.84"));
        assertThat(insertedOrder.getItems().get(0).getTax()).isEqualTo(new BigDecimal("0.72"));
        assertThat(insertedOrder.getItems().get(1).getProduct().getName()).isEqualTo("tomato");
        assertThat(insertedOrder.getItems().get(1).getProduct().getPrice()).isEqualTo(new BigDecimal("4.65"));
        assertThat(insertedOrder.getItems().get(1).getQuantity()).isEqualTo(3);
        assertThat(insertedOrder.getItems().get(1).getTaxedAmount()).isEqualTo(new BigDecimal("15.36"));
        assertThat(insertedOrder.getItems().get(1).getTax()).isEqualTo(new BigDecimal("1.41"));
    }

    @Test
    public void unknownProduct() {
        //given
        SellItemsRequest request = new SellItemsRequest();
        request.setRequests(new ArrayList<>());
        SellItemRequest unknownProductRequest = new SellItemRequest();
        unknownProductRequest.setProductName("unknown product");
        request.getRequests().add(unknownProductRequest);

        //when
        Throwable exception = catchThrowable(() -> orderService.createOrder(request));

        //then
        assertThat(exception).isInstanceOf(UnknownProductException.class);
    }

    @Test
    public void approvedExistingOrder() {
        //given
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.CREATED);
        initialOrder.setId(UUID.randomUUID());
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(initialOrder.getId());
        request.setApproved(true);

        //when
        orderService.approveOrder(request);

        //then
        Order savedOrder = orderRepository.getSavedOrder();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.APPROVED);
    }

    @Test
    public void rejectedExistingOrder() {
        //given
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.CREATED);
        initialOrder.setId(UUID.randomUUID());
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(initialOrder.getId());
        request.setApproved(false);

        //when
        orderService.approveOrder(request);

        //then
        Order savedOrder = orderRepository.getSavedOrder();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.REJECTED);
    }

    @Test
    public void cannotApproveRejectedOrder() {
        //given
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.REJECTED);
        initialOrder.setId(UUID.randomUUID());
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(initialOrder.getId());
        request.setApproved(true);

        //when
        Throwable thrown = catchThrowable(() -> orderService.approveOrder(request));

        //then
        assertThat(thrown).isInstanceOf(RejectedOrderCannotBeApprovedException.class);
    }

    @Test
    public void cannotRejectApprovedOrder() {
        //given
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.APPROVED);
        initialOrder.setId(UUID.randomUUID());
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(initialOrder.getId());
        request.setApproved(false);

        //when
        Throwable thrown = catchThrowable(() -> orderService.approveOrder(request));

        //then
        assertThat(thrown).isInstanceOf(ApprovedOrderCannotBeRejectedException.class);
    }

    @Test
    public void shippedOrdersCannotBeApproved() {
        //given
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.SHIPPED);
        initialOrder.setId(UUID.randomUUID());
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(initialOrder.getId());
        request.setApproved(true);

        //when
        Throwable thrown = catchThrowable(() -> orderService.approveOrder(request));

        //then
        assertThat(thrown).isInstanceOf(ShippedOrdersCannotBeChangedException.class);
    }

    @Test
    public void shippedOrdersCannotBeRejected() {
        //given
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.SHIPPED);
        initialOrder.setId(UUID.randomUUID());
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(initialOrder.getId());
        request.setApproved(false);

        //when
        Throwable thrown = catchThrowable(() -> orderService.approveOrder(request));

        //then
        assertThat(thrown).isInstanceOf(ShippedOrdersCannotBeChangedException.class);
    }

    @Test
    public void shipApprovedOrder() {
        //given
        Order initialOrder = new Order();
        initialOrder.setId(UUID.randomUUID());
        initialOrder.setStatus(OrderStatus.APPROVED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(initialOrder.getId());

        //when
        orderService.shipOrder(request);

        //then
        assertThat(orderRepository.getSavedOrder().getStatus()).isEqualTo((OrderStatus.SHIPPED));
        assertThat(shipmentService.getShippedOrder()).isEqualTo(initialOrder);
    }

    @Test
    public void createdOrdersCannotBeShipped() {
        //given
        Order initialOrder = new Order();
        initialOrder.setId(UUID.randomUUID());
        initialOrder.setStatus(OrderStatus.CREATED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(initialOrder.getId());

        //when
        Throwable thrown = catchThrowable(() -> orderService.shipOrder(request));

        //then
        assertThat(thrown).isInstanceOf(OrderCannotBeShippedException.class);
    }

    @Test
    public void rejectedOrdersCannotBeShipped() {
        //given
        Order initialOrder = new Order();
        initialOrder.setId(UUID.randomUUID());
        initialOrder.setStatus(OrderStatus.REJECTED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(initialOrder.getId());

        //when
        Throwable thrown = catchThrowable(() -> orderService.shipOrder(request));

        //then
        assertThat(thrown).isInstanceOf(OrderCannotBeShippedException.class);
    }

    @Test
    public void shippedOrdersCannotBeShippedAgain() {
        Order initialOrder = new Order();
        initialOrder.setId(UUID.randomUUID());
        initialOrder.setStatus(OrderStatus.SHIPPED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(initialOrder.getId());

        //when
        Throwable thrown = catchThrowable(() -> orderService.shipOrder(request));

        //then
        assertThat(thrown).isInstanceOf(OrderCannotBeShippedTwiceException.class);
    }
}
