package cz.dev.vanya.miniboltfood.order.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderServiceEndpointUtils {

    public static final String CREATE_ORDER_ENDPOINT = "/api/v1/orders";
    public static final String GET_ORDER_ENDPOINT = "/api/v1/orders/{id}";
    public static final String PAY_ORDER_ENDPOINT = "/api/v1/orders/{id}/pay";
    public static final String DELIVERY_ORDER_ENDPOINT = "/api/v1/orders/{id}/delivery";
}
