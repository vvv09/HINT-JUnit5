package com.valunskii.labjunit5.dto;

import lombok.Data;
import lombok.Value;

@Data
@Value(staticConstructor = "of")
public class User {
    Integer id;
    String username;
    String password;
}
