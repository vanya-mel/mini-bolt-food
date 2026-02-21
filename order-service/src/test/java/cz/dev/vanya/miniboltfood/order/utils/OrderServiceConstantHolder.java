package cz.dev.vanya.miniboltfood.order.utils;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentMethodDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class OrderServiceConstantHolder {

    public static final Long ORDER_ID = 101L;
    public static final Long ORDER_ITEM_ID = 201L;
    public static final Long SECOND_ORDER_ITEM_ID = 202L;
    public static final Long CUSTOMER_ID = 301L;
    public static final Long PAYMENT_ID = 401L;
    public static final Long ITEM_ID = 501L;
    public static final Long SECOND_ITEM_ID = 502L;

    public static final Integer QUANTITY = 2;
    public static final Integer SECOND_QUANTITY = 3;
    public static final Integer ETA_MINUTES = 25;
    public static final Integer UPDATED_ETA_MINUTES = 35;

    public static final BigDecimal ITEM_PRICE = new BigDecimal("12.34");
    public static final BigDecimal SECOND_ITEM_PRICE = new BigDecimal("56.78");
    public static final BigDecimal ORDER_TOTAL_AMOUNT = new BigDecimal("195.02");

    public static final String DESTINATION_ADDRESS = "221B Baker Street";
    public static final String COURIER_NAME = "Sam Courier";
    public static final String UPDATED_COURIER_NAME = "Alex Courier";
    public static final String ITEM_NAME = "Burger";
    public static final String SECOND_ITEM_NAME = "Fries";

    public static final String ORDER_PAID_TOPIC = "orders.events.it.v1";
    public static final String DELIVERY_ASSIGNED_TOPIC = "delivery.events.it.v1";

    public static final PaymentMethodDto PAYMENT_METHOD_DTO = PaymentMethodDto.CARD;
    public static final PaymentStatusDto PAYMENT_STATUS_DTO = PaymentStatusDto.PAYMENT_SUCCEEDED;

    public static final OrderStatus ORDER_STATUS_PENDING_PAYMENT = OrderStatus.PENDING_PAYMENT;
    public static final OrderStatus ORDER_STATUS_PAID = OrderStatus.PAID;
    public static final OrderStatus ORDER_STATUS_PAYMENT_FAILED = OrderStatus.PAYMENT_FAILED;
}
