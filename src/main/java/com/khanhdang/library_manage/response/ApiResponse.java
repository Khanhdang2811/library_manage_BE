package com.khanhdang.library_manage.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.khanhdang.library_manage.exception.ApiStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

//annotation: khi generate json thi cai nao null thi khong can xuat hien trong json
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
      ApiStatus status;
      String message;
      T data;
}
