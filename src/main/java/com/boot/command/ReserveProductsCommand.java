package com.boot.command;

import com.boot.order.dto.OrderDTO;
import io.eventuate.tram.commands.common.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReserveProductsCommand implements Command {
    private Long orderId;
    private OrderDTO orderDTO;
    private long customerId;
}
