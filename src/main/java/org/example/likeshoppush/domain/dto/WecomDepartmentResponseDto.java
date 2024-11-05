package org.example.likeshoppush.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "企业微信部门响应实体", description = "企业微信部门响应")
public class WecomDepartmentResponseDto {

    private Integer errcode;
    private String errmsg;
    private List<Department> department;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Department {
        private Integer id;
        private String name;
    }
}
