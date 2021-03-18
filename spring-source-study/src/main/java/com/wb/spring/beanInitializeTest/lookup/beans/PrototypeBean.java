package com.wb.spring.beanInitializeTest.lookup.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class PrototypeBean {
}
