package com.gustavosoares.rest.core;

import io.restassured.http.ContentType;

public interface Constantes {

    String app_base_url = "https://barrigarest.wcaquino.me/";
    Integer app_port = 443;
    String app_base_path = "";

    ContentType app_content_type = ContentType.JSON;

    Long max_timeout = 5000l;

}
