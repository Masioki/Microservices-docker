package pl.inteca.credit.dto;

import lombok.Data;

/**
 * DTO with format of data returned by Customer microservice
 */
@Data
public class ProductDTO {
    private long creditID;
    private String productName;
    private int value;
}
