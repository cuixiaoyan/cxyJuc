package com.cxy.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-07 15:52
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class cxyUser {
    private Integer id;
    private String name;
    private Integer age;
}